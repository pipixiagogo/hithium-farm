package com.bugull.hithium.integration.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithium.core.common.Const;
import com.bugull.hithium.core.dao.*;
import com.bugull.hithium.core.dto.*;
import com.bugull.hithium.core.entity.*;
import com.bugull.hithium.core.enumerate.DataType;
import com.bugull.hithium.core.util.DateUtil;
import com.bugull.hithium.core.util.PropertiesConfig;
import com.bugull.hithium.integration.message.JMessage;
import com.bugull.hithium.integration.util.redis.RedisPoolUtil;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import com.mongodb.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithium.core.common.Const.*;


@Component("kCEssDeviceService")
public class KCEssDeviceService {
    private static final Logger log = LoggerFactory.getLogger(KCEssDeviceService.class);

    public static final Map<String, Object> REAL_TIME_DATA_TYPE = new HashMap<>();

    public static String INCOME_RECORD_PREFIX = "DEIVCE_INCOME_RECORED_";
    static {
        REAL_TIME_DATA_TYPE.put("PcsCabinetDic", PcsCabinetDic.class);
        REAL_TIME_DATA_TYPE.put("PcsChannelDic", PcsChannelDic.class);
        REAL_TIME_DATA_TYPE.put("BamsDataDic", BamsDataDicBA.class);
        REAL_TIME_DATA_TYPE.put("BcuDataDic", BcuDataDicBCU.class);
        REAL_TIME_DATA_TYPE.put("BmsCellVoltDataDic", BmsCellVoltDataDic.class);
        REAL_TIME_DATA_TYPE.put("BmsCellTempDataDic", BmsCellTempDataDic.class);
        REAL_TIME_DATA_TYPE.put("AmmeterDataDic", AmmeterDataDic.class);
        REAL_TIME_DATA_TYPE.put("TemperatureMeterDataDic", TemperatureMeterDataDic.class);
        REAL_TIME_DATA_TYPE.put("AirConditionDataDic", AirConditionDataDic.class);
        REAL_TIME_DATA_TYPE.put("FireControlDataDic", FireControlDataDic.class);
        REAL_TIME_DATA_TYPE.put("UpsPowerDataDic", UpsPowerDataDic.class);
    }

    @Resource
    private PccsDao pccsDao;
    @Resource
    private CubeDao cubeDao;
    @Resource
    private CabinDao cabinDao;
    @Resource
    private EquipmentDao equipmentDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private AmmeterDataDicDao ammeterDataDicDao;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private BmsCellTempDataDicDao bmsCellTempDataDicDao;
    @Resource
    private BmsCellVoltDataDicDao bmsCellVoltDataDicDao;
    @Resource
    private BcuDataDicBCUDao bcuDataDicBCUDao;
    @Resource
    private PcsCabinetDicDao pcsCabinetDicDao;
    @Resource
    private TemperatureMeterDataDicDao temperatureMeterDataDicDao;
    @Resource
    private PcsChannelDicDao pcsChannelDicDao;
    @Resource
    private UpsPowerDataDicDao upsPowerDataDicDao;
    @Resource
    private FireControlDataDicDao fireControlDataDicDao;
    @Resource
    private AirConditionDataDicDao airConditionDataDicDao;
    @Resource
    private BreakDownLogDao breakDownLogDao;
    @Resource
    private RedisPoolUtil redisPoolUtil;
    @Resource
    private PropertiesConfig propertiesConfig;

    /**
     * 处理设备
     */
    public void handle(Message<JMessage> messageMessage) {
        JMessage jMessage = messageMessage.getPayload();
        DataType dataType = jMessage.getDataType();
        try {
            switch (dataType) {
                case DEVICE_LIST_DATA:
                    handleDeviceInfo(jMessage);
                    break;
                case BREAKDOWNLOG_DATA:
                    handleBreakDownLog(jMessage);
                    break;
                case REAL_TIME_DATA:
                    handleRealTimeData(jMessage);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("数据解析错误:{},dataType:{}", e, dataType);
        }
    }

    private void handleRealTimeData(JMessage jMessage) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("设备上报实时数据:{}", deviceName);
            JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
            JSONObject datajson = (JSONObject) jsonObject.get(Const.DATA);
            List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).results();
            if (equipmentList != null && equipmentList.size() > 0) {
                jedis.set("REALDATA-" + deviceName, datajson.toString());
                resolvingRealDataTime(datajson, equipmentList, deviceName);
            } else {
                log.info("设备上报实时数据未查到对应设备:{}", deviceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("设备上报实时数据格式错误,丢弃:{}", jMessage.getPayloadMsg());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void resolvingRealDataTime(JSONObject datajson, List<Equipment> equipmentList, String deviceName) {
        for (Map.Entry<String, Object> entry : REAL_TIME_DATA_TYPE.entrySet()) {
            JSONObject bodyJSONObject = datajson.getJSONObject(entry.getKey());
            if (bodyJSONObject != null) {
                equipmentList.stream().forEach(equipment -> {
                    JSONObject jsonObject = bodyJSONObject.getJSONObject(equipment.getEquipmentId().toString());
                    if (jsonObject != null) {
                        parseSaveData(entry, jsonObject, equipment);
                    }
                });
            }
        }
    }

    private void parseSaveData(Map.Entry<String, Object> entry, JSONObject jsonObject, Equipment device) {
        switch (entry.getKey()) {
            case Const.REAL_TIME_DATATYPE_AMMETER:
                handleAmmeterData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_BAMS:
                handleBamsData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_BMSCELLTEMP:
                handleBmsCellTempData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_BMSCELLVOLT:
                handleBmsCellVolData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_BCU:
                handleBcuData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_PCSCABINET:
                handlePcsData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_PCSCHANNEL:
                handlePcsChannelData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_AIRCONDITION:
                handleAirConditionData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_TEMPERATUREMETER:
                handleTemperatureData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_FIRECONTROLDATA:
                handleFireControlData(jsonObject, entry, device);
                break;
            case REAL_TIME_DATATYPE_UPSPOWERDATA:
                handleUpsPowerData(jsonObject, entry, device);
                break;
            default:
                log.error("无效数据");
        }
    }

    private void handleFireControlData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        FireControlDataDic fireControlDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (fireControlDataDic != null) {
            fireControlDataDic.setName(equipment.getName());
            fireControlDataDic.setDeviceName(equipment.getDeviceName());
            fireControlDataDic.setGenerationDataTime(strToDate(fireControlDataDic.getTime()));
            fireControlDataDicDao.insert(fireControlDataDic);
        }
    }

    private void handleAirConditionData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        AirConditionDataDic airConditionDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (airConditionDataDic != null) {
            airConditionDataDic.setName(equipment.getName());
            airConditionDataDic.setDeviceName(equipment.getDeviceName());
            airConditionDataDic.setGenerationDataTime(strToDate(airConditionDataDic.getTime()));
            airConditionDataDicDao.insert(airConditionDataDic);
        }
    }

    private void handleBcuData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BcuDataDicBCU bcuDataDicBCU = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (bcuDataDicBCU != null) {
            bcuDataDicBCU.setName(equipment.getName());
            bcuDataDicBCU.setDeviceName(equipment.getDeviceName());
            bcuDataDicBCU.setGenerationDataTime(strToDate(bcuDataDicBCU.getTime()));
            bcuDataDicBCUDao.insert(bcuDataDicBCU);
        }
    }

    private void handleAmmeterData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        AmmeterDataDic ammeterDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        /**
         * TODO 实时计算单台设备收益--->充放电量*时间段单价
         */
        if (ammeterDataDic != null) {
            ammeterDataDic.setDeviceName(equipment.getDeviceName());
            ammeterDataDic.setName(equipment.getName());
            ammeterDataDic.setGenerationDataTime(strToDate(ammeterDataDic.getTime()));
            saveAndCountIncome(equipment, ammeterDataDic);
            ammeterDataDicDao.insert(ammeterDataDic);
        }
    }

    private void saveAndCountIncome(Equipment equipment, AmmeterDataDic ammeterDataDic) {
        if (ammeterDataDic.getEquipChannelStatus() == 1) {
            return;
        }
        Device device = deviceDao.query().is("deviceName", equipment.getDeviceName()).result();
//        if (device.getApplicationScenarios() == 4) {
//            return;
//        }
        if (device != null) {
            List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
            if (!CollectionUtils.isEmpty(priceOfTime) && priceOfTime.size() > 0) {
                Map<Integer, List<String>> map = null;
                for (TimeOfPriceBo timeOfPriceBo : priceOfTime) {
                    String time = timeOfPriceBo.getTime();
                    String[] split = time.split(",");
                    List<String> list = new ArrayList<>();
                    for (String spl : split) {
                        list.add(spl);
                    }
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(timeOfPriceBo.getType(), list);
                }
                if (map == null) {
                    return;
                }
                Map<Integer, String> typeOfMap = null;
                try {
                    Set<Map.Entry<Integer, List<String>>> entries = map.entrySet();
                    for (Map.Entry<Integer, List<String>> entry : entries) {
                        List<String> entryValue = entry.getValue();
                        for (String value : entryValue) {
                            String[] split = value.split("-");
                            Calendar now = Calendar.getInstance();
                            now.setTime(DateUtil.dateToStrWithHHmm(new Date()));
                            Calendar begin = Calendar.getInstance();
                            begin.setTime(DateUtil.dateToStrWithHHmmWith(split[0]));
                            Calendar end = Calendar.getInstance();
                            end.setTime(DateUtil.dateToStrWithHHmmWith(split[1]));
                            /**
                             * 不相等时候在查看是否在区间内
                             */
                            if (!now.equals(begin) && !now.equals(end)) {
                                if (now.after(begin) && now.before(end)) {
                                    if (typeOfMap == null) {
                                        typeOfMap = new HashMap<>();
                                    }
                                    typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
                                }
                            } else {
                                /**
                                 * 相同的话  会有多个区间出来 取哪个？
                                 */
                                if (typeOfMap == null) {
                                    typeOfMap = new HashMap<>();
                                }
                                typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("解析电表数据错误:{}", e);
                }
                if (typeOfMap == null) {
                    return;
                }
                List<BigDecimal> bigDecimalList = null;
                for (Map.Entry<Integer, String> entry : typeOfMap.entrySet()) {
                    Integer key = entry.getKey();
                    List<TimeOfPriceBo> ofPriceBos = priceOfTime.stream().filter(timeOfPriceBo -> {
                        return timeOfPriceBo.getType() == key;
                    }).collect(Collectors.toList());
                    BigDecimal bigDecimal = new BigDecimal(ofPriceBos.get(0).getPrice());
                    if (bigDecimalList == null) {
                        bigDecimalList = new ArrayList<>();
                    }
                    bigDecimalList.add(bigDecimal);
                }
                /**
                 * 放电降序取第一个  充电升序取第一个
                 * 取出价格列表
                 */
                bigDecimalList = bigDecimalList.stream().sorted(new Comparator<BigDecimal>() {
                    @Override
                    public int compare(BigDecimal o1, BigDecimal o2) {
                        return o2.compareTo(o1);
                    }
                }).collect(Collectors.toList());
                /**
                 * 上一次上报数据电表数据
                 */
                Iterable<DBObject> iterable = ammeterDataDicDao.aggregate().match(ammeterDataDicDao.query().is("deviceName", equipment.getDeviceName())
                        .is("equipmentId", equipment.getEquipmentId()).is("equipChannelStatus", 0)).sort("{generationDataTime:-1}").limit(1).results();
                AmmeterDataDic fromDBObject = null;
                if (iterable != null) {
                    for (DBObject object : iterable) {
                        fromDBObject = MapperUtil.fromDBObject(AmmeterDataDic.class, object);
                    }
                }
                if (fromDBObject != null) {
                    /**
                     * TODO 根据实际情况来取得 充放电 电价
                     */
                    BigDecimal discharDecimal = bigDecimalList.get(0);//放电单价
                    BigDecimal charDecimal = bigDecimalList.get(bigDecimalList.size() - 1);//充电单价

                    BigDecimal lastTimeTotalChargeQuantity = new BigDecimal(fromDBObject.getTotalChargeQuantity());
                    BigDecimal lastTimeTotalDisChargeQuantity = new BigDecimal(fromDBObject.getTotalDischargeQuantity());
                    /**
                     * 当前上报 充电电表数据
                     */
                    BigDecimal thisTimeTotalChargeQuantity = new BigDecimal(ammeterDataDic.getTotalChargeQuantity());
                    BigDecimal thisTimeTotalDisChargeQuantity = new BigDecimal(ammeterDataDic.getTotalDischargeQuantity());

                    //放电
                    BigDecimal dissubResult = thisTimeTotalDisChargeQuantity.subtract(lastTimeTotalDisChargeQuantity);
                    BigDecimal charsubResult = thisTimeTotalChargeQuantity.subtract(lastTimeTotalChargeQuantity);
                    /**
                     * 放电多少钱 充电多少钱
                     */
                    BigDecimal discharResultPrice = discharDecimal.multiply(dissubResult);
                    BigDecimal charResultPrice = charDecimal.multiply(charsubResult);
                    //收益
                    BigDecimal income = discharResultPrice.subtract(charResultPrice);
                    //计算总收益
                    updateIncomeWithTotal(income, device);
                    //计算单天收益
                    countIncomeOfDay(device, income, ammeterDataDic.getGenerationDataTime());
                }
                //第一次上报 为空
            }
        }
    }

    private void countIncomeOfDay(Device device, BigDecimal income, Date recordDate) {
        this.keyIncDecByBigDecimal(recordDate, income, (d) -> {
            return getIncomeKey(d, device);
        });
    }

    private String getIncomeKey(Date recordDate, Device device) {
        return INCOME_RECORD_PREFIX + DateUtil.dateToStr(recordDate) + "_" + device.getProvince() + "_" + device.getCity();
    }

    private void keyIncDecByBigDecimal(Date recordDate, BigDecimal income, Function<Date, String> keyGen) {
        long time = DateUtil.getTodayEndMilSeconds(recordDate);
        if (time < 0 || time > DateUtil.ONE_DAY_MIL_SECONDS) { //表示已经是昨天的记录了 不进行增加
            return;
        }
        Jedis jedis = redisPoolUtil.getJedis();
        String key = null;
        try {
            key = keyGen.apply(recordDate);
            //过期时间为7天
            jedis.incrByFloat(key, income.doubleValue());
            jedis.expire(key, (int) ((time / 1000) + DateUtil.ONE_DAY_MIN_SECONDS));
        } catch (Exception e) {
            log.error("向redis中修改传输数量失败key：{}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void updateIncomeWithTotal(BigDecimal income, Device device) {
        BigDecimal deviceIncome = new BigDecimal(device.getIncome());
        BigDecimal incomeResult = deviceIncome.add(income);
        deviceDao.update().set("income", incomeResult.toString()).execute(deviceDao.query().is("deviceName", device.getDeviceName()));
    }

    private void handleTemperatureData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        TemperatureMeterDataDic temperatureMeterDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (temperatureMeterDataDic != null) {
            temperatureMeterDataDic.setName(equipment.getName());
            temperatureMeterDataDic.setDeviceName(equipment.getDeviceName());
            temperatureMeterDataDic.setGenerationDataTime(strToDate(temperatureMeterDataDic.getTime()));
            temperatureMeterDataDicDao.insert(temperatureMeterDataDic);
        }
    }

    private void handleBmsCellVolData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BmsCellVoltDataDic bmsCellVoltDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (bmsCellVoltDataDic != null) {
            bmsCellVoltDataDic.setName(equipment.getName());
            bmsCellVoltDataDic.setDeviceName(equipment.getDeviceName());
            bmsCellVoltDataDic.setVolMap(getListOfTempAndVol(jsonObject, "Vol"));
            bmsCellVoltDataDic.setGenerationDataTime(strToDate(bmsCellVoltDataDic.getTime()));
            bmsCellVoltDataDicDao.insert(bmsCellVoltDataDic);
        }
    }

    private void handleBamsData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BamsDataDicBA bamsDataDicBA = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (bamsDataDicBA != null) {
            bamsDataDicBA.setDeviceName(equipment.getDeviceName());
            bamsDataDicBA.setName(equipment.getName());
            bamsDataDicBA.setGenerationDataTime(strToDate(bamsDataDicBA.getTime()));
            bamsDataDicBADao.insert(bamsDataDicBA);
        }
        Device device = deviceDao.query().is("deviceName", equipment.getDeviceName()).result();
        if (device != null) {
            Jedis jedis = null;
            try {
                jedis = redisPoolUtil.getJedis();
                /**
                 * 单台总放电量
                 */
                String dischargeCapacitySum = bamsDataDicBA.getDischargeCapacitySum();
                jedis.set(getDischargeKey(device), dischargeCapacitySum);
                /**
                 * 总充电量
                 */
                String chargeCapacitySum = bamsDataDicBA.getChargeCapacitySum();
                jedis.set(getChargeKey(device), chargeCapacitySum);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    private String getChargeKey(Device device) {
        return CHARGECAPACITYSUM + device.getProvince() +"-"+device.getCity()+"-"+device.getDeviceName();
    }

    private String getDischargeKey(Device device) {
        return DISCHARGECAPACITYSUM + device.getProvince() + "-" + device.getCity() + "-" + device.getDeviceName();
    }

    private void handleBmsCellTempData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BmsCellTempDataDic tempDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (tempDataDic != null) {
            tempDataDic.setName(equipment.getName());
            tempDataDic.setDeviceName(equipment.getDeviceName());
            tempDataDic.setTempMap(getListOfTempAndVol(jsonObject, "Temp"));
            tempDataDic.setGenerationDataTime(strToDate(tempDataDic.getTime()));
            bmsCellTempDataDicDao.insert(tempDataDic);
        }
    }

    private void handlePcsChannelData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        PcsChannelDic pcsChannelDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (pcsChannelDic != null) {
            pcsChannelDic.setName(equipment.getName());
            pcsChannelDic.setDeviceName(equipment.getDeviceName());
            pcsChannelDic.setGenerationDataTime(strToDate(pcsChannelDic.getTime()));
            pcsChannelDicDao.insert(pcsChannelDic);
        }
    }

    private void handlePcsData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        PcsCabinetDic pcsCabinetDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (pcsCabinetDic != null) {
            pcsCabinetDic.setName(equipment.getName());
            pcsCabinetDic.setGenerationDataTime(strToDate(pcsCabinetDic.getTime()));
            pcsCabinetDic.setDeviceName(equipment.getDeviceName());
            pcsCabinetDicDao.insert(pcsCabinetDic);
        }
    }

    private void handleUpsPowerData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        UpsPowerDataDic upsPowerDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (upsPowerDataDic != null) {
            upsPowerDataDic.setName(equipment.getName());
            upsPowerDataDic.setDeviceName(equipment.getDeviceName());
            upsPowerDataDic.setGenerationDataTime(strToDate(upsPowerDataDic.getTime()));
            upsPowerDataDicDao.insert(upsPowerDataDic);
        }
    }

    private Date strToDate(String time) {
        try {
            if (StringUtils.hasText(time)) {
                return DateUtil.dateToStrWithT(time);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("转换日期错误", e);
            return null;
        }
    }

    private Map<String, Integer> getListOfTempAndVol(JSONObject jsonObject, String sign) {
        jsonObject.remove("Time");
        jsonObject.remove("EquipChannelStatus");
        jsonObject.remove("EquipmentId");
        String[] temps = jsonObject.toString().replace("{", "").replace("}", "").replace("\"", "").split(",");
        Map<String, Integer> resulMap = new HashMap<>();
        for (String temp : temps) {
            String[] tem = temp.split(":");
            resulMap.put(tem[0], Integer.parseInt(tem[1]));
        }
        if (resulMap == null && resulMap.isEmpty()) {
            return null;
        }
        Map<String, Integer> shortMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String replace1 = o1.replace(sign, "");
                String replace2 = o2.replace(sign, "");
                return Integer.parseInt(replace1) - Integer.parseInt(replace2);
            }
        });
        shortMap.putAll(resulMap);
        return shortMap;
    }

    private void handleBreakDownLog(JMessage jMessage) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("设备告警日志上报 deviceName:{}", deviceName);
            String messagePayloadMsg = jMessage.getPayloadMsg();
            if (!StringUtils.isEmpty(messagePayloadMsg)) {
                JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
                JSONArray dataArray = (JSONArray) jsonObject.get(Const.DATA);
                List<BreakDownLog> breakDownLogs = JSONArray.parseArray(dataArray.toJSONString(), BreakDownLog.class);
                if (breakDownLogs != null && breakDownLogs.size() > 0) {
                    jedis.set("BREAKDOWNLOG-" + deviceName, dataArray.toString());
                    List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();
                    List<BreakDownLog> breakDownLogList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(equipmentDbList) && equipmentDbList.size() > 0) {
                        for (BreakDownLog breakDownLog : breakDownLogs) {
                            Equipment equipment = equipmentDbList.stream().filter(equip -> {
                                return breakDownLog.getEquipmentId() == equip.getEquipmentId();
                            }).findFirst().get();
                            //防止空指针
                            if (equipment == null) {
                                continue;
                            }
                            breakDownLog.setId(null);
                            breakDownLog.setGenerationDataTime(strToDate(breakDownLog.getTime()));
                            breakDownLog.setName(equipment.getName());
                            breakDownLog.setRemoveData(strToDate(breakDownLog.getRemoveTime()));
                            breakDownLog.setDeviceName(equipment.getDeviceName());
                            Date date = DateUtil.addDateDays(new Date(), -1);
                            breakDownLogDao.update()
                                    .set("status", breakDownLog.getStatus())
                                    .set("removeReason", breakDownLog.getRemoveReason())
                                    .set("removeData", breakDownLog.getRemoveData())
                                    .execute(breakDownLogDao.query().is("deviceName", breakDownLog.getDeviceName())
                                            .is("message", breakDownLog.getMessage()).is("status", true)
                                            .greaterThanEquals("generationDataTime", date));
                            breakDownLogList.add(breakDownLog);
                        }
                        if (!CollectionUtils.isEmpty(breakDownLogList) && breakDownLogList.size() > 0) {
                            breakDownLogList = breakDownLogList.stream().sorted(Comparator.comparing(BreakDownLog::getGenerationDataTime)).collect(Collectors.toList());
                            breakDownLogDao.insert(breakDownLogList);
                        }
                    }
                }
            } else {
                log.info("设备告警日志上报为空 Data:{} deviceName:{}", new Date(), deviceName);
            }
        } catch (Exception e) {
            log.error("设备告警处理错误", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    private void handleDeviceInfo(JMessage jMessage) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("设备列表信息上报 deviceName:{}", deviceName);
            JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
            JSONObject dataJson = (JSONObject) jsonObject.get(Const.DATA);
            jedis.set("DEVICEINFO-" + deviceName, dataJson.toString());

            JSONArray stationsArray = (JSONArray) dataJson.get(DEVICE_STATION);
            JSONArray pccsArray = (JSONArray) dataJson.get(DEVICE_PCCS);
            JSONArray cubesArray = (JSONArray) dataJson.get(DEVICE_CUBES);
            JSONArray cabinsArray = (JSONArray) dataJson.get(DEVICE_CABINS);
            JSONArray equipmentsArray = (JSONArray) dataJson.get(DEVICE_EQUIPMENTS);
            List<DeviceStationsInfo> deviceStationsInfos = JSONArray.parseArray(stationsArray.toJSONString(), DeviceStationsInfo.class);
            List<PccsInfo> pccsInfos = JSONArray.parseArray(pccsArray.toJSONString(), PccsInfo.class);
            List<CubesInfo> cubesInfos = JSONArray.parseArray(cubesArray.toJSONString(), CubesInfo.class);
            List<CabinsInfo> cabinsInfos = JSONArray.parseArray(cabinsArray.toJSONString(), CabinsInfo.class);
            List<EquipmentInfo> equipmentInfos = JSONArray.parseArray(equipmentsArray.toJSONString(), EquipmentInfo.class);
            List<Equipment> equipmentList;
            if (equipmentInfos != null && equipmentInfos.size() > 0) {
                equipmentList = equipmentInfos.stream().map(equipmentInfo -> {
                    Equipment equipment = new Equipment();
                    BeanUtils.copyProperties(equipmentInfo, equipment, Const.IGNORE_ID);
                    equipment.setEquipmentId(equipmentInfo.getId());
                    equipment.setDeviceName(deviceName);
                    return equipment;
                }).collect(Collectors.toList());
            } else {
                return;
            }

            updateDeviceList(equipmentList, deviceName);
            /**
             * 判断是否更新设备列表  查询更新后的数据
             */
            List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();

            /**
             * Pccs信息处理 pcc下的三个设备
             */
            pccsMsgHanlder(deviceName, equipmentDbList, deviceStationsInfos, pccsInfos);
            /**
             * 储能箱信息处理
             */
            cubeMsgHanlder(deviceName, equipmentDbList, cubesInfos);
            /**
             * 电池舱信息处理
             */
            cabinMsgHanlder(deviceName, equipmentDbList, cabinsInfos);
        } catch (Exception e) {
            log.error("解析上报设备列表信息失败:{}", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void cabinMsgHanlder(String deviceName, List<Equipment> equipmentDbList, List<CabinsInfo> cabinsInfos) {
        Map<String, List<String>> cabinEquipmentIds = equipmentDbList.stream().filter(equipment -> {
            return equipment.getCabinId() != null && equipment.getCubeId() != null
                    && equipment.getPccId() == null && equipment.getStationId() == null;
        }).collect(Collectors.groupingBy(equipment -> {
            return equipment.getCubeId() + "---" + equipment.getCabinId();
        }, Collectors.mapping(Equipment::getId, Collectors.toList())));

        /**
         * 单独摄像头的
         */
        Map<Integer, List<String>> cameraEquipmentIds = equipmentDbList.stream().filter(equipment -> {
            return equipment.getCabinId() != null && equipment.getCubeId() == null
                    && equipment.getPccId() == null && equipment.getStationId() == null;
        }).collect(Collectors.groupingBy(Equipment::getCabinId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Cabin> cabinList = cabinsInfos.stream().map(cabinsInfo -> {
            Cabin cabin = new Cabin();
            BeanUtils.copyProperties(cabinsInfo, cabin, Const.IGNORE_ID);
            Cube cube = cubeDao.query().is("deviceName", deviceName).is("cubeId", cabin.getCubeId()).result();
            cabin.setDeviceName(deviceName);
            cabin.setCabinId(cabinsInfo.getId());
            if (cube != null) {
                cabin.setStationId(cube.getStationId());
                cabin.setPccId(cube.getPccId());
            }
            List<String> equipmentIds = new ArrayList<>();
            List<String> cubeCabinIds = cabinEquipmentIds.get(cabin.getCubeId() + "---" + cabin.getCabinId());
            if (cubeCabinIds != null && cubeCabinIds.size() > 0) {
                equipmentIds.addAll(cubeCabinIds);
            }
            List<String> cabinIds = cameraEquipmentIds.get(cabin.getCabinId());
            if (cabinIds != null && cabinIds.size() > 0) {
                equipmentIds.addAll(cabinIds);
            }
            cabin.setEquipmentIds(equipmentIds);
            return cabin;
        }).collect(Collectors.toList());
        for (Cabin cabin : cabinList) {
            BuguQuery<Cabin> query = cabinDao.query().is("deviceName", deviceName).is("stationId", cabin.getStationId())
                    .is("pccId", cabin.getPccId()).is("cubeId", cabin.getCubeId())
                    .is("cabinId", cabin.getCabinId());
            if (query.exists()) {
                cabinDao.update().set("equipmentIds", cabin.getEquipmentIds())
                        .set("cabinType", cabin.getCabinType())
                        .set("name", cabin.getName())
                        .execute(query);
            } else {
                cabinDao.insert(cabinList);
            }
        }
    }

    private void cubeMsgHanlder(String deviceName, List<Equipment> equipmentDbList, List<CubesInfo> cubesInfos) {
        Map<Integer, List<String>> cubeEquipmentIds = equipmentDbList.stream().filter(equipment -> {
            return equipment.getCabinId() == null && equipment.getCubeId() != null
                    && equipment.getPccId() == null && equipment.getStationId() == null;
        }).collect(Collectors.groupingBy(Equipment::getCubeId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Cube> cubeList = cubesInfos.stream().map(cubesInfo -> {
            Cube cube = new Cube();
            BeanUtils.copyProperties(cubesInfo, cube, Const.IGNORE_ID);
            Pccs pccs = pccsDao.query().is("pccsId", cubesInfo.getPccId()).is("deviceName", deviceName).result();
            cube.setCubeId(cubesInfo.getId());
            cube.setDeviceName(deviceName);
            cube.setEquipmentIds(cubeEquipmentIds.get(cube.getCubeId()));
            if (pccs != null) {
                cube.setStationId(pccs.getStationId());
            }
            return cube;
        }).collect(Collectors.toList());
        for (Cube cube : cubeList) {
            BuguQuery<Cube> query = cubeDao.query().is("deviceName", deviceName).is("stationId", cube.getStationId())
                    .is("pccId", cube.getPccId()).is("cubeId", cube.getCubeId());
            if (query.exists()) {
                cubeDao.update().set("equipmentIds", cube.getEquipmentIds())
                        .set("batteryType", cube.getBatteryType())
                        .set("batteryLevel", cube.getBatteryLevel())
                        .set("batteryChargeDischargeRate", cube.getBatteryChargeDischargeRate())
                        .set("cubeModel", cube.getCubeModel())
                        .set("cubeCapacity", cube.getCubeCapacity())
                        .set("cubePower", cube.getCubePower())
                        .set("batteryManufacturer", cube.getBatteryManufacturer())
                        .set("cellCapacity", cube.getCellCapacity())
                        .set("coolingMode", cube.getCoolingMode())
                        .set("name", cube.getName())
                        .set("description", cube.getDescription())
                        .execute(query);
            } else {
                cubeDao.insert(cube);
            }
        }
    }

    private void pccsMsgHanlder(String deviceName, List<Equipment> equipmentDbList, List<DeviceStationsInfo> deviceStationsInfos, List<PccsInfo> pccsInfos) {
        /**
         * 设备信息处理
         */
        DeviceStationsInfo deviceStationsInfo = deviceStationsInfos.get(0);
        Device device = new Device();
        BeanUtils.copyProperties(deviceStationsInfo, device, Const.IGNORE_ID);
        device.setStationId(deviceStationsInfo.getId());
        device.setDeviceName(deviceName);

        Map<Integer, List<String>> stationEquipmentIds = equipmentDbList.stream().filter(equipment -> {
            return equipment.getCabinId() == null && equipment.getCubeId() == null
                    && equipment.getPccId() == null && equipment.getStationId() != null;
        }).collect(Collectors.groupingBy(Equipment::getStationId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        device.setEquipmentIds(stationEquipmentIds.get(device.getStationId()));

        Map<Integer, List<String>> pccEquipmentIds = equipmentDbList.stream().filter(equipment -> {
            return equipment.getCabinId() == null && equipment.getCubeId() == null
                    && equipment.getPccId() != null && equipment.getStationId() == null;
        }).collect(Collectors.groupingBy(Equipment::getPccId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Pccs> pccsList = pccsInfos.stream().map(pccsInfo -> {
            Pccs pccs = new Pccs();
            BeanUtils.copyProperties(pccsInfo, pccs, Const.IGNORE_ID);
            pccs.setPccsId(pccsInfo.getId());
            pccs.setDeviceName(deviceName);
            pccs.setEquipmentIds(pccEquipmentIds.get(pccsInfo.getId()));
            return pccs;
        }).collect(Collectors.toList());
        for (Pccs pccs : pccsList) {
            Pccs pccs1 = pccsDao.query().is("deviceName", deviceName)
                    .is("stationId", pccs.getStationId())
                    .is("pccsId", pccs.getPccsId()).result();
            if (pccs1 != null) {
                pccsDao.update().set("equipmentIds", pccs.getEquipmentIds())
                        .set("name", pccs.getName())
                        .set("description", pccs.getDescription())
                        .execute(pccs1);
            } else {
                pccsDao.insert(pccs);
            }
        }
        List<String> pccIds = pccsList.stream().map(pccs -> {
            return pccs.getId();
        }).collect(Collectors.toList());
        device.setPccsIds(pccIds);
        device.setAccessTime(new Date());
        if (deviceDao.query().is("deviceName", deviceName)
                .is("stationId", device.getStationId()).exists()) {
            deviceDao.update().set("equipmentIds", device.getEquipmentIds())
                    .set("name", device.getName())
                    .set("description", device.getDescription())
                    .set("stationCapacity", device.getStationCapacity())
                    .set("stationPower", device.getStationPower())
                    .set("province", device.getProvince())
                    .set("city", device.getCity())
                    .set("longitude", device.getLongitude())
                    .set("latitude", device.getLatitude())
                    .set("applicationScenarios", device.getApplicationScenarios())
                    .set("applicationScenariosItem", device.getApplicationScenariosItem())
                    .execute(deviceDao.query().is("deviceName", deviceName)
                            .is("stationId", device.getStationId()));
        } else {
            deviceDao.insert(device);
        }
    }

    private void updateDeviceList(List<Equipment> equipmentList, String deviceName) {
        if (CollectionUtils.isEmpty(equipmentList)) {
            return;
        }
        /**
         * 判断是否更新设备列表
         */
        List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();

        /**
         * 是否不同
         */
        List<Integer> newIds = new ArrayList<>();
        List<Integer> oldIds = new ArrayList<>();
        /**
         * 根据新上报的ID更新数据  分别取出两个集合ID
         */
        if (equipmentDbList != null && equipmentDbList.size() > 0) {
            equipmentDbList.stream().forEach(equipmentDb -> {
                oldIds.add(equipmentDb.getEquipmentId());
            });
        }
        if (equipmentList != null && equipmentList.size() > 0) {
            equipmentList.stream().forEach(equipment -> {
                newIds.add(equipment.getEquipmentId());
            });
        }

        if (oldIds.contains(newIds)) {
            log.info("设备信息列表无需更新");
        } else {
            if (equipmentDbList == null && equipmentDbList.size() == 0) {
                /**
                 * 首次插入
                 */
                equipmentDao.insert(equipmentList);
            } else {
                /**
                 * 删除
                 */
                List<Integer> collectDelete = oldIds.stream().filter(it -> !newIds.contains(it)).collect(Collectors.toList());
                if (collectDelete != null && collectDelete.size() > 0) {
                    for (Integer deleteId : collectDelete) {
                        equipmentDao.remove(equipmentDao.query().is("deviceName", deviceName).is("equipmentId", deleteId));
                    }
                }
                /**
                 * 新增
                 */
                List<Integer> collectAdd = newIds.stream().filter(it -> !oldIds.contains(it)).collect(Collectors.toList());
                if (collectAdd != null && collectAdd.size() > 0) {
                    List<Equipment> addEquipment = new ArrayList<>();
                    for (Equipment equipment : equipmentList) {
                        if (collectAdd.contains(equipment.getEquipmentId())) {
                            addEquipment.add(equipment);
                        }
                    }
                    equipmentDao.insert(addEquipment);
                }
                /**
                 * 已经存在设备进行信息更新
                 */
                List<Equipment> equipments = equipmentList.stream().filter(equipment -> {
                    return oldIds.contains(equipment.getEquipmentId());
                }).collect(Collectors.toList());
                equipments.stream().forEach(oldEquipment -> {
                    equipmentDao.update().set("enabled", oldEquipment.isEnabled())
                            .set("model", oldEquipment.getModel())
                            .set("manufacturer", oldEquipment.getManufacturer())
                            .set("stationId", oldEquipment.getStationId())
                            .set("description", oldEquipment.getDescription())
                            .set("name", oldEquipment.getName())
                            .set("pccid", oldEquipment.getPccId())
                            .set("cubeId", oldEquipment.getCubeId())
                            .set("cabinId", oldEquipment.getCabinId())
                            .execute(equipmentDao.query().is("deviceName", deviceName)
                                    .is("equipmentId", oldEquipment.getEquipmentId()));
                });
            }
        }
    }

    /**
     * 修改告警日志状态
     */
    @Scheduled(cron = "${untrans.report.interval}")
    public void editBreakDownlogStatus() {
        int alterBreakdownlogStatusStart = propertiesConfig.getAlterBreakdownlogStatusStart();
        int alterBreakdownlogStatusEnd = propertiesConfig.getAlterBreakdownlogStatusEnd();
        Date startTime = DateUtil.addDateDays(new Date(), -alterBreakdownlogStatusStart);
        Date endTime = DateUtil.addDateDays(new Date(), -alterBreakdownlogStatusEnd);
        breakDownLogDao.update().set("status", false)
                .set("removeReason", "自动恢复")
                .set("removeData", new Date()).execute(breakDownLogDao.query().is("status", true)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime));
        log.info("定时执行告警日志信息状态自动恢复:{}", new Date());
    }
}
