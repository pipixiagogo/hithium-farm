package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.AreaInfoBo;
import com.bugull.hithiumfarmweb.http.bo.AreaNetInNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bugull.hithiumfarmweb.common.Const.*;

@Service
public class StatisticsService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private BreakDownLogDao breakDownLogDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private IncomeEntityDao incomeEntityDao;
    @Resource
    private EssStationDao essStationDao;
    @Resource
    private BamsDischargeCapacityDao bamsDischargeCapacityDao;
    public static final String INCOME_RECORD_PREFIX = "DEIVCE_INCOME_RECORED_";
    protected static final Map<Integer, String> QUARTER_NAME = new HashMap<>();

    static {
        QUARTER_NAME.put(1, "第一季度");
        QUARTER_NAME.put(2, "第二季度");
        QUARTER_NAME.put(3, "第三季度");
        QUARTER_NAME.put(4, "第四季度");
    }

    public ResHelper<List<StatisticBo>> statisticNetInNum(AreaNetInNumBo areaNetInNumBo, SysUser user) {
        Calendar calendar = Calendar.getInstance();
        // 获取当前年
        int year = calendar.get(Calendar.YEAR);
        if (areaNetInNumBo.getYear() > year) {
            return ResHelper.pamIll();
        }
        Date startTime = getStartTimeOfYear(areaNetInNumBo.getYear());
        Date endTime = getEndTimeOfYear(areaNetInNumBo.getYear());
        Criteria criteria = new Criteria();
        criteria.and("accessTime").gte(startTime).lte(endTime);
        if (!StringUtils.isEmpty(areaNetInNumBo.getProvince())) {
            if (!propertiesConfig.getProToCitys().containsKey(areaNetInNumBo.getProvince())) {
                return ResHelper.pamIll();
            }
            if (!StringUtils.isEmpty(areaNetInNumBo.getCity()) && !propertiesConfig.getProToCitys().get(areaNetInNumBo.getProvince()).contains(areaNetInNumBo.getCity())) {
                return ResHelper.pamIll();
            }
            criteria.and(PROVINCE).is(areaNetInNumBo.getProvince());
        }
        if (!StringUtils.isEmpty(areaNetInNumBo.getCity())) {
            if (!propertiesConfig.getCitys().containsKey(areaNetInNumBo.getCity())
                    || !propertiesConfig.getCitys().get(areaNetInNumBo.getCity()).equals(areaNetInNumBo.getProvince())) {
                return ResHelper.pamIll();
            }
            criteria.and("city").is(areaNetInNumBo.getCity());
        }
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            Query essStationQuery = new Query();
            essStationQuery.addCriteria(Criteria.where("_id").in(user.getStationList()));
            List<EssStation> essStationList = essStationDao.findEssStationBatch(essStationQuery);
            if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
        }
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.project().andExpression("{ $dateToString: { format: '%Y-%m', date: '$accessTime' } }").as("time")
                        .andExpression("{$toDouble:'$stationPower'}").as("stationPower"),
                Aggregation.group("time").sum("stationPower").as("count")
        );
        List<BasicDBObject> dbObjectList = deviceDao.aggregation(aggregation);

        Map<String, StatisticBo> map = getDefaultBo(areaNetInNumBo.getYear());
        if (!CollectionUtils.isEmpty(dbObjectList) && !dbObjectList.isEmpty()) {
            for (DBObject obj : dbObjectList) {
                String name = obj.get("_id").toString();
                String count = obj.get("count").toString();
                String decimal = new BigDecimal(count).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
                Double countDouble = Double.valueOf(decimal);
                StatisticBo bo = map.get(name);
                if (bo != null) {
                    bo.setCount(countDouble);
                }
            }
        }

        List<StatisticBo> aim = new ArrayList<>(map.values());
        Collections.sort(aim, (x, y) -> Integer.parseInt(x.getDate()) - Integer.parseInt(y.getDate()));
        List<StatisticBo> result = new ArrayList<>(map.values().size());
        int month = calendar.get(Calendar.MONTH) + 1;
        if (areaNetInNumBo.getYear() == year) {
            for (int i = 1; i <= month; i++) {
                result.add(getStatisticBo(aim, i));
            }
        } else {
            for (int i = 1; i <= aim.size(); i++) {
                result.add(getStatisticBo(aim, i));
            }
        }
        if (result.size() != aim.size()) {
            for (int i = result.size() + 1; i <= aim.size(); i++) {
                result.add(new StatisticBo(String.valueOf(i), 0D));
            }
        }
        // 获取当前月
        if (MONTH.equals(areaNetInNumBo.getType())) {
            return ResHelper.success("", result);
        } else {
            return ResHelper.pamIll();
        }
    }

    private StatisticBo getStatisticBo(List<StatisticBo> aim, int i) {
        List<StatisticBo> statisticBos = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            statisticBos.add(aim.get(j));
        }
        double sum = statisticBos.stream().mapToDouble(StatisticBo::getCount).sum();
        StatisticBo statisticBo = new StatisticBo();
        statisticBo.setCount(sum);
        statisticBo.setDate(String.valueOf(i));
        return statisticBo;
    }


    private Map<String, StatisticBo> getDefaultBo(Integer year) {
        Map<String, StatisticBo> map = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(year + "-" + (i > 9 ? i : "0" + i), new StatisticBo(i + "", 0D));
        }
        return map;
    }

    private Date getEndTimeOfYear(Integer year) {
        Calendar yearEnd = Calendar.getInstance();
        yearEnd.set(Calendar.YEAR, year + 1);
        yearEnd.set(Calendar.MONTH, 0);
        yearEnd.set(Calendar.DATE, 1);
        yearEnd.set(Calendar.HOUR_OF_DAY, 0);
        yearEnd.set(Calendar.MINUTE, 0);
        yearEnd.set(Calendar.SECOND, 0);
        yearEnd.set(Calendar.MILLISECOND, -1);
        return yearEnd.getTime();
    }

    private Date getStartTimeOfYear(Integer year) {
        Calendar yearStart = Calendar.getInstance();
        yearStart.set(Calendar.YEAR, year);
        yearStart.set(Calendar.MONTH, 0);
        yearStart.set(Calendar.DATE, 1);
        yearStart.set(Calendar.HOUR_OF_DAY, 0);
        yearStart.set(Calendar.MINUTE, 0);
        yearStart.set(Calendar.SECOND, 0);
        yearStart.set(Calendar.MILLISECOND, 0);
        return yearStart.getTime();
    }

    public ResHelper<CapacityNumVo> statisticCapacityNum(AreaInfoBo areaInfoBo, SysUser user) {
        Criteria criteria = new Criteria();
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
        }
        Aggregation chargeAggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.project().andExpression("{$toDouble:'$chargeCapacitySum'}").as("chargeCapacitySum"),
                Aggregation.group().sum("chargeCapacitySum").as("count"),
                Aggregation.limit(1));
        Aggregation disChargeAggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.project().andExpression("{$toDouble:'$dischargeCapacitySum'}").as("dischargeCapacitySum"),
                Aggregation.group().sum("dischargeCapacitySum").as("count"),
                Aggregation.limit(1));
        List<BasicDBObject> chargeAggregationResult = deviceDao.aggregation(chargeAggregation);
        List<BasicDBObject> disChargeAggregationResult = deviceDao.aggregation(disChargeAggregation);
        CapacityNumVo capacityNumBo = new CapacityNumVo();
        if (!CollectionUtils.isEmpty(chargeAggregationResult) && !chargeAggregationResult.isEmpty()) {
            for (DBObject object : chargeAggregationResult) {
                Double sum = Double.valueOf(object.get("count").toString());
                BigDecimal bigDecimal = BigDecimal.valueOf(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
                capacityNumBo.setChargeCapacitySum(bigDecimal.toString());
            }
        }
        if (!CollectionUtils.isEmpty(disChargeAggregationResult) && !disChargeAggregationResult.isEmpty()) {
            for (DBObject object : disChargeAggregationResult) {
                Double sum = Double.valueOf(object.get("count").toString());
                BigDecimal bigDecimal = BigDecimal.valueOf(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
                capacityNumBo.setDischargeCapacitySum(bigDecimal.toString());
            }
        }
        BigDecimal totalChargeSum = new BigDecimal(capacityNumBo.getChargeCapacitySum()).add(new BigDecimal(capacityNumBo.getWindEnergyChargeSum())).add(new BigDecimal(capacityNumBo.getPhotovoltaicChargeSum())).setScale(2, BigDecimal.ROUND_HALF_UP);
        capacityNumBo.setTotalChargeSum(totalChargeSum.toString());
        return ResHelper.success("", capacityNumBo);
    }


    public ResHelper<AlarmNumVo> statisticAlarmNum(AreaInfoBo areaInfoBo, SysUser user) {
        if (areaInfoBo != null) {
            Criteria criteria = new Criteria();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity()) ||
                        !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
            return ResHelper.success("", getAlarmNumVo(criteria, areaInfoBo));
        }
        return ResHelper.pamIll();


    }

    private AlarmNumVo getAlarmNumVo(Criteria criteria, AreaInfoBo areaInfoBo) {
        return createAlarmNumVo(criteria, areaInfoBo);
    }


    private AlarmNumVo createAlarmNumVo(Criteria criteria, AreaInfoBo areaInfoBo) {
        AlarmNumVo alarmNumVo = new AlarmNumVo();
        if (areaInfoBo != null) {
            alarmNumVo.setCity(areaInfoBo.getCity());
            alarmNumVo.setProvince(areaInfoBo.getProvince());
        }
        criteria.and("status").is(true);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("status").count().as("count"));
        List<BasicDBObject> basicDBObjects = breakDownLogDao.aggregation(aggregation);
        if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
            for (DBObject object : basicDBObjects) {
                Integer count = (Integer) object.get("count");
                alarmNumVo.setNumber(count);
            }
        }
        alarmNumVo.setCountry(COUNTRY);
        return alarmNumVo;
    }

    public ResHelper<Map<String, BigDecimal>> saveEnergyNum(AreaInfoBo areaInfoBo, SysUser user) {
        Criteria criteria = new Criteria();
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
        }
        Map<String, BigDecimal> resultDecimal = new HashMap<>();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project().andExpression("{$toDouble:'$chargeCapacitySum'}").as("chargeCapacitySum"),
                Aggregation.group().sum("chargeCapacitySum").as("count")
        );
        List<BasicDBObject> chargeAggregation = deviceDao.aggregation(aggregation);
        if (!CollectionUtils.isEmpty(chargeAggregation) && !chargeAggregation.isEmpty()) {
            for (DBObject object : chargeAggregation) {
                Double sum = Double.valueOf(object.get("count").toString());
                BigDecimal bigDecimal = BigDecimal.valueOf(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
                resultDecimal.put(CARBON_DIOXIDE, bigDecimal.multiply(new BigDecimal(0.9590)));
                resultDecimal.put(SULFUR_DIOXIDE, bigDecimal.multiply(new BigDecimal(0.3619).multiply(new BigDecimal(177.9))));
                resultDecimal.put(NOX, bigDecimal.multiply(new BigDecimal(0.3619).multiply(new BigDecimal(87.7))));
                resultDecimal.put(SMOKE, bigDecimal.multiply(new BigDecimal(0.3619).multiply(new BigDecimal(48.4))));
            }
        }
        return ResHelper.success("", resultDecimal);
    }

    public ResHelper<RunNumVO> runNumStatistic(AreaInfoBo areaInfoBo, SysUser user) {
//        List<Criteria> criteriaList = new ArrayList<>();
//        if (areaInfoBo != null) {
//            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
//                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
//                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
//                    return ResHelper.pamIll();
//                }
//                criteriaList.add(Criteria.where(PROVINCE).is(areaInfoBo.getProvince()));
//            }
//            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
//                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())) {
//                    return ResHelper.pamIll();
//                }
//                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
//                    return ResHelper.pamIll();
//                }
//                criteriaList.add(Criteria.where("city").is(areaInfoBo.getCity()));
//            }
//            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
//                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
//                List<String> deviceNames = new ArrayList<>();
//                for (EssStation essStation : essStationList) {
//                    List<String> deviceNameList = essStation.getDeviceNameList();
//                    deviceNames.addAll(deviceNameList);
//                }
//                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
//                    criteriaList.add(Criteria.where(DEVICE_NAME).in(deviceNames));
//                }
//            }
//        }
//        Query deviceQuery = new Query().addCriteria(new Criteria().andOperator(criteriaList));
//        long count =deviceDao.count(deviceQuery);
//
//        List<String> deviceNameStrList = deviceDao.findBatchDevices(deviceQuery).stream().map(Device::getDeviceName).collect(Collectors.toList());
//        Date date = DateUtils.addDateMinutes(new Date(), -30);
//        int runNum = 0;
//        if (!CollectionUtils.isEmpty(deviceNameStrList) && !deviceNameStrList.isEmpty()) {
//            //设备总数
//            //运行设备数量
//            Iterable<DBObject> iterable = bamsDataDicBADao.aggregate()
//                    .match(deviceDao.query().in(DEVICE_NAME, deviceNameStrList))
//                    .match(bamsDataDicBADao.query().greaterThanEquals("generationDataTime", date)).group("{_id:'$deviceName'}").results();
//            if (iterable != null) {
//                while (iterable.iterator().hasNext()) {
//                    runNum++;
//                }
//            }
//        }
//        RunNumVO runNumVO = new RunNumVO();
//        runNumVO.setRunNum(runNum);
//        runNumVO.setNotRunNum(Integer.valueOf((int) count - runNum));
//        return ResHelper.success("", runNumVO);
        return null;
    }

    public ResHelper<IncomeStatisticVo> incomeStatistic(AreaInfoBo areaInfoBo, SysUser user) {
//        BuguQuery<Device> query = deviceDao.query().existsField("income");
//
//        IncomeStatisticVo incomeStatisticVo = new IncomeStatisticVo();
//        if (areaInfoBo != null) {
//            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
//                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
//                    return ResHelper.pamIll();
//                }
//                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
//                    return ResHelper.pamIll();
//                }
//                query.is(PROVINCE, areaInfoBo.getProvince());
//                incomeStatisticVo.setProvince(areaInfoBo.getProvince());
//            }
//            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
//                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
//                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
//                    return ResHelper.pamIll();
//                }
//                query.is("city", areaInfoBo.getCity());
//                incomeStatisticVo.setCity(areaInfoBo.getCity());
//            }
//            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
//                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
//                List<String> deviceNames = new ArrayList<>();
//                for (EssStation essStation : essStationList) {
//                    List<String> deviceNameList = essStation.getDeviceNameList();
//                    deviceNames.addAll(deviceNameList);
//                }
//                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
//                    query.in(DEVICE_NAME, deviceNames);
//                }
//            }
//        }
//        DBObject group = new BasicDBObject();
//        group.put("_id", null);
//        DBObject toDouble = new BasicDBObject();
//        toDouble.put("$toDouble", "$income");
//        DBObject dbObject = new BasicDBObject("$sum", toDouble);
//        group.put("count", dbObject);
//        Iterable<DBObject> iterable = deviceDao.aggregate().match(query).group(group).results();
//        BigDecimal bigDecimal = new BigDecimal("0");
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                Double income = (Double) object.get("count");
//                bigDecimal = bigDecimal.add(BigDecimal.valueOf(income).setScale(2, BigDecimal.ROUND_HALF_UP));
//            }
//        }
//        incomeStatisticVo.setIncome(bigDecimal.toString());
//        return ResHelper.success("", incomeStatisticVo);
        return null;
    }

    public ResHelper<Map<String, IncomeStatisticVo>> incomeStatistics(AreaInfoBo areaInfoBo, SysUser user) {

        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(4);
        IncomeStaticsOfMongo g1 = new IncomeStaticsOfMongo(countDownLatch, "DAY", areaInfoBo, user);
        executorService.execute(g1);
        IncomeStaticsOfMongo g2 = new IncomeStaticsOfMongo(countDownLatch, "MONTH", areaInfoBo, user);
        executorService.execute(g2);
        IncomeStaticsOfMongo g3 = new IncomeStaticsOfMongo(countDownLatch, "YEAR", areaInfoBo, user);
        executorService.execute(g3);
        IncomeStaticsOfMongo g4 = new IncomeStaticsOfMongo(countDownLatch, "SAFE_DAYS", areaInfoBo, user);
        executorService.execute(g4);
        IncomeStaticsOfMongo g5 = new IncomeStaticsOfMongo(countDownLatch, TOTAL, areaInfoBo, user);
        executorService.execute(g5);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, IncomeStatisticVo> resultMap = new HashMap<>();
        resultMap.put(DAY, g1.getIncomeStatisticVo());
        resultMap.put(MONTH, g2.getIncomeStatisticVo());
        resultMap.put(YEAR, g3.getIncomeStatisticVo());
        resultMap.put(SAFE_OF_DAY, g4.getIncomeStatisticVo());
        resultMap.put(TOTAL, g5.getIncomeStatisticVo());
        return ResHelper.success("", resultMap);
    }


    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> incomeStatisticOfAll(AreaInfoBo areaInfoBo, SysUser user) {
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        IncomeStaticsOfDb incomeStaticsOfDb1 = new IncomeStaticsOfDb(countDownLatch, areaInfoBo, INCOME_OF_DAY, 7, user);
        executorService.execute(incomeStaticsOfDb1);
        IncomeStaticsOfDb incomeStaticsOfDb2 = new IncomeStaticsOfDb(countDownLatch, areaInfoBo, INCOME_OF_MONTH, 6, user);
        executorService.execute(incomeStaticsOfDb2);
        IncomeStaticsOfDb incomeStaticsOfDb3 = new IncomeStaticsOfDb(countDownLatch, areaInfoBo, INCOME_OF_YEAR, 5, user);
        executorService.execute(incomeStaticsOfDb3);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, List<IncomeStatisticOfDayVo>> map = new HashMap<>();
        map.put(DAY, incomeStaticsOfDb1.getIncomeStatisticOfDayVos());
        map.put(MONTH, incomeStaticsOfDb2.getIncomeStatisticOfDayVos());
        map.put(YEAR, incomeStaticsOfDb3.getIncomeStatisticOfDayVos());
        return ResHelper.success("", map);
    }

    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> deviceOfIncomeStatistic(String deviceName) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        IncomeOfDevice incomeOfDevice1 = new IncomeOfDevice(countDownLatch, deviceName, DAY, 7);
        executorService.execute(incomeOfDevice1);
        IncomeOfDevice incomeOfDevice2 = new IncomeOfDevice(countDownLatch, deviceName, MONTH, 6);
        executorService.execute(incomeOfDevice2);
        IncomeOfDevice incomeOfDevice3 = new IncomeOfDevice(countDownLatch, deviceName, YEAR, 5);
        executorService.execute(incomeOfDevice3);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, List<IncomeStatisticOfDayVo>> map = new HashMap<>();
        map.put(DAY, incomeOfDevice1.getIncomeStatisticOfDayVos());
        map.put(MONTH, incomeOfDevice2.getIncomeStatisticOfDayVos());
        map.put(YEAR, incomeOfDevice3.getIncomeStatisticOfDayVos());
        return ResHelper.success("", map);
    }

    public ResHelper<Map<String, List<CapacityVo>>> capacityNumOfDate(SysUser user, AreaInfoBo areaInfoBo) {
        Criteria criteria = new Criteria();
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
        }
        List<String> deviceNameList = deviceDao.findBatchDevices(new Query(criteria)).stream().map(Device::getDeviceName).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        CapacityOfDevice capacityOfDevice1 = new CapacityOfDevice(countDownLatch, DISCHARGECAPACITY_HOUR, deviceNameList, 24, "HOUR");
        executorService.execute(capacityOfDevice1);
        CapacityOfDevice capacityOfDevice2 = new CapacityOfDevice(countDownLatch, DISCHARGECAPACITY_DAY, deviceNameList, 7, DAY);
        executorService.execute(capacityOfDevice2);
        CapacityOfDevice capacityOfDevice3 = new CapacityOfDevice(countDownLatch, DISCHARGECAPACITY_MONTH, deviceNameList, 12, MONTH);
        executorService.execute(capacityOfDevice3);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, List<CapacityVo>> map = new HashMap<>();
        map.put("HOUR", capacityOfDevice1.getCapacityVos());
        map.put("DAY", capacityOfDevice2.getCapacityVos());
        map.put("MONTH", capacityOfDevice3.getCapacityVos());
        return ResHelper.success("", map);
    }

    public ResHelper<ChargeCapacityStationVo> capacityNumOfStation(String stationId) {
        ChargeCapacityStationVo chargeCapacityStationVo = new ChargeCapacityStationVo();
        EssStation essStation = essStationDao.findEssStation(new Query().addCriteria(Criteria.where("_id").is(stationId)));
        if (essStation != null) {
            if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                List<Device> deviceList = deviceDao.findBatchDevices(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(essStation.getDeviceNameList())));
                List<BigDecimal> chargeCapacitySumList = deviceList.stream().map(device -> new BigDecimal(device.getChargeCapacitySum())).collect(Collectors.toList());
                BigDecimal chargeCapacityDecimal = chargeCapacitySumList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                List<BigDecimal> disChargeCapacitySumList = deviceList.stream().map(device -> new BigDecimal(device.getDischargeCapacitySum())).collect(Collectors.toList());
                BigDecimal disChargeCapacityDecimal = disChargeCapacitySumList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                chargeCapacityStationVo.setChargeCapacity(chargeCapacityDecimal.toString());
                chargeCapacityStationVo.setDisChargeCapacity(disChargeCapacityDecimal.toString());
                chargeCapacityStationVo.setStationName(essStation.getStationName());
                getTotalStationIncome(essStation.getDeviceNameList(), chargeCapacityStationVo);
                getDayStationIncome(essStation.getDeviceNameList(), chargeCapacityStationVo);
                return ResHelper.success("", chargeCapacityStationVo);
            }
            return ResHelper.error("该电站未绑定设备");
        }
        return ResHelper.error("该电站不存在");

    }

    private void getDayStationIncome(List<String> deviceNameList, ChargeCapacityStationVo chargeCapacityStationVo) {
        String dateToStr = DateUtils.dateToStr(new Date());
        Query incomeQuery = new Query();
        incomeQuery.addCriteria(new Criteria().and(DEVICE_NAME).in(deviceNameList).and("incomeOfDay").is(dateToStr));
        List<IncomeEntity> incomeEntities = incomeEntityDao.findBatchIncome(incomeQuery);
        BigDecimal incomeBigDecimal = new BigDecimal(0);
        for (IncomeEntity incomeEntity : incomeEntities) {
            incomeBigDecimal = incomeBigDecimal.add(incomeEntity.getIncome().setScale(4, BigDecimal.ROUND_HALF_UP));
        }
        chargeCapacityStationVo.setDayStationIncome(incomeBigDecimal.toString());
    }

    private void getTotalStationIncome(List<String> deviceNameList, ChargeCapacityStationVo chargeCapacityStationVo) {
        Criteria criteria = new Criteria().and(DEVICE_NAME).in(deviceNameList);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.project().andExpression("{$toDouble:'$income'}").as("income"),
                Aggregation.group().sum("income").as("count"),
                Aggregation.limit(1));

        List<BasicDBObject> basicDBObjects = incomeEntityDao.aggregate(aggregation);
        if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
            for (DBObject object :basicDBObjects) {
                Double count = (Double) object.get("count");
                BigDecimal incomeBigDecimal = BigDecimal.valueOf(count).setScale(4, BigDecimal.ROUND_HALF_UP);
                chargeCapacityStationVo.setTotalStationIncome(incomeBigDecimal.toString());
            }
        }
    }


    class CapacityOfDevice implements Runnable {
        private CountDownLatch countDownLatch;
        private String groupMsg;
        private Integer limit;
        private String type;
        private Map<String, CapacityVo> initData;
        private List<String> deviceNameList;
        private List<CapacityVo> capacityVos;

        public CapacityOfDevice(CountDownLatch countDownLatch, String groupMsg, List<String> deviceNameList, Integer limit, String type) {
            this.countDownLatch = countDownLatch;
            this.limit = limit;
            this.groupMsg = groupMsg;
            this.type = type;
            this.deviceNameList = deviceNameList;
        }

        public List<CapacityVo> getCapacityVos() {
            return capacityVos;
        }


        @Override
        public void run() {
            try {
                if (type.equals("HOUR")) {
                    initHoursMapData();
                } else if (type.equals("DAY")) {
                    initDayMapData();
                } else {
                    initMonthMapData();
                }
            } finally {
                countDownLatch.countDown();
            }
        }

        private void initHoursMapData() {
            this.initData = new TreeMap<>();
            for (int i = 0; i < limit; i++) {
                String key = i > 9 ? String.valueOf(i) : "0" + i;
                initData.put(key, new CapacityVo(key, "0"));
            }
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                criteria.and(DEVICE_NAME).in(deviceNameList);
            }
            criteria.and("generationDataTime").gte(DateUtils.getStartTime(new Date())).lte(DateUtils.getEndTime(new Date()));
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                    Aggregation.project().andExpression("{$dateToString:{format:'%Y-%m-%d %H',date: {$add:{'$generationDataTime',28800000}}}}").as("_id")
                            .andExpression("{$toDouble:'$dischargeCapacitySubtract'}").as("dischargeCapacitySubtract"),
                    Aggregation.group("_id").sum("dischargeCapacitySubtract").as("count"),
                    Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                    Aggregation.limit(limit)
            );
            List<BasicDBObject> basicDBObjects = bamsDischargeCapacityDao.aggregation(aggregation);
            if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
                for (DBObject object : basicDBObjects) {
                    String[] id = object.get("_id").toString().split(" ");
                    CapacityVo capacityVo = initData.get(id[1]);
                    if (capacityVo != null) {
                        capacityVo.setNumber(BigDecimal.valueOf(Double.valueOf(object.get("count").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    }
                }
            }
            List<CapacityVo> capacityVos = new ArrayList<>(initData.values());
            Collections.sort(capacityVos, new Comparator<CapacityVo>() {
                @Override
                public int compare(CapacityVo o1, CapacityVo o2) {
                    return Integer.parseInt(o1.getDate()) - Integer.parseInt(o2.getDate());
                }
            });
            this.capacityVos = capacityVos;
        }

        private void initMonthMapData() {
            this.initData = new HashMap<>();
            Calendar c = Calendar.getInstance();
            for (int i = limit - 1; i >= 0; i--) {
                c.setTime(new Date());
                c.add(Calendar.MONTH, -i);
                String key = DateUtils.formatYYYYMM(c.getTime());
                initData.put(key, new CapacityVo(key, "0"));
            }
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                criteria.and(DEVICE_NAME).in(deviceNameList);
            }
            criteria.and("generationDataTime").gte(DateUtils.getPreviousYearStartTime(new Date())).lte(DateUtils.getEndTime(new Date()));
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                    Aggregation.project().andExpression("{$dateToString:{format:'%Y-%m',date: {$add:{'$generationDataTime',28800000}}}}").as("_id")
                            .andExpression("{$toDouble:'$dischargeCapacitySubtract'}").as("dischargeCapacitySubtract"),
                    Aggregation.group("_id").sum("dischargeCapacitySubtract").as("count"),
                    Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                    Aggregation.limit(limit)
            );
            List<BasicDBObject> basicDBObjects = bamsDischargeCapacityDao.aggregation(aggregation);
            if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
                for (DBObject object : basicDBObjects) {
                    String id = object.get("_id").toString();
                    CapacityVo capacityVo = initData.get(id);
                    if (capacityVo != null) {
                        capacityVo.setNumber(BigDecimal.valueOf(Double.valueOf(object.get("count").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    }
                }
            }
            List<CapacityVo> capacityVos = new ArrayList<>(initData.values());
            Collections.sort(capacityVos, new Comparator<CapacityVo>() {
                @Override
                public int compare(CapacityVo o1, CapacityVo o2) {
                    String[] splitVo1 = o1.getDate().split("-");
                    String[] splitVo2 = o2.getDate().split("-");
                    if (splitVo1[0].equals(splitVo2[0])) {
                        return Integer.parseInt(splitVo1[1]) - Integer.parseInt(splitVo2[1]);
                    }
                    return Integer.parseInt(splitVo1[0]) - Integer.parseInt(splitVo2[0]);
                }
            });
            this.capacityVos = capacityVos;
        }

        private void initDayMapData() {
            this.initData = new HashMap<>();
            Date dayOfSevenAgo = null;
            for (int i = 0; i < limit; i++) {
                Date startTimeOfDay = DateUtils.getStartTime(new Date());
                Calendar cal = Calendar.getInstance();
                cal.setTime(startTimeOfDay);
                cal.add(Calendar.DAY_OF_YEAR, -i);
                startTimeOfDay = cal.getTime();
                dayOfSevenAgo = cal.getTime();
                String key = DateUtils.dateToStrMMDD(startTimeOfDay);
                initData.put(key, new CapacityVo(key, "0"));
            }
//            public static final String DISCHARGECAPACITY_MONTH="{_id:{$dateToString:{format:'%Y-%m',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}";
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                criteria.and(DEVICE_NAME).in(deviceNameList);
            }

            criteria.and("generationDataTime").gte(dayOfSevenAgo).lte(DateUtils.getEndTime(new Date()));
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                    Aggregation.project().andExpression("{$dateToString:{format:'%Y-%m',date: {$add:{'$generationDataTime',28800000}}}}").as("_id")
                            .andExpression("{$toDouble:'$dischargeCapacitySubtract'}").as("dischargeCapacitySubtract"),
                    Aggregation.group("_id").sum("dischargeCapacitySubtract").as("count"),
                    Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                    Aggregation.limit(limit)
            );
            List<BasicDBObject> basicDBObjects = bamsDischargeCapacityDao.aggregation(aggregation);
            if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
                for (DBObject object : basicDBObjects) {
                    String[] id = object.get("_id").toString().split(" ");
                    CapacityVo capacityVo = initData.get(id[1]);
                    if (capacityVo != null) {
                        capacityVo.setNumber(BigDecimal.valueOf(Double.valueOf(object.get("count").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    }
                }
            }
            List<CapacityVo> capacityVos = new ArrayList<>(initData.values());
            Collections.sort(capacityVos, new Comparator<CapacityVo>() {
                @Override
                public int compare(CapacityVo o1, CapacityVo o2) {
                    String[] splitOne = o1.getDate().split("-");
                    String[] splitTwo = o2.getDate().split("-");
                    if (splitOne[0].equals(splitTwo[0])) {
                        return Integer.parseInt(splitOne[1]) - Integer.parseInt(splitTwo[1]);
                    }
                    if (new BigDecimal(Integer.parseInt(splitOne[0])).subtract(new BigDecimal(Integer.parseInt(splitTwo[0]))).abs().compareTo(new BigDecimal(11)) == 0) {
                        return 1;
                    }
                    return Integer.parseInt(splitOne[0]) - Integer.parseInt(splitTwo[0]);
                }
            });
            this.capacityVos = capacityVos;
        }
    }


    class IncomeOfDevice implements Runnable {
        private CountDownLatch countDownLatch;
        private String deviceName;
        private Aggregation aggregation;
        private String groupMsg;
        private Integer limit;
        private Criteria criteria;
        private List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();

        public List<IncomeStatisticOfDayVo> getIncomeStatisticOfDayVos() {
            return this.incomeStatisticOfDayVos;
        }

        public IncomeOfDevice(CountDownLatch countDownLatch, String deviceName, String groupMsg, Integer limit) {
            this.countDownLatch = countDownLatch;
            this.deviceName = deviceName;
            this.limit = limit;
            this.groupMsg = groupMsg;
            this.criteria = new Criteria().and(DEVICE_NAME).is(deviceName);
            initIncomeStatics();
        }

        private void initIncomeStatics() {
            if (groupMsg.equals(DAY)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{$toDouble:'$income'}").as("income").andExpression("'$incomeOfDay'").as("day"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            } else if (groupMsg.equals(MONTH)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYYMM(DateUtils.addDateMonths(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{substr($incomeOfDay,0,7)}").as("day")
                                .andExpression("{$toDouble:'$income'}").as("income"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            } else if (groupMsg.equals(YEAR)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYY(DateUtils.addDateYears(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{substr(incomeOfDay,0,4)}").as("day")
                                .andExpression("{$toDouble:'$income'}").as("income"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            }
        }

        @Override
        public void run() {
            try {
                List<BasicDBObject> aggregate = incomeEntityDao.aggregate(aggregation);
                if (!CollectionUtils.isEmpty(aggregate) && !aggregate.isEmpty()) {
                    List<IncomeStatisticOfDayVo> resultStatisticOfDayList = new ArrayList<>();
                    for (DBObject object : aggregate) {
                        String incomeOfDay = (String) object.get("_id");
                        Double count = Double.valueOf(object.get("count").toString());
                        IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                        incomeStatisticOfDayVo.setIncome(BigDecimal.valueOf(count).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        incomeStatisticOfDayVo.setDay(incomeOfDay);
                        resultStatisticOfDayList.add(incomeStatisticOfDayVo);
                    }
                    if (!CollectionUtils.isEmpty(resultStatisticOfDayList) && !resultStatisticOfDayList.isEmpty()) {
                        for (IncomeStatisticOfDayVo incomeStatisticOfDayVo : incomeStatisticOfDayVos) {
                            for (IncomeStatisticOfDayVo statisticOfDayVo : resultStatisticOfDayList) {
                                if (incomeStatisticOfDayVo.getDay().equals(statisticOfDayVo.getDay())) {
                                    incomeStatisticOfDayVo.setIncome(statisticOfDayVo.getIncome());
                                }
                            }
                        }
                    }

                }
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    class IncomeStaticsOfDb implements Runnable {
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private String aggregateMsg;
        private Integer limit;
        private SysUser user;
        private Criteria criteria;
        private Aggregation aggregation;
        private List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();

        public IncomeStaticsOfDb(CountDownLatch countDownLatch, AreaInfoBo areaInfoBo, String aggregateMsg, Integer limit, SysUser user) {
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            this.aggregateMsg = aggregateMsg;
            this.limit = limit;
            this.user = user;
            getCriteriaList();
            initIncomeStatics();
        }

        public List<IncomeStatisticOfDayVo> getIncomeStatisticOfDayVos() {
            return this.incomeStatisticOfDayVos;
        }

        private void initIncomeStatics() {
            if (aggregateMsg.equals(INCOME_OF_DAY)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{$toDouble:'$income'}").as("income")
                        .andExpression("'$incomeOfDay'").as("day"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            } else if (aggregateMsg.equals(INCOME_OF_MONTH)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYYMM(DateUtils.addDateMonths(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{substr(incomeOfDay,0,7)}").as("day")
                                .andExpression("{$toDouble:'$income'}").as("income"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            } else if (aggregateMsg.equals(INCOME_OF_YEAR)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYY(DateUtils.addDateYears(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
                this.aggregation = Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.project().andExpression("{substr(incomeOfDay,0,4)}").as("day")
                                .andExpression("{$toDouble:'$income'}").as("income"),
                        Aggregation.group("day").sum("income").as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                        Aggregation.limit(limit)
                );
            }
        }

        private void getCriteriaList() {
            this.criteria=new Criteria();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
               criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query().addCriteria(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                }
            }
        }

        @Override
        public void run() {
            try {
                List<BasicDBObject> aggregate = incomeEntityDao.aggregate(aggregation);
                if (!CollectionUtils.isEmpty(aggregate) && !aggregate.isEmpty()) {
                    List<IncomeStatisticOfDayVo> incomeStatisticOfDayVoList = new ArrayList<>();
                    for (DBObject object : aggregate) {
                        IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                        String incomeOfDay = (String) object.get("_id");
                        Double count = (Double) object.get("count");
                        BigDecimal decimal = BigDecimal.valueOf(count);
                        incomeStatisticOfDayVo.setDay(incomeOfDay);
                        incomeStatisticOfDayVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        incomeStatisticOfDayVoList.add(incomeStatisticOfDayVo);
                    }
                    if (!CollectionUtils.isEmpty(incomeStatisticOfDayVoList) && !incomeStatisticOfDayVoList.isEmpty()) {
                        for (IncomeStatisticOfDayVo incomeStatisticOfDayVo : incomeStatisticOfDayVos) {
                            for (IncomeStatisticOfDayVo incomeStatistic : incomeStatisticOfDayVoList) {
                                if (incomeStatisticOfDayVo.getDay().equals(incomeStatistic.getDay())) {
                                    incomeStatisticOfDayVo.setIncome(incomeStatistic.getIncome());
                                }
                            }
                        }
                    }
                }
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    class IncomeStaticsOfMongo implements Runnable {

        private String type;
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private IncomeStatisticVo incomeStatisticVo;
        private SysUser user;
        private List<String> deviceNameList;

        public IncomeStaticsOfMongo(CountDownLatch countDownLatch, String type, AreaInfoBo areaInfoBo, SysUser user) {
            this.type = type;
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            this.incomeStatisticVo = new IncomeStatisticVo();
            this.user = user;
        }

        public IncomeStatisticVo getIncomeStatisticVo() {
            return this.incomeStatisticVo;
        }

        @Override
        public void run() {
            Criteria criteria = new Criteria();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                incomeStatisticVo.setProvince(areaInfoBo.getProvince());
                criteria.and(PROVINCE).is(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                incomeStatisticVo.setCity(areaInfoBo.getCity());
                criteria.and("city").is(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query(Criteria.where("_id").in(user.getStationList())));
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    criteria.and(DEVICE_NAME).in(deviceNames);
                    this.deviceNameList = deviceNames;
                }
            }
            //统计当天的收益、当年的收益 当月的收益
            try {
                if (!StringUtils.isEmpty(type)) {
                    Date date = new Date(System.currentTimeMillis());
                    if (DAY.equals(type)) {
                        criteria.and("incomeOfDay").is(DateUtils.dateToStr(date));
                        Aggregation aggregation = Aggregation.newAggregation(
                                Aggregation.match(criteria),
                                Aggregation.project().andExpression("{$toDouble:'$income'}").as("income"),
                                Aggregation.group().sum("income").as("count"),
                                Aggregation.limit(1)
                        );
                        List<BasicDBObject> aggregate = incomeEntityDao.aggregate(aggregation);
                        if (!CollectionUtils.isEmpty(aggregate) && !aggregate.isEmpty()) {
                            for (DBObject object : aggregate) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (MONTH.equals(type)) {
                        criteria.and("incomeOfDay").regex(DateUtils.dateToStryyyymm(date));
                        Aggregation aggregation = Aggregation.newAggregation(
                                Aggregation.match(criteria),
                                Aggregation.project().andExpression("{substr(incomeOfDay,0,7)}").as("_id")
                                        .andExpression("{$toDouble:'$income'}").as("income"),
                                Aggregation.group("_id").sum("income").as("count"),
                                Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                                Aggregation.limit(1)
                        );
                        List<BasicDBObject> dbObjects = incomeEntityDao.aggregate(aggregation);
                        if (!CollectionUtils.isEmpty(dbObjects) && !dbObjects.isEmpty()) {
                            for (DBObject object : dbObjects) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (YEAR.equals(type)) {
                        criteria.and("incomeOfDay").regex(DateUtils.dateToStryyyy(date));
                        Aggregation aggregation = Aggregation.newAggregation(
                                Aggregation.match(criteria),
                                Aggregation.project().andExpression("{substr(incomeOfDay,0,4)}").as("_id")
                                        .andExpression("{$toDouble:'$income'}").as("income"),
                                Aggregation.group("_id").sum("income").as("count"),
                                Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                                Aggregation.limit(1)
                        );
                        List<BasicDBObject> basicDBObjects = incomeEntityDao.aggregate(aggregation);
                        if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
                            for (DBObject object : basicDBObjects) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (SAFE_OF_DAY.equals(type)) {
                        /**
                         * 计算安全天数
                         */
                        if (!CollectionUtils.isEmpty(this.deviceNameList) && !this.deviceNameList.isEmpty()) {
                            /**
                             * 有绑定电站情况
                             */
                            List<Device> deviceList = deviceDao.findBatchDevices(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(deviceNameList)));
                            if (!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()) {
                                Device device = deviceList.get(0);
                                incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
                            } else {
                                Device device = deviceDao.findDevice(new Query());
                                if (device != null) {
                                    incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
                                }
                            }
                        } else {
                            /**
                             * 没有绑定电站情况  直接取第一台设备
                             */
                            Device device = deviceDao.findDevice(new Query());
                            if (device != null) {
                                incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (TOTAL.equals(type)) {
                        /**
                         * TODO 计算全部收益
                         */
                        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                                Aggregation.project().andExpression("{$toDouble:'$income'}").as("income"),
                                Aggregation.group().sum("income").as("count"),
                                Aggregation.sort(Sort.by(Sort.Order.desc("_id"))),
                                Aggregation.limit(1)
                        );
                        List<BasicDBObject> dbObjects = incomeEntityDao.aggregate(aggregation);
                        if (!CollectionUtils.isEmpty(dbObjects) && !dbObjects.isEmpty()) {
                            for (DBObject object : dbObjects) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    }
                }
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
