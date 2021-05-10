package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.bo.AreaInfoBo;
import com.bugull.hithiumfarmweb.http.bo.AreaNetInNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.dao.BamsDataDicBADao;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.dao.IncomeEntityDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.IncomeEntity;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.StringUtil;
import com.bugull.mongo.utils.ThreadUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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

import static com.bugull.hithiumfarmweb.common.Const.*;

@Service
public class StatisticsService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private RedisPoolUtil redisPoolUtil;
    @Resource
    private BreakDownLogDao breakDownLogDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private IncomeEntityDao incomeEntityDao;
    public static String INCOME_RECORD_PREFIX = "DEIVCE_INCOME_RECORED_";
    public static final Map<Integer, String> QUARTER_NAME = new HashMap<>();

    static {
        QUARTER_NAME.put(1, "第一季度");
        QUARTER_NAME.put(2, "第二季度");
        QUARTER_NAME.put(3, "第三季度");
        QUARTER_NAME.put(4, "第四季度");
    }

    public ResHelper<List<StatisticBo>> statisticNetInNum(AreaNetInNumBo areaNetInNumBo) {
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
            if (!propertiesConfig.getProToCitys().containsKey(areaNetInNumBo.getProvince())
                    || !propertiesConfig.getProToCitys().get(areaNetInNumBo.getProvince()).contains(areaNetInNumBo.getCity())) {
                return ResHelper.pamIll();
            }
            query.is("province", areaNetInNumBo.getProvince());
        }
        if (!StringUtils.isEmpty(areaNetInNumBo.getCity())) {
            if (!propertiesConfig.getCitys().containsKey(areaNetInNumBo.getCity())
                    || !propertiesConfig.getCitys().get(areaNetInNumBo.getCity()).equals(areaNetInNumBo.getProvince())) {
                return ResHelper.pamIll();
            }
            query.is("city", areaNetInNumBo.getCity());
        }
        Iterable<DBObject> objs = deviceDao.aggregate().match(query)
                .group("{_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } },count:{$sum:{$toDouble:'$stationPower'}}}").results();
        Map<String, StatisticBo> map = getDefaultBo(areaNetInNumBo.getYear());
        if (objs != null) {
            for (DBObject obj : objs) {
                String name = obj.get("_id").toString();
                Double count = Double.valueOf(obj.get("count").toString());
                StatisticBo bo = map.get(name);
                if (bo != null) {
                    bo.setCount(count);
                }
            }
        }
        List<StatisticBo> aim = new ArrayList<>(map.values());
        Collections.sort(aim, new Comparator<StatisticBo>() {
            @Override
            public int compare(StatisticBo o1, StatisticBo o2) {
                return Integer.parseInt(o1.getDate()) - Integer.parseInt(o2.getDate());
            }
        });
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

    public ResHelper<CapacityNumVo> statisticCapacityNum(AreaInfoBo areaInfoBo) {
        BuguQuery<Device> query = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is("province", areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
        }
        Iterable<DBObject> chargeIterable = deviceDao.aggregate().match(query).group("{_id:null,count:{$sum:{$toDouble:'$chargeCapacitySum'}}}").limit(1).results();
        Iterable<DBObject> dischargeIterable = deviceDao.aggregate().match(query).group("{_id:null,count:{$sum:{$toDouble:'$dischargeCapacitySum'}}}").limit(1).results();
        CapacityNumVo capacityNumBo = new CapacityNumVo();
        if (chargeIterable != null) {
            for (DBObject object : chargeIterable) {
                Double sum = (Double) object.get("count");
                BigDecimal bigDecimal = BigDecimal.valueOf(sum);
                capacityNumBo.setChargeCapacitySum(bigDecimal.toString());
            }
        }
        if (dischargeIterable != null) {
            for (DBObject object : dischargeIterable) {
                Double sum = (Double) object.get("count");
                BigDecimal bigDecimal = BigDecimal.valueOf(sum);
                capacityNumBo.setDischargeCapacitySum(bigDecimal.toString());
            }
        }
        return ResHelper.success("", capacityNumBo);
    }


    public ResHelper<AlarmNumVo> statisticAlarmNum(AreaInfoBo areaInfoBo) {
        if (areaInfoBo != null) {
            BuguQuery<Device> query = deviceDao.query();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is("province", areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity()) ||
                        !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                query.is("city", areaInfoBo.getCity());
            }
            return ResHelper.success("", getAlarmNumVo(query, areaInfoBo));
        }
        if (StringUtils.isEmpty(areaInfoBo.getProvince()) && StringUtils.isEmpty(areaInfoBo.getCity())) {
            return ResHelper.success("", getAlarmNumVo());
        }
        return ResHelper.error("");
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
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is("province", areaInfoBo.getProvince());
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

    public ResHelper<RunNumVO> runNumStatistic(AreaInfoBo areaInfoBo) {
        BuguQuery<Device> deviceBuguQuery = deviceDao.query();
        if (areaInfoBo != null) {
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                deviceBuguQuery.is("province", areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(areaInfoBo.getCity())
                        || !propertiesConfig.getCitys().get(areaInfoBo.getCity()).equals(areaInfoBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                deviceBuguQuery.is("city", areaInfoBo.getCity());
            }
        }
        long count = deviceBuguQuery.count();
        List<Device> deviceNameList = deviceBuguQuery.returnFields("deviceName").results();
        Date date = DateUtils.addDateMinutes(new Date(), -30);
        int runNum = 0;
        if (CollectionUtils.isEmpty(deviceNameList) && deviceNameList.size() > 0) {
            //设备总数
            //运行设备数量
            Iterable<DBObject> iterable = bamsDataDicBADao.aggregate()
                    .match(deviceDao.query().in("deviceName", deviceNameList))
                    .match(bamsDataDicBADao.query().greaterThanEquals("generationDataTime", date))
                    .group("{_id:'$deviceName'}").results();
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

    public ResHelper<IncomeStatisticVo> incomeStatistic(AreaInfoBo areaInfoBo) {
        BuguQuery<Device> query = deviceDao.query().existsField("income");
        IncomeStatisticVo incomeStatisticVo = new IncomeStatisticVo();
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
                    return ResHelper.pamIll();
                }
                query.is("province", areaInfoBo.getProvince());
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
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(income));
            }
        }
        incomeStatisticVo.setIncome(bigDecimal.toString());
        return ResHelper.success("", incomeStatisticVo);
    }

    public ResHelper<Map<String, IncomeStatisticVo>> incomeStatistics(AreaInfoBo areaInfoBo) {

        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
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
        IncomeStatisOfRedis g1 = new IncomeStatisOfRedis(countDownLatch, "DAY", areaInfoBo);
        executorService.execute(g1);
        IncomeStatisOfRedis g2 = new IncomeStatisOfRedis(countDownLatch, "MONTH", areaInfoBo);
        executorService.execute(g2);
        IncomeStatisOfRedis g3 = new IncomeStatisOfRedis(countDownLatch, "YEAR", areaInfoBo);
        executorService.execute(g3);
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
        return ResHelper.success("", resultMap);
    }

    private String getIncomeKey(Date recordDate, IncomeStatisticVo incomeStatisticVo, String type) {
        switch (type) {
            case DAY:
                return getDayIncomeKey(recordDate, incomeStatisticVo);
            case MONTH:
                return getMonthIncomeKey(recordDate, incomeStatisticVo);
            case YEAR:
                return getYearIncomeKey(recordDate, incomeStatisticVo);
            default:
                return null;
        }
    }

    private String getYearIncomeKey(Date recordDate, IncomeStatisticVo incomeStatisticVo) {
        if (incomeStatisticVo != null) {
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyy(recordDate) + "_" + incomeStatisticVo.getProvince() + "_*";
            }
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyy(recordDate) + "_" + incomeStatisticVo.getProvince() + "_" + incomeStatisticVo.getCity();
            }
            if (StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                String province = propertiesConfig.getCitys().get(incomeStatisticVo.getCity());
                if (!StringUtils.isEmpty(province)) {
                    incomeStatisticVo.setProvince(province);
                    return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyy(recordDate) + "_" + province + "_" + incomeStatisticVo.getCity();
                }
                return null;
            }
        }
        return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyy(recordDate) + "_*";
    }

    private String getMonthIncomeKey(Date recordDate, IncomeStatisticVo incomeStatisticVo) {
        if (incomeStatisticVo != null) {
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyymm(recordDate) + "_" + incomeStatisticVo.getProvince() + "_*";
            }
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyymm(recordDate) + "_" + incomeStatisticVo.getProvince() + "_" + incomeStatisticVo.getCity();
            }
            if (StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                String province = propertiesConfig.getCitys().get(incomeStatisticVo.getCity());
                if (!StringUtils.isEmpty(province)) {
                    incomeStatisticVo.setProvince(province);
                    return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyymm(recordDate) + "_" + province + "_" + incomeStatisticVo.getCity();
                }
                return null;
            }
        }
        return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyymm(recordDate) + "_*";
    }

    private String getDayIncomeKey(Date recordDate, IncomeStatisticVo incomeStatisticVo) {
        if (incomeStatisticVo != null) {
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStr(recordDate) + "_" + incomeStatisticVo.getProvince() + "_*";
            }
            if (!StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
                return INCOME_RECORD_PREFIX + DateUtils.dateToStr(recordDate) + "_" + incomeStatisticVo.getProvince() + "_" + incomeStatisticVo.getCity();
            }
        }
        if (StringUtils.isEmpty(incomeStatisticVo.getProvince()) && !StringUtils.isEmpty(incomeStatisticVo.getCity())) {
            String province = propertiesConfig.getCitys().get(incomeStatisticVo.getCity());
            if (!StringUtils.isEmpty(province)) {
                incomeStatisticVo.setProvince(province);
                return INCOME_RECORD_PREFIX + DateUtils.dateToStr(recordDate) + "_*";
            }
            return null;
        }
        return INCOME_RECORD_PREFIX + DateUtils.dateToStr(recordDate) + "_*";
    }

    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> incomeStatisticOfAll(AreaInfoBo areaInfoBo) {
        if (areaInfoBo != null) {
            if (!StringUtil.isEmpty(areaInfoBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(areaInfoBo.getProvince())
                        || !propertiesConfig.getProToCitys().get(areaInfoBo.getProvince()).contains(areaInfoBo.getCity())) {
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
        /**
         * TODO 使用countDownLatch
         */
        IncomeStatisOfDb incomeStatisOfDb1 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFDAY, 7);
        executorService.execute(incomeStatisOfDb1);
        IncomeStatisOfDb incomeStatisOfDb2 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFMONTH, 12);
        executorService.execute(incomeStatisOfDb2);
        IncomeStatisOfDb incomeStatisOfDb3 = new IncomeStatisOfDb(countDownLatch, areaInfoBo, INCOMEOFYEAR, 10);
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

    class IncomeStatisOfDb implements Runnable {
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private String aggregateMsg;
        private Integer limit;
        private List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();

        public IncomeStatisOfDb(CountDownLatch countDownLatch, AreaInfoBo areaInfoBo, String aggregateMsg, Integer limit) {
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            this.aggregateMsg = aggregateMsg;
            this.limit = limit;
        }

        public List<IncomeStatisticOfDayVo> getIncomeStatisticOfDayVos() {
            return this.incomeStatisticOfDayVos;
        }

        @Override
        public void run() {
            try {
                BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query();
                if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                    incomeEntityBuguQuery.is("province", areaInfoBo.getProvince());
                }
                if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                    incomeEntityBuguQuery.is("city", areaInfoBo.getCity());
                }
                Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                        .group(aggregateMsg).sort("{_id:-1}").limit(limit).results();
                if (iterable != null) {
                    for (DBObject object : iterable) {
                        IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                        String incomeOfDay = (String) object.get("_id");
                        Double count = (Double) object.get("count");
                        incomeStatisticOfDayVo.setDay(incomeOfDay);
                        incomeStatisticOfDayVo.setIncome(BigDecimal.valueOf(count).toString());
                        incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
                    }
                }
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    class IncomeStatisOfRedis implements Runnable {

        private String type;
        private CountDownLatch countDownLatch;
        private AreaInfoBo areaInfoBo;
        private IncomeStatisticVo incomeStatisticVo;

        public IncomeStatisOfRedis(CountDownLatch countDownLatch, String type, AreaInfoBo areaInfoBo) {
            this.type = type;
            this.countDownLatch = countDownLatch;
            this.areaInfoBo = areaInfoBo;
            incomeStatisticVo = new IncomeStatisticVo();
        }

        public IncomeStatisticVo getIncomeStatisticVo() {
            return this.incomeStatisticVo;
        }

        @Override
        public void run() {
            BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query();
            if (!StringUtils.isEmpty(areaInfoBo.getProvince())) {
                incomeStatisticVo.setProvince(areaInfoBo.getProvince());
                incomeEntityBuguQuery.is("province", areaInfoBo.getProvince());
            }
            if (!StringUtils.isEmpty(areaInfoBo.getCity())) {
                incomeStatisticVo.setCity(areaInfoBo.getCity());
                incomeEntityBuguQuery.is("city", areaInfoBo.getCity());
            }
            //TODO 使用数据库的数据统计 不用redis
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
                                incomeStatisticVo.setIncome(decimal.toString());
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
                                incomeStatisticVo.setIncome(String.valueOf(count));
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    } else {
                        incomeEntityBuguQuery.regexCaseInsensitive("incomeOfDay", DateUtils.dateToStryyyy(date));
                        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery)
                                .group(INCOMEOFYEAR).sort("{_id:-1}").limit(1).results();
                        if (iterable != null) {
                            for (DBObject object : iterable) {
                                Double count = (Double) object.get("count");
                                incomeStatisticVo.setIncome(String.valueOf(count));
                            }
                        }
                        incomeStatisticVo.setDate(date);
                    }
                }
            } finally {
                countDownLatch.countDown();
            }

//            Jedis jedis = null;
//            try {
//                jedis = redisPoolUtil.getJedis();
//                String incomeKey = getIncomeKey(new Date(), incomeStatisticVo, type);
//                if (!StringUtils.isEmpty(incomeKey)) {
//                    Set<String> keys = jedis.keys(incomeKey);
//                    BigDecimal result = new BigDecimal(0);
//                    for (String key : keys) {
//                        BigDecimal keyBigDecimal = new BigDecimal(jedis.get(key));
//                        result = result.add(keyBigDecimal);
//                    }
//                    incomeStatisticVo.setIncome(result.toString());
//                }
//            } finally {

//                if (jedis != null) {
//                    jedis.close();
//                }

//            }
        }
    }
}
