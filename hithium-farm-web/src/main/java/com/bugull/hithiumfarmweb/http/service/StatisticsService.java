package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.AreaInfoBo;
import com.bugull.hithiumfarmweb.http.bo.AreaNetInNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguAggregation;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.StringUtil;
import com.bugull.mongo.utils.ThreadUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        BuguQuery<Device> query = deviceDao.query()
                .greaterThanEquals("accessTime", startTime)
                .lessThanEquals("accessTime", endTime);
        if (!StringUtils.isEmpty(areaNetInNumBo.getProvince())) {
            if (!propertiesConfig.getProToCitys().containsKey(areaNetInNumBo.getProvince())) {
                return ResHelper.pamIll();
            }
            if (!StringUtils.isEmpty(areaNetInNumBo.getCity()) && !propertiesConfig.getProToCitys().get(areaNetInNumBo.getProvince()).contains(areaNetInNumBo.getCity())) {
                return ResHelper.pamIll();
            }
            query.is(PROVINCE, areaNetInNumBo.getProvince());
        }
        if (!StringUtils.isEmpty(areaNetInNumBo.getCity())) {
            if (!propertiesConfig.getCitys().containsKey(areaNetInNumBo.getCity())
                    || !propertiesConfig.getCitys().get(areaNetInNumBo.getCity()).equals(areaNetInNumBo.getProvince())) {
                return ResHelper.pamIll();
            }
            query.is("city", areaNetInNumBo.getCity());
        }
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
            if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    query.in(DEVICE_NAME, deviceNames);
                }
            }
        }
        Iterable<DBObject> objs = deviceDao.aggregate().match(query)
                .group("{_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } },count:{$sum:{$toDouble:'$stationPower'}}}").results();
        Map<String, StatisticBo> map = getDefaultBo(areaNetInNumBo.getYear());
        if (objs != null) {
            for (DBObject obj : objs) {
                String name = obj.get("_id").toString();
                String count = obj.get("count").toString();
                String decimal = new BigDecimal(count).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
                Double countDoub = Double.valueOf(decimal);
                StatisticBo bo = map.get(name);
                if (bo != null) {
                    bo.setCount(countDoub);
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
        BuguQuery<Device> query = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    query.in(DEVICE_NAME, deviceNames);
                }
            }
        }
        Iterable<DBObject> chargeIterable = deviceDao.aggregate().match(query).group("{_id:null,count:{$sum:{$toDouble:'$chargeCapacitySum'}}}").limit(1).results();
        Iterable<DBObject> dischargeIterable = deviceDao.aggregate().match(query).group("{_id:null,count:{$sum:{$toDouble:'$dischargeCapacitySum'}}}").limit(1).results();
        CapacityNumVo capacityNumBo = new CapacityNumVo();
        if (chargeIterable != null) {
            for (DBObject object : chargeIterable) {
                Double sum = Double.valueOf(object.get("count").toString());
                BigDecimal bigDecimal = BigDecimal.valueOf(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
                capacityNumBo.setChargeCapacitySum(bigDecimal.toString());
            }
        }
        if (dischargeIterable != null) {
            for (DBObject object : dischargeIterable) {
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
            BuguQuery<Device> query = deviceDao.query();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity()) ||
                        !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    query.in(DEVICE_NAME, deviceNames);
                }
            }
            return ResHelper.success("", getAlarmNumVo(query, areaInfoBo));
        }
        return ResHelper.pamIll();


    }

    private AlarmNumVo getAlarmNumVo(BuguQuery<Device> query, AreaInfoBo areaInfoBo) {
        return createAlarmNumVo(query, areaInfoBo);
    }

    public AlarmNumVo getAlarmNumVo() {
        return createAlarmNumVo(null, null);
    }

    private AlarmNumVo createAlarmNumVo(BuguQuery<Device> query, AreaInfoBo areaInfoBo) {
        AlarmNumVo alarmNumVo = new AlarmNumVo();
        if (areaInfoBo != null) {
            alarmNumVo.setCity(areaInfoBo.getCity());
            alarmNumVo.setProvince(areaInfoBo.getProvince());
        }
        BuguQuery<BreakDownLog> downLogBuguQuery = breakDownLogDao.query().is("status", true);
        Iterable<DBObject> results = breakDownLogDao.aggregate().match(downLogBuguQuery).match(query)
                .group("{_id:'$null',count:{$sum:1}}").limit(1)
                .results();
        if (results != null) {
            for (DBObject object : results) {
                Integer count = (Integer) object.get("count");
                alarmNumVo.setNumber(count);
            }
        }
        alarmNumVo.setCountry(COUNTRY);
        return alarmNumVo;
    }

    public ResHelper<SaveEnergyVo> saveEnergyNum(AreaInfoBo areaInfoBo) {
        BuguQuery<Device> query = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
        }

        return ResHelper.error("");
    }

    public ResHelper<RunNumVO> runNumStatistic(AreaInfoBo areaInfoBo, SysUser user) {
        BuguQuery<Device> deviceBuguQuery = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                deviceBuguQuery.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                deviceBuguQuery.is("city", areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    deviceBuguQuery.in(DEVICE_NAME, deviceNames);
                }
            }
        }
        long count = deviceBuguQuery.count();
        List<Device> deviceNameList = deviceBuguQuery.returnFields(DEVICE_NAME).results();
        List<String> deviceNameStrList = deviceNameList.stream().map(Device::getDeviceName).collect(Collectors.toList());
        Date date = DateUtils.addDateMinutes(new Date(), -30);
        int runNum = 0;
        if (!CollectionUtils.isEmpty(deviceNameStrList) && !deviceNameStrList.isEmpty()) {
            //设备总数
            //运行设备数量
            Iterable<DBObject> iterable = bamsDataDicBADao.aggregate()
                    .match(deviceDao.query().in(DEVICE_NAME, deviceNameStrList))
                    .match(bamsDataDicBADao.query().greaterThanEquals("generationDataTime", date)).group("{_id:'$deviceName'}").results();
            if (iterable != null) {
                while (iterable.iterator().hasNext()) {
                    runNum++;
                }
            }
        }
        RunNumVO runNumVO = new RunNumVO();
        runNumVO.setRunNum(runNum);
        runNumVO.setNotRunNum(Integer.valueOf((int) count - runNum));
        return ResHelper.success("", runNumVO);
    }

    public ResHelper<IncomeStatisticVo> incomeStatistic(AreaInfoBo areaInfoBo, SysUser user) {
        BuguQuery<Device> query = deviceDao.query().existsField("income");
        IncomeStatisticVo incomeStatisticVo = new IncomeStatisticVo();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is(PROVINCE, areaInfoBo.getProvince());
                incomeStatisticVo.setProvince(areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
                incomeStatisticVo.setCity(areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    query.in(DEVICE_NAME, deviceNames);
                }
            }
        }
        DBObject group = new BasicDBObject();
        group.put("_id", null);
        DBObject toDouble = new BasicDBObject();
        toDouble.put("$toDouble", "$income");
        DBObject dbObject = new BasicDBObject("$sum", toDouble);
        group.put("count", dbObject);
        Iterable<DBObject> iterable = deviceDao.aggregate().match(query).group(group).results();
        BigDecimal bigDecimal = new BigDecimal("0");
        if (iterable != null) {
            for (DBObject object : iterable) {
                Double income = (Double) object.get("count");
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(income).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        incomeStatisticVo.setIncome(bigDecimal.toString());
        return ResHelper.success("", incomeStatisticVo);
    }

    public ResHelper<Map<String, IncomeStatisticVo>> incomeStatistics(AreaInfoBo areaInfoBo, SysUser user) {

        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
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
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(4);
        IncomeStatisOfMongo g1 = new IncomeStatisOfMongo(countDownLatch, "DAY", areaInfoBo, user);
        executorService.execute(g1);
        IncomeStatisOfMongo g2 = new IncomeStatisOfMongo(countDownLatch, "MONTH", areaInfoBo, user);
        executorService.execute(g2);
        IncomeStatisOfMongo g3 = new IncomeStatisOfMongo(countDownLatch, "YEAR", areaInfoBo, user);
        executorService.execute(g3);
        IncomeStatisOfMongo g4 = new IncomeStatisOfMongo(countDownLatch, "SAFE_OF_DAY", areaInfoBo, user);
        executorService.execute(g4);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            ThreadUtil.safeClose(executorService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, IncomeStatisticVo> resultMap = new HashMap<>();
        resultMap.put(DAY, g1.getIncomeStatisticVo());
        resultMap.put(MONTH, g2.getIncomeStatisticVo());
        resultMap.put(YEAR, g3.getIncomeStatisticVo());
        resultMap.put(SAFE_OF_DAY, g4.getIncomeStatisticVo());
        return ResHelper.success("", resultMap);
    }

    private IncomeStatisticVo getSafeDays(List<String> deviceNames) {
        IncomeStatisticVo incomeStatisticVo = new IncomeStatisticVo();

        return null;
    }

    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> incomeStatisticOfAll(AreaInfoBo areaInfoBo, SysUser user) {
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
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
        IncomeStatisOfDb incomeStatisOfDb1 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFDAY, 7, user);
        executorService.execute(incomeStatisOfDb1);
        IncomeStatisOfDb incomeStatisOfDb2 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFMONTH, 6, user);
        executorService.execute(incomeStatisOfDb2);
        IncomeStatisOfDb incomeStatisOfDb3 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFYEAR, 5, user);
        executorService.execute(incomeStatisOfDb3);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            ThreadUtil.safeClose(executorService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, List<IncomeStatisticOfDayVo>> map = new HashMap<>();
        map.put(DAY, incomeStatisOfDb1.getIncomeStatisticOfDayVos());
        map.put(MONTH, incomeStatisOfDb2.getIncomeStatisticOfDayVos());
        map.put(YEAR, incomeStatisOfDb3.getIncomeStatisticOfDayVos());
        return ResHelper.success("", map);
    }

    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> deviceOfIncomeStatistic(String deviceName) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        IncomeOfDevice incomeOfDevice1 = new IncomeOfDevice(countDownLatch, deviceName, INCOMEOFDAY, 7);
        executorService.execute(incomeOfDevice1);
        IncomeOfDevice incomeOfDevice2 = new IncomeOfDevice(countDownLatch, deviceName, INCOMEOFMONTH, 6);
        executorService.execute(incomeOfDevice2);
        IncomeOfDevice incomeOfDevice3 = new IncomeOfDevice(countDownLatch, deviceName, INCOMEOFYEAR, 5);
        executorService.execute(incomeOfDevice3);
        try {
            countDownLatch.await(120L, TimeUnit.SECONDS);
            ThreadUtil.safeClose(executorService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, List<IncomeStatisticOfDayVo>> map = new HashMap<>();
        map.put(DAY, incomeOfDevice1.getIncomeStatisticOfDayVos());
        map.put(MONTH, incomeOfDevice2.getIncomeStatisticOfDayVos());
        map.put(YEAR, incomeOfDevice3.getIncomeStatisticOfDayVos());
        return ResHelper.success("", map);
    }

    //
    public ResHelper<Map<String, List<CapacityVo>>> capacityNumOfDate(SysUser user, AreaInfoBo areaInfoBo) {
        BuguQuery<Device> query = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity()) && !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    query.in(DEVICE_NAME, deviceNames);
                }
            }
        }
        List<String> deviceNameList = query.results().stream().map(device -> device.getDeviceName()).collect(Collectors.toList());
        BuguQuery<BamsDataDicBA> bamsDataDicBABuguQuery = bamsDataDicBADao.query();
        BuguAggregation<BamsDischargeCapacity> aggregate = bamsDischargeCapacityDao.aggregate();
        if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
            bamsDataDicBABuguQuery.in(DEVICE_NAME, deviceNameList);
            aggregate.match(bamsDataDicBABuguQuery);
        }
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
            ThreadUtil.safeClose(executorService);
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
        EssStation essStation = essStationDao.query().is("_id", stationId).result();
        if (essStation != null) {
            if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                List<Device> deviceList = deviceDao.query().in(DEVICE_NAME, essStation.getDeviceNameList()).results();
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
        List<IncomeEntity> incomeEntities = incomeEntityDao.query().in("deviceName", deviceNameList).is("incomeOfDay", dateToStr).results();
        BigDecimal incomeBigDecimal = new BigDecimal(0);
        for (IncomeEntity incomeEntity : incomeEntities) {
            incomeBigDecimal = incomeBigDecimal.add(incomeEntity.getIncome().setScale(4, BigDecimal.ROUND_HALF_UP));
        }
        chargeCapacityStationVo.setDayStationIncome(incomeBigDecimal.toString());
    }

    private void getTotalStationIncome(List<String> deviceNameList, ChargeCapacityStationVo chargeCapacityStationVo) {
        Iterable<DBObject> incomeIterable = incomeEntityDao.aggregate().match(incomeEntityDao.query().in("deviceName", deviceNameList))
                .group("{_id:null,count:{$sum:{$toDouble:'$income'}}}").limit(1).results();
        if (incomeIterable != null) {
            for (DBObject object : incomeIterable) {
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


        private void initHoursMapData() {
            this.initData = new TreeMap<>();
            for (int i = 0; i < limit; i++) {
                String key = i > 9 ? String.valueOf(i) : "0" + i;
                initData.put(key, new CapacityVo(key, 0D));
            }
            BuguQuery<BamsDataDicBA> bamsDataDicBABuguQuery = bamsDataDicBADao.query();
            BuguAggregation<BamsDischargeCapacity> aggregate = bamsDischargeCapacityDao.aggregate();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                bamsDataDicBABuguQuery.in(DEVICE_NAME, deviceNameList);
                aggregate.match(bamsDataDicBABuguQuery);
            }
            Iterable<DBObject> iterable =
                    aggregate.match(bamsDischargeCapacityDao.query()
                            .greaterThanEquals("generationDataTime", DateUtils.getStartTime(new Date())).lessThanEquals("generationDataTime", DateUtils.getEndTime(new Date()))).group(groupMsg)
                            .sort("{generationDataTime:-1}").limit(limit)
                            .results();
            if (iterable != null) {
                for (DBObject object : iterable) {
                    String[] id = object.get("_id").toString().split(" ");
                    CapacityVo capacityVo = initData.get(id[1]);
                    if (capacityVo != null) {
                        capacityVo.setNumber(Double.valueOf(object.get("count").toString()));
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

        private void initMonthMapData() {
            this.initData = new HashMap<>();
            for (int i = 1; i <= limit; i++) {
                String key = i > 9 ? String.valueOf(i) : "0" + i;
                initData.put(key, new CapacityVo(key, 0D));
            }
            BuguQuery<BamsDataDicBA> bamsDataDicBABuguQuery = bamsDataDicBADao.query();
            BuguAggregation<BamsDischargeCapacity> aggregate = bamsDischargeCapacityDao.aggregate();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                bamsDataDicBABuguQuery.in(DEVICE_NAME, deviceNameList);
                aggregate.match(bamsDataDicBABuguQuery);
            }
            Iterable<DBObject> iterable = aggregate
                    .match(bamsDischargeCapacityDao.query().greaterThanEquals("generationDataTime", DateUtils.getCurrentYearStartTime())
                            .lessThanEquals("generationDataTime", DateUtils.getCurrentYearEndTime())).group(groupMsg)
                    .sort("{generationDataTime:-1}").limit(limit)
                    .results();
            if (iterable != null) {
                for (DBObject object : iterable) {
                    String[] idSplit = object.get("_id").toString().split("-");
                    CapacityVo capacityVo = initData.get(idSplit[1]);
                    if (capacityVo != null) {
                        capacityVo.setNumber(Double.valueOf(object.get("count").toString()));
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
                initData.put(key, new CapacityVo(key, 0D));
            }
            BuguQuery<BamsDataDicBA> bamsDataDicBABuguQuery = bamsDataDicBADao.query();
            BuguAggregation<BamsDischargeCapacity> aggregate = bamsDischargeCapacityDao.aggregate();
            if (!CollectionUtils.isEmpty(deviceNameList) && !deviceNameList.isEmpty()) {
                bamsDataDicBABuguQuery.in(DEVICE_NAME, deviceNameList);
                aggregate.match(bamsDataDicBABuguQuery);
            }
            Iterable<DBObject> iterable = aggregate
                    .match(bamsDischargeCapacityDao.query().greaterThanEquals("generationDataTime", dayOfSevenAgo).lessThanEquals("generationDataTime", DateUtils.getEndTime(new Date()))).group(groupMsg)
                    .sort("{generationDataTime:-1}").limit(limit)
                    .results();
            Map<String, CapacityVo> dbOfResultOfHour = new HashMap<>();
            if (iterable != null) {
                for (DBObject object : iterable) {
                    String[] id = object.get("_id").toString().split(" ");
                    CapacityVo capacityVo = initData.get(id[1]);
                    if (capacityVo != null) {
                        capacityVo.setNumber(Double.valueOf(object.get("count").toString()));
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
                    return Integer.parseInt(splitOne[0]) - Integer.parseInt(splitTwo[0]);
                }
            });
            this.capacityVos = capacityVos;
        }
    }


    class IncomeOfDevice implements Runnable {
        private CountDownLatch countDownLatch;
        private String deviceName;
        private String groupMsg;
        private Integer limit;
        private List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();

        public List<IncomeStatisticOfDayVo> getIncomeStatisticOfDayVos() {
            return this.incomeStatisticOfDayVos;
        }

        public IncomeOfDevice(CountDownLatch countDownLatch, String deviceName, String groupMsg, Integer limit) {
            this.countDownLatch = countDownLatch;
            this.deviceName = deviceName;
            this.limit = limit;
            this.groupMsg = groupMsg;
            initIncomeStatis();
        }

        private void initIncomeStatis() {
            if (groupMsg.equals(INCOMEOFDAY)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            } else if (groupMsg.equals(INCOMEOFMONTH)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYYMM(DateUtils.addDateMonths(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            } else if (groupMsg.equals(INCOMEOFYEAR)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYY(DateUtils.addDateYears(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            }
        }

        @Override
        public void run() {
            try {
                Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityDao.query().is("deviceName", deviceName))
                        .group(groupMsg).sort("{_id:-1}").limit(limit).results();
                if (iterable != null) {
                    List<IncomeStatisticOfDayVo> resultStatisticOfDayList = new ArrayList<>();
                    for (DBObject object : iterable) {
                        String incomeOfDay = object.get("_id").toString();
                        Double counct = Double.valueOf(object.get("count").toString());
                        IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                        incomeStatisticOfDayVo.setIncome(BigDecimal.valueOf(counct).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
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

    class IncomeStatisOfDb implements Runnable {
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private String aggregateMsg;
        private Integer limit;
        private SysUser user;
        private List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();

        public IncomeStatisOfDb(CountDownLatch countDownLatch, AreaInfoBo areaInfoBo, String aggregateMsg, Integer limit, SysUser user) {
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            this.aggregateMsg = aggregateMsg;
            this.limit = limit;
            this.user = user;
            initIncomeStatis();
        }

        public List<IncomeStatisticOfDayVo> getIncomeStatisticOfDayVos() {
            return this.incomeStatisticOfDayVos;
        }

        private void initIncomeStatis() {
            if (aggregateMsg.equals(INCOMEOFDAY)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            } else if (aggregateMsg.equals(INCOMEOFMONTH)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYYMM(DateUtils.addDateMonths(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            } else if (aggregateMsg.equals(INCOMEOFYEAR)) {
                for (int i = 0; i < limit; i++) {
                    IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                    incomeStatisticOfDayVo.setDay(DateUtils.formatYYYY(DateUtils.addDateYears(new Date(), -i)));
                    incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                }
            }
        }

        @Override
        public void run() {
            try {
                BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query();
                if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                    incomeEntityBuguQuery.is(PROVINCE, areaInfoBo.getProvince());
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                    incomeEntityBuguQuery.is("city", areaInfoBo.getCity());
                }
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                    List<String> deviceNames = new ArrayList<>();
                    for (EssStation essStation : essStationList) {
                        List<String> deviceNameList = essStation.getDeviceNameList();
                        deviceNames.addAll(deviceNameList);
                    }
                    if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                        incomeEntityBuguQuery.in(DEVICE_NAME, deviceNames);
                    }
                }
                Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                        .group(aggregateMsg).sort("{_id:-1}").limit(limit).results();
                if (iterable != null) {
                    List<IncomeStatisticOfDayVo> incomeStatisticOfDayVoList = new ArrayList<>();
                    for (DBObject object : iterable) {
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

    class IncomeStatisOfMongo implements Runnable {

        private String type;
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private IncomeStatisticVo incomeStatisticVo;
        private SysUser user;
        private List<String> deviceNameList;

        public IncomeStatisOfMongo(CountDownLatch countDownLatch, String type, AreaInfoBo areaInfoBo, SysUser user) {
            this.type = type;
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            incomeStatisticVo = new IncomeStatisticVo();
            this.user = user;
        }

        public IncomeStatisticVo getIncomeStatisticVo() {
            return this.incomeStatisticVo;
        }

        @Override
        public void run() {
            BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                incomeStatisticVo.setProvince(areaInfoBo.getProvince());
                incomeEntityBuguQuery.is(PROVINCE, areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                incomeStatisticVo.setCity(areaInfoBo.getCity());
                incomeEntityBuguQuery.is("city", areaInfoBo.getCity());
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                List<EssStation> essStationList = essStationDao.query().in("_id", user.getStationList()).results();
                List<String> deviceNames = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    List<String> deviceNameList = essStation.getDeviceNameList();
                    deviceNames.addAll(deviceNameList);
                }
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    incomeEntityBuguQuery.in(DEVICE_NAME, deviceNames);
                    this.deviceNameList = deviceNames;
                }
            }
            //统计当天的收益、当年的收益 当月的收益
            try {
                if (!StringUtils.isEmpty(type)) {
                    Date date = new Date();
                    if (DAY.equals(type)) {
                        incomeEntityBuguQuery.is("incomeOfDay", DateUtils.dateToStr(date));
                        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                                .group(INCOMEOFDAY).sort("{_id:-1}").limit(1).results();
                        if (iterable != null) {
                            for (DBObject object : iterable) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (MONTH.equals(type)) {
                        incomeEntityBuguQuery.regexCaseInsensitive("incomeOfDay", DateUtils.dateToStryyyymm(date));
                        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                                .group(INCOMEOFMONTH).sort("{_id:-1}").limit(1).results();
                        if (iterable != null) {
                            for (DBObject object : iterable) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else if (YEAR.equals(type)) {
                        incomeEntityBuguQuery.regexCaseInsensitive("incomeOfDay", DateUtils.dateToStryyyy(date));
                        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                                .group(INCOMEOFYEAR).sort("{_id:-1}").limit(1).results();
                        if (iterable != null) {
                            for (DBObject object : iterable) {
                                Double count = (Double) object.get("count");
                                BigDecimal decimal = BigDecimal.valueOf(count);
                                incomeStatisticVo.setIncome(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else {
                        /**
                         * 计算安全天数
                         */
                        if (!CollectionUtils.isEmpty(this.deviceNameList) && !this.deviceNameList.isEmpty()) {
                            /**
                             * 有绑定电站情况
                             */
                            List<Device> deviceList = deviceDao.query().in("deviceName", deviceNameList).results();
                            if(!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()){
                                Device device = deviceList.get(0);
                                incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
                            }else {
                                Device device = deviceDao.findOne();
                                if(device != null){
                                    incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
                                }
                            }
                        } else {
                            /**
                             * 没有绑定电站情况  直接取第一台设备
                             */
                            Device device = deviceDao.findOne();
                            if(device != null){
                                incomeStatisticVo.setIncome(String.valueOf((date.getTime() - device.getAccessTime().getTime()) / DAY_OF_MILLISECONDS));
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
