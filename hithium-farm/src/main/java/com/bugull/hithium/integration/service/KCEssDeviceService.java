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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithium.core.common.Const.*;


@Component("kCEssDeviceService")
public class KCEssDeviceService {
    private static final Logger log = LoggerFactory.getLogger(KCEssDeviceService.class);

    protected static final Map<String, Object> REAL_TIME_DATA_TYPE = new HashMap<>();

    protected static final String INCOME_RECORD_PREFIX = "DEIVCE_INCOME_RECORED_";

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
    @Resource
    private IncomeEntityDao incomeEntityDao;
    @Resource
    private BamsDischargeCapacityDao bamsDischargeCapacityDao;

    /**
     * ????????????
     */
    public void handle(Message<JMessage> messageMessage) {
        JMessage jMessage = messageMessage.getPayload();
        DataType dataType = jMessage.getDataType();
        try {
            switch (dataType) {
                //????????????????????????
                case DEVICE_LIST_DATA:
                    handleDeviceInfo(jMessage);
                    break;
                //????????????????????????
                case BREAKDOWNLOG_DATA:
                    handleBreakDownLog(jMessage);
                    break;
                //????????????????????????
                case REAL_TIME_DATA:
                    handleRealTimeData(jMessage);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("??????????????????:{},dataType:{}", e, dataType);
        }
    }

    private void handleRealTimeData(JMessage jMessage) {
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("????????????????????????:{}", deviceName);
            JSONObject jsonObject = JSON.parseObject(jMessage.getPayloadMsg());
            JSONObject datajson = (JSONObject) jsonObject.get(Const.DATA);
//            List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).results();
            List<Equipment> equipmentList = equipmentDao.queryEquipment(new Query().addCriteria(Criteria.where("deviceName").is(deviceName)));
            if (!CollectionUtils.isEmpty(equipmentList) && equipmentList.size() > 0) {
                if (propertiesConfig.isRedisCacheSwitch()) {
                    cacheRealData(datajson, deviceName);
                }
                resolvingRealDataTime(datajson, equipmentList);
            } else {
                log.info("?????????????????????????????????????????????:{}", deviceName);
            }
        } catch (Exception e) {
            log.error("????????????????????????????????????,??????:{},??????:{}", jMessage.getPayloadMsg(), e);
        }
    }

    private void cacheRealData(JSONObject datajson, String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            jedis.set("REALDATA-" + deviceName, datajson.toString());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void resolvingRealDataTime(JSONObject datajson, List<Equipment> equipmentList) {
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

    private void parseSaveData(Map.Entry<String, Object> entry, JSONObject jsonObject, Equipment equipment) {
        switch (entry.getKey()) {
            case Const.REAL_TIME_DATATYPE_AMMETER:
                handleAmmeterData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_BAMS:
                handleBamsData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_BMSCELLTEMP:
                handleBmsCellTempData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_BMSCELLVOLT:
                handleBmsCellVolData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_BCU:
                handleBcuData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_PCSCABINET:
                handlePcsData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_PCSCHANNEL:
                handlePcsChannelData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_AIRCONDITION:
                handleAirConditionData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_TEMPERATUREMETER:
                handleTemperatureData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_FIRECONTROLDATA:
                handleFireControlData(jsonObject, entry, equipment);
                break;
            case REAL_TIME_DATATYPE_UPSPOWERDATA:
                handleUpsPowerData(jsonObject, entry, equipment);
                break;
            default:
                log.error("????????????");
        }
    }

    private void handleFireControlData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        FireControlDataDic fireControlDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (fireControlDataDic != null) {
            if (fireControlDataDic.getEquipChannelStatus() == 1) {
                log.info("??????????????????????????????????????? ?????????????????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            fireControlDataDic.setName(equipment.getName());
            fireControlDataDic.setDeviceName(equipment.getDeviceName());
            fireControlDataDic.setGenerationDataTime(strToDate(fireControlDataDic.getTime()));
            fireControlDataDicDao.saveFireControlData(fireControlDataDic);
//            fireControlDataDicDao.insert(fireControlDataDic);
        }
    }

    private void handleAirConditionData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        AirConditionDataDic airConditionDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (airConditionDataDic != null) {
            if (airConditionDataDic.getEquipChannelStatus() == 1) {
                log.info("????????????????????????????????? ?????????????????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            airConditionDataDic.setName(equipment.getName());
            airConditionDataDic.setDeviceName(equipment.getDeviceName());
            airConditionDataDic.setGenerationDataTime(strToDate(airConditionDataDic.getTime()));
            airConditionDataDicDao.saveAirConditionData(airConditionDataDic);
//            airConditionDataDicDao.insert(airConditionDataDic);
        }
    }

    private void handleBcuData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BcuDataDicBCU bcuDataDicBCU = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (bcuDataDicBCU != null) {
            if (bcuDataDicBCU.getEquipChannelStatus() == 1) {
                log.info("BCU?????? ??????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            bcuDataDicBCU.setName(equipment.getName());
            bcuDataDicBCU.setDeviceName(equipment.getDeviceName());
            bcuDataDicBCU.setGenerationDataTime(strToDate(bcuDataDicBCU.getTime()));
            bcuDataDicBCUDao.saveBcuData(bcuDataDicBCU);
//            bcuDataDicBCUDao.insert(bcuDataDicBCU);
        }
    }

    private void handleAmmeterData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        AmmeterDataDic ammeterDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        /**
         * TODO ??????????????????????????????--->????????????*???????????????
         */
        if (ammeterDataDic != null) {
            if (ammeterDataDic.getEquipChannelStatus() == 1) {
                log.info("??????????????????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            ammeterDataDic.setDeviceName(equipment.getDeviceName());
            ammeterDataDic.setName(equipment.getName());
            ammeterDataDic.setGenerationDataTime(strToDate(ammeterDataDic.getTime()));
            saveAndCountIncome(equipment, ammeterDataDic);
            ammeterDataDicDao.saveAmmeterDataDicDao(ammeterDataDic);
        }
    }

    private void saveAndCountIncome(Equipment equipment, AmmeterDataDic ammeterDataDic) {
        if (equipment.getEquipmentId() != 4 && propertiesConfig.getProductionDeviceNameList().contains(equipment.getDeviceName())) {
            log.info("??????{}?????? ??????ID{}?????? ???????????????", equipment.getDeviceName(), equipment.getEquipmentId());
            return;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(equipment.getDeviceName()));
        Device device = deviceDao.findDevice(query);
//        Device device = deviceDao.query().is("deviceName", equipment.getDeviceName()).result();
        //TODO ??????????????????????????????
        if (device.getApplicationScenarios() == 4) {
            return;
        }

        if (device != null) {
            List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
            if (!CollectionUtils.isEmpty(priceOfTime) && priceOfTime.size() > 0) {
                Map<Integer, List<String>> map = null;
                for (TimeOfPriceBo timeOfPriceBo : priceOfTime) {
                    String time = timeOfPriceBo.getTime();
                    String[] split = time.split(",");
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(timeOfPriceBo.getType(), Arrays.asList(split));
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
                            now.setTime(DateUtil.dateToStrWithHHmm(ammeterDataDic.getGenerationDataTime()));
                            Calendar begin = Calendar.getInstance();
                            begin.setTime(DateUtil.dateToStrWithHHmmWith(split[0]));
                            Calendar end = Calendar.getInstance();
                            end.setTime(DateUtil.dateToStrWithHHmmWith(split[1]));
                            /**
                             * ??????????????????????????????????????????
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
                                 * ????????????  ???????????????????????? ????????????
                                 */
                                if (typeOfMap == null) {
                                    typeOfMap = new HashMap<>();
                                }
                                typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("????????????????????????", e);
                }
                if (typeOfMap == null) {
                    return;
                }
                List<BigDecimal> bigDecimalList = null;
                for (Map.Entry<Integer, String> entry : typeOfMap.entrySet()) {
                    Integer key = entry.getKey();
                    List<TimeOfPriceBo> ofPriceBos = priceOfTime.stream().filter(timeOfPriceBo -> timeOfPriceBo.getType() == key).collect(Collectors.toList());
                    BigDecimal bigDecimal = new BigDecimal(ofPriceBos.get(0).getPrice());
                    if (bigDecimalList == null) {
                        bigDecimalList = new ArrayList<>();
                    }
                    bigDecimalList.add(bigDecimal);
                }
                /**
                 * ????????????????????????  ????????????????????????
                 * ??????????????????
                 */
                if (!CollectionUtils.isEmpty(bigDecimalList) && !bigDecimalList.isEmpty()) {
                    bigDecimalList = bigDecimalList.stream().sorted(new Comparator<BigDecimal>() {
                        @Override
                        public int compare(BigDecimal o1, BigDecimal o2) {
                            return o2.compareTo(o1);
                        }
                    }).collect(Collectors.toList());
                    /**
                     * ?????????????????????????????????
                     */
                    Criteria criteria = new Criteria();
                    Aggregation aggregation = Aggregation.newAggregation(
                            Aggregation.match(criteria.andOperator(Criteria.where("deviceName").is(equipment.getDeviceName())).andOperator(
                                    Criteria.where("equipmentId").is(equipment.getEquipmentId())
                            )), Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))),
                            Aggregation.limit(1)
                    );

                    AmmeterDataDic fromAggregation = ammeterDataDicDao.fromAggregation(aggregation);
//                    Iterable<DBObject> iterable = ammeterDataDicDao.aggregate().match(ammeterDataDicDao.query().is("deviceName", equipment.getDeviceName())
//                            .is("equipmentId", equipment.getEquipmentId())).sort("{generationDataTime:-1}").limit(1).results();
//                    AmmeterDataDic fromDBObject = null;
//                    if (iterable != null) {
//                        for (DBObject object : iterable) {
//                            fromDBObject = MapperUtil.fromDBObject(AmmeterDataDic.class, object);
//                        }
//                    }
                    if (fromAggregation != null) {
                        /**
                         * TODO ??????????????????????????? ????????? ??????
                         * ?????????????????????????????????????????????????????????  ?????????????????????????????????????????? ??????????????????????????????
                         *
                         * ???????????????????????????????????????????????????????????? ??????????????????
                         */
                        if (ammeterDataDic.getGenerationDataTime().after(fromAggregation.getGenerationDataTime())) {
                            BigDecimal discharDecimal = bigDecimalList.get(0);//????????????
                            BigDecimal charDecimal = bigDecimalList.get(bigDecimalList.size() - 1);//????????????

                            BigDecimal lastTimeTotalChargeQuantity = new BigDecimal(fromAggregation.getTotalChargeQuantity());
                            BigDecimal lastTimeTotalDisChargeQuantity = new BigDecimal(fromAggregation.getTotalDischargeQuantity());
                            /**
                             * ???????????? ??????????????????
                             */
                            BigDecimal thisTimeTotalChargeQuantity = new BigDecimal(ammeterDataDic.getTotalChargeQuantity());
                            BigDecimal thisTimeTotalDisChargeQuantity = new BigDecimal(ammeterDataDic.getTotalDischargeQuantity());

                            //??????
                            BigDecimal dissubResult = thisTimeTotalDisChargeQuantity.subtract(lastTimeTotalDisChargeQuantity);
                            BigDecimal charsubResult = thisTimeTotalChargeQuantity.subtract(lastTimeTotalChargeQuantity);
                            /**
                             * ??????????????? ???????????????
                             */
                            BigDecimal discharResultPrice = discharDecimal.multiply(dissubResult);
                            BigDecimal charResultPrice = charDecimal.multiply(charsubResult);
                            //??????
                            BigDecimal income = discharResultPrice.subtract(charResultPrice);
                            //???????????????  ??????????????????
                            updateIncomeWithTotal(income, device);
                            /**
                             * key  ????????????  ?????????
                             *  ??????-??????-
                             *  ??????????????? ?????? ???????????? ????????? ?????? ????????????????????????
                             */
                            updateIncomeOfDay(income, device);
                        }
                    }
                }
                //??????????????? ??????
            }
        }
    }

    private void updateIncomeOfDay(BigDecimal income, Device device) {
        Date date = new Date();
        String dateToStr = DateUtil.dateToStr(date);
        Query query = new Query();
        query.addCriteria(Criteria.where("incomeOfDay").is(dateToStr)).addCriteria(Criteria.where("deviceName").is(device.getDeviceName()));
//        BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query().is("incomeOfDay", dateToStr).is("deviceName", device.getDeviceName());
        if (incomeEntityDao.existQuery(query)) {
            IncomeEntity incomeResult = incomeEntityDao.findIncomeEntity(query);
//            IncomeEntity incomeResult = incomeEntityBuguQuery.result();
            BigDecimal resultDecimal = incomeResult.getIncome().add(income);
            Update update = new Update();
            update.set("income", resultDecimal);
            incomeEntityDao.updateQuery(query, update);
        } else {
            IncomeEntity incomeEntity = new IncomeEntity();
            incomeEntity.setDeviceName(device.getDeviceName());
            incomeEntity.setIncomeOfDay(dateToStr);
            incomeEntity.setIncome(income);
            incomeEntity.setProvince(device.getProvince());
            incomeEntity.setCity(device.getCity());
            incomeEntityDao.saveIncomeEntity(incomeEntity);
        }
    }


    private void updateIncomeWithTotal(BigDecimal income, Device device) {
        BigDecimal deviceIncome = new BigDecimal(device.getIncome());
        BigDecimal incomeResult = deviceIncome.add(income);
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(device.getDeviceName()));
        Update update = new Update();
        update.set("income", incomeResult.toString());
        deviceDao.updateFirst(query, update);
//        deviceDao.update().set("income", incomeResult.toString()).execute(deviceDao.query().is("deviceName", device.getDeviceName()));
    }

    private void handleTemperatureData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        TemperatureMeterDataDic temperatureMeterDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (temperatureMeterDataDic != null) {
            if (temperatureMeterDataDic.getEquipChannelStatus() == 1) {
                log.info("????????????????????????????????????????????? ?????????????????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            temperatureMeterDataDic.setName(equipment.getName());
            temperatureMeterDataDic.setDeviceName(equipment.getDeviceName());
            temperatureMeterDataDic.setGenerationDataTime(strToDate(temperatureMeterDataDic.getTime()));
            temperatureMeterDataDicDao.saveTemperatureMeterData(temperatureMeterDataDic);
//            temperatureMeterDataDicDao.insert(temperatureMeterDataDic);
        }
    }

    private void handleBmsCellVolData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BmsCellVoltDataDic bmsCellVoltDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (bmsCellVoltDataDic != null) {
            if (bmsCellVoltDataDic.getEquipChannelStatus() == 1) {
                log.info("?????????????????? ??????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            bmsCellVoltDataDic.setName(equipment.getName());
            bmsCellVoltDataDic.setDeviceName(equipment.getDeviceName());
            bmsCellVoltDataDic.setVolMap(getListOfTempAndVol(jsonObject, "Vol"));
            bmsCellVoltDataDic.setGenerationDataTime(strToDate(bmsCellVoltDataDic.getTime()));
            bmsCellVoltDataDicDao.saveBmsCellVoltData(bmsCellVoltDataDic);
//            bmsCellVoltDataDicDao.insert(bmsCellVoltDataDic);
        }
    }

    private void handleBamsData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BamsDataDicBA bamsDataDicBA = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(equipment.getDeviceName()));
        Device device = deviceDao.findDevice(query);
//        Device device = deviceDao.query().is("deviceName", equipment.getDeviceName()).result();
        if (bamsDataDicBA != null && device != null) {
            if (bamsDataDicBA.getEquipChannelStatus() == 1) {
                log.info("BAMS????????????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            bamsDataDicBA.setDeviceName(equipment.getDeviceName());
            bamsDataDicBA.setName(equipment.getName());
            bamsDataDicBA.setGenerationDataTime(strToDate(bamsDataDicBA.getTime()));
            updateChargeCapacityNumber(device, bamsDataDicBA, equipment);
            bamsDataDicBADao.saveBamDataDic(bamsDataDicBA);
//            bamsDataDicBADao.insert(bamsDataDicBA);
        }
    }

    private void updateChargeCapacityNumber(Device device, BamsDataDicBA bamsDataDicBA, Equipment equipment) {
        String dischargeCapacitySum = bamsDataDicBA.getDischargeCapacitySum();
        String chargeCapacitySum = bamsDataDicBA.getChargeCapacitySum();

        Criteria criteria = new Criteria();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria.andOperator(Criteria.where("deviceName").is(device.getDeviceName())).andOperator(
                        Criteria.where("equipmentId").is(equipment.getEquipmentId())
                )), Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))),
                Aggregation.limit(1));

        BamsDataDicBA bamDataDicBAAggregation = bamsDataDicBADao.aggregationBamData(aggregation);

//        Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is("deviceName", device.getDeviceName())
//                .is("equipmentId", equipment.getEquipmentId()).sort("{generationDataTime:-1}")).limit(1).results();
//        BamsDataDicBA bamsDataDicObject = null;
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                bamsDataDicObject = MapperUtil.fromDBObject(BamsDataDicBA.class, object);
//            }
//        }
        BamsDischargeCapacity bamsDischargeCapacity = new BamsDischargeCapacity();
        bamsDischargeCapacity.setDischargeCapacitySum(new BigDecimal(dischargeCapacitySum));
        bamsDischargeCapacity.setChargeCapacitySum(new BigDecimal(chargeCapacitySum));
        bamsDischargeCapacity.setGenerationDataTime(bamsDataDicBA.getGenerationDataTime());
        bamsDischargeCapacity.setDeviceName(device.getDeviceName());
        bamsDischargeCapacity.setEquipmentId(equipment.getEquipmentId());
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(device.getDeviceName()));
        Update update = new Update();
        update.set("dischargeCapacitySum", dischargeCapacitySum).set("chargeCapacitySum", chargeCapacitySum);
        if (bamDataDicBAAggregation == null) {
            deviceDao.updateFirst(query, update);
            //???????????? ???????????? ?????????????????? ?????????????????????  ???????????? ?????????????????????
            //???????????????????????????????????????
            bamsDischargeCapacity.setDischargeCapacitySubtract(new BigDecimal(dischargeCapacitySum));
            bamsDischargeCapacity.setChargeCapacitySubtract(new BigDecimal(chargeCapacitySum));
            bamsDischargeCapacityDao.saveBamDischargeCapacity(bamsDischargeCapacity);
//            bamsDischargeCapacityDao.insert(bamsDischargeCapacity);
        } else {
            if (bamsDataDicBA.getGenerationDataTime().after(bamDataDicBAAggregation.getGenerationDataTime())) {
                deviceDao.updateFirst(query, update);
            }
            Aggregation dischargeCapacityAggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria.andOperator(Criteria.where("deviceName").is(device.getDeviceName())).andOperator(
                            Criteria.where("equipmentId").is(equipment.getEquipmentId())
                    )), Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))),
                    Aggregation.limit(1));
            BamsDischargeCapacity aggregateDischargeCapacity = bamsDischargeCapacityDao.aggregateDischargeCapacityData(dischargeCapacityAggregation);
//            Iterable<DBObject> bamsDischargeCapacityIterable = bamsDischargeCapacityDao.aggregate().match(bamsDischargeCapacityDao.query().is("deviceName", device.getDeviceName())
//                    .is("equipmentId", equipment.getEquipmentId())).sort("{generationDataTime:-1}").limit(1).results();
//            BamsDischargeCapacity bamsDischargeCapacityIterableEntity = null;
//            if (bamsDischargeCapacityIterable != null) {
//                for (DBObject object : bamsDischargeCapacityIterable) {
//                    bamsDischargeCapacityIterableEntity = MapperUtil.fromDBObject(BamsDischargeCapacity.class, object);
//                }
//            }
            if (aggregateDischargeCapacity != null) {
                BigDecimal dischargeCapacitySumOld = aggregateDischargeCapacity.getDischargeCapacitySum();
                BigDecimal dischargeCapacitySumNew = new BigDecimal(dischargeCapacitySum);
                BigDecimal dischargeCapacitySubtractResult = dischargeCapacitySumNew.subtract(dischargeCapacitySumOld);
                bamsDischargeCapacity.setDischargeCapacitySubtract(dischargeCapacitySubtractResult);
                BigDecimal chargeCapacitySumOld = aggregateDischargeCapacity.getChargeCapacitySum();
                BigDecimal chargeCapacitySumNew = new BigDecimal(chargeCapacitySum);
                BigDecimal chargeCapacitySubtractResult = chargeCapacitySumNew.subtract(chargeCapacitySumOld);
                bamsDischargeCapacity.setChargeCapacitySubtract(chargeCapacitySubtractResult);
            } else {
                /**
                 * ?????? ?????????????????? ??????bams??????????????????
                 */
                bamsDischargeCapacity.setDischargeCapacitySubtract(new BigDecimal(dischargeCapacitySum));
                bamsDischargeCapacity.setChargeCapacitySubtract(new BigDecimal(chargeCapacitySum));
            }
            if (bamsDischargeCapacity.getDischargeCapacitySubtract().compareTo(new BigDecimal("0")) == 0 &&
                    bamsDischargeCapacity.getChargeCapacitySubtract().compareTo(new BigDecimal("0")) == 0) {
                return;
            }
            bamsDischargeCapacityDao.saveBamDischargeCapacity(bamsDischargeCapacity);
//            bamsDischargeCapacityDao.insert(bamsDischargeCapacity);
        }
    }

    private String getChargeKey(Device device) {
        return CHARGECAPACITYSUM + device.getProvince() + "-" + device.getCity() + "-" + device.getDeviceName();
    }

    private String getDischargeKey(Device device) {
        return DISCHARGECAPACITYSUM + device.getProvince() + "-" + device.getCity() + "-" + device.getDeviceName();
    }

    private void handleBmsCellTempData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        BmsCellTempDataDic tempDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (tempDataDic != null) {
            if (tempDataDic.getEquipChannelStatus() == 1) {
                log.info("????????????????????????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            tempDataDic.setName(equipment.getName());
            tempDataDic.setDeviceName(equipment.getDeviceName());
            tempDataDic.setTempMap(getListOfTempAndVol(jsonObject, "Temp"));
            tempDataDic.setGenerationDataTime(strToDate(tempDataDic.getTime()));
            bmsCellTempDataDicDao.saveCellTempData(tempDataDic);
//            bmsCellTempDataDicDao.insert(tempDataDic);
        }
    }

    private void handlePcsChannelData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        PcsChannelDic pcsChannelDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (pcsChannelDic != null) {
            if (pcsChannelDic.getEquipChannelStatus() == 1) {
                log.info("PCS??????????????????????????????????????? ???????????? ?????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            pcsChannelDic.setName(equipment.getName());
            pcsChannelDic.setDeviceName(equipment.getDeviceName());
            pcsChannelDic.setGenerationDataTime(strToDate(pcsChannelDic.getTime()));
            pcsChannelDicDao.savePcsChannel(pcsChannelDic);
//            pcsChannelDicDao.insert(pcsChannelDic);
        }
    }

    private void handlePcsData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        PcsCabinetDic pcsCabinetDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (pcsCabinetDic != null) {
            if (pcsCabinetDic.getEquipChannelStatus() == 1) {
                log.info("PCS???????????? ??????????????????????????? ?????????????????? ????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            pcsCabinetDic.setName(equipment.getName());
            pcsCabinetDic.setGenerationDataTime(strToDate(pcsCabinetDic.getTime()));
            pcsCabinetDic.setDeviceName(equipment.getDeviceName());
            pcsCabinetDicDao.savePcsCabinetDic(pcsCabinetDic);
//            pcsCabinetDicDao.insert(pcsCabinetDic);
        }
    }

    private void handleUpsPowerData(JSONObject jsonObject, Map.Entry<String, Object> entry, Equipment equipment) {
        UpsPowerDataDic upsPowerDataDic = JSON.parseObject(jsonObject.toString(), (Type) entry.getValue());
        if (upsPowerDataDic != null) {
            if (upsPowerDataDic.getEquipChannelStatus() == 1) {
                log.info("UPS????????????????????????????????? ??????????????????,????????????:{},????????????:{}", equipment.getDeviceName(), equipment.getName());
                return;
            }
            upsPowerDataDic.setName(equipment.getName());
            upsPowerDataDic.setDeviceName(equipment.getDeviceName());
            upsPowerDataDic.setGenerationDataTime(strToDate(upsPowerDataDic.getTime()));
            upsPowerDataDicDao.saveUpsPowerData(upsPowerDataDic);

//            upsPowerDataDicDao.insert(upsPowerDataDic);
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
            log.error("??????????????????", e);
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
        if (resulMap.isEmpty()) {
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
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("???????????????????????? deviceName:{}", deviceName);
            String messagePayloadMsg = jMessage.getPayloadMsg();
            List<BreakDownLog> breakDownLogs = getBreakDownLogList(messagePayloadMsg, deviceName);
            if (!CollectionUtils.isEmpty(breakDownLogs) && !breakDownLogs.isEmpty()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("deviceName").is(deviceName));
                List<Equipment> equipmentDbList = equipmentDao.queryEquipment(query);
//                List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();
                List<BreakDownLog> breakDownLogList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(equipmentDbList) && !equipmentDbList.isEmpty()) {
                    for (BreakDownLog breakDownLog : breakDownLogs) {
                        Equipment equipment = equipmentDbList.stream().filter(equip ->
                                breakDownLog.getEquipmentId() == equip.getEquipmentId()).findFirst().get();
                        //???????????????
                        if (equipment == null) {
                            continue;
                        }
                        breakDownLog.setId(null);
                        breakDownLog.setGenerationDataTime(strToDate(breakDownLog.getTime()));
                        breakDownLog.setName(equipment.getName());
                        breakDownLog.setRemoveData(strToDate(breakDownLog.getRemoveTime()));
                        breakDownLog.setDeviceName(equipment.getDeviceName());
                        Date date = DateUtil.addDateDays(new Date(), -1);
                        //????????????????????? ??????????????? ??????????????????????????? ????????? ??????

                        Update update = new Update();
                        update.set("status", breakDownLog.getStatus()).set("removeReason", breakDownLog.getRemoveReason()).set("removeData", breakDownLog.getRemoveData());
                        Query breakDownLogQuery = new Query();
                        breakDownLogQuery.addCriteria(Criteria.where("deviceName").is(breakDownLog.getDeviceName()))
                                .addCriteria(Criteria.where("message").is(breakDownLog.getMessage()))
                                .addCriteria(Criteria.where("status").is(true))
                                .addCriteria(Criteria.where("generationDataTime").gte(date));
//                        breakDownLogDao.update()
//                                .set("status", breakDownLog.getStatus())
//                                .set("removeReason", breakDownLog.getRemoveReason())
//                                .set("removeData", breakDownLog.getRemoveData())
//                                .execute(breakDownLogDao.query().is("deviceName", breakDownLog.getDeviceName())
//                                        .is("message", breakDownLog.getMessage()).is("status", true)
//                                        .greaterThanEquals("generationDataTime", date));
                        breakDownLogDao.updateByQuery(breakDownLogQuery, update);
                        breakDownLogList.add(breakDownLog);
                    }
                    if (!CollectionUtils.isEmpty(breakDownLogList) && !breakDownLogList.isEmpty()) {

                        breakDownLogList = breakDownLogList.stream().sorted(Comparator.comparing(BreakDownLog::getGenerationDataTime)).collect(Collectors.toList());
                        breakDownLogDao.saveBatchBreakDown(breakDownLogList);
//                        breakDownLogDao.insert(breakDownLogList);
                    }
                }
            } else {
                log.info("?????????????????????????????? Data:{} deviceName:{}", new Date(), deviceName);
            }
        } catch (Exception e) {
            log.error("????????????????????????", e);
        }
    }

    private List<BreakDownLog> getBreakDownLogList(String messagePayloadMsg, String deviceName) {
        if (!StringUtils.isEmpty(messagePayloadMsg)) {
            JSONObject jsonObject = JSON.parseObject(messagePayloadMsg);
            if (!CollectionUtils.isEmpty(jsonObject) && !jsonObject.isEmpty()) {
                JSONArray dataArray = (JSONArray) jsonObject.get(Const.DATA);
                if (!CollectionUtils.isEmpty(dataArray) && !dataArray.isEmpty()) {
                    if (propertiesConfig.isRedisCacheSwitch()) {
                        cacheBreakdownLog(dataArray, deviceName);
                    }
                    return JSON.parseArray(dataArray.toJSONString(), BreakDownLog.class);
                }
            }
        }
        return null;
    }

    private void cacheBreakdownLog(JSONArray dataArray, String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            jedis.set("BREAKDOWNLOG-" + deviceName, dataArray.toString());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void handleDeviceInfo(JMessage jMessage) {

        try {
            String deviceName = jMessage.getDeviceName();
            log.info("???????????????????????? deviceName:{}", deviceName);
            JSONObject jsonObject = JSON.parseObject(jMessage.getPayloadMsg());
            JSONObject dataJson = (JSONObject) jsonObject.get(Const.DATA);

            if (!dataJson.isEmpty() && !CollectionUtils.isEmpty(dataJson)) {
                if (propertiesConfig.isRedisCacheSwitch()) {
                    cacheDeviceInfo(dataJson, deviceName);
                }
                JSONArray stationsArray = (JSONArray) dataJson.get(DEVICE_STATION);
                List<DeviceStationsInfo> deviceStationsInfos = null;
                if (!CollectionUtils.isEmpty(stationsArray) && !stationsArray.isEmpty()) {
                    deviceStationsInfos = JSON.parseArray(stationsArray.toJSONString(), DeviceStationsInfo.class);
                }
                JSONArray pccsArray = (JSONArray) dataJson.get(DEVICE_PCCS);
                List<PccsInfo> pccsInfos = null;
                if (!CollectionUtils.isEmpty(pccsArray) && !pccsArray.isEmpty()) {
                    pccsInfos = JSON.parseArray(pccsArray.toJSONString(), PccsInfo.class);
                }
                JSONArray cubesArray = (JSONArray) dataJson.get(DEVICE_CUBES);
                List<CubesInfo> cubesInfos = null;
                if (!CollectionUtils.isEmpty(cubesArray) && !cubesArray.isEmpty()) {
                    cubesInfos = JSON.parseArray(cubesArray.toJSONString(), CubesInfo.class);
                }
                JSONArray cabinsArray = (JSONArray) dataJson.get(DEVICE_CABINS);
                List<CabinsInfo> cabinsInfos = null;
                if (!CollectionUtils.isEmpty(cabinsArray) && !cabinsArray.isEmpty()) {
                    cabinsInfos = JSON.parseArray(cabinsArray.toJSONString(), CabinsInfo.class);
                }
                JSONArray equipmentsArray = (JSONArray) dataJson.get(DEVICE_EQUIPMENTS);
                List<Equipment> equipmentList = null;
                if (!CollectionUtils.isEmpty(equipmentsArray) && !equipmentsArray.isEmpty()) {
                    List<EquipmentInfo> equipmentInfos = JSON.parseArray(equipmentsArray.toJSONString(), EquipmentInfo.class);
                    if (!CollectionUtils.isEmpty(equipmentInfos) && !equipmentInfos.isEmpty()) {
                        equipmentList = equipmentInfos.stream().map(equipmentInfo -> {
                            Equipment equipment = new Equipment();
                            BeanUtils.copyProperties(equipmentInfo, equipment, Const.IGNORE_ID);
                            equipment.setEquipmentId(equipmentInfo.getId());
                            equipment.setEquipmentTypeInfo(getEquipmentTypeInfo(equipmentInfo.getEquipmentType()));
                            equipment.setDeviceName(deviceName);
                            return equipment;
                        }).collect(Collectors.toList());
                    } else {
                        return;
                    }
                    updateDeviceList(equipmentList, deviceName);
                }
                /**
                 * ??????????????????????????????  ????????????????????????
                 */
                Query query = new Query();
                query.addCriteria(Criteria.where("deviceName").is(deviceName));
                List<Equipment> equipmentDbList = equipmentDao.queryEquipment(query);
//                List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();
                /**
                 * Pccs???????????? pcc??????????????????
                 */
                if (!CollectionUtils.isEmpty(deviceStationsInfos) && !deviceStationsInfos.isEmpty() && !CollectionUtils.isEmpty(pccsInfos)
                        && !pccsInfos.isEmpty()) {
                    pccsMsgHanlder(deviceName, equipmentDbList, deviceStationsInfos, pccsInfos);
                }
                /**
                 * ?????????????????????
                 */
                if (!CollectionUtils.isEmpty(cubesInfos) && !cubesInfos.isEmpty()) {
                    cubeMsgHanlder(deviceName, equipmentDbList, cubesInfos);
                }
                /**
                 * ?????????????????????
                 */
                if (!CollectionUtils.isEmpty(cabinsInfos) && !cabinsInfos.isEmpty()) {
                    cabinMsgHanlder(deviceName, equipmentDbList, cabinsInfos);
                }
            }
        } catch (Exception e) {
            log.error("????????????????????????????????????", e);
        }
    }

    // [Description("????????????")]    PowerQuality = 0x60,
//        [Description("????????????")]    FireFighting = 0x70,
//        [Description("????????????")]    Distribution = 0x80,
//        [Description("??????")]    AirConditionMaster = 0x90,
//        [Description("??????????????????")] GreeAirConditionMaster = 0x91,
//        [Description("??????????????????")] GreeAirConditionSlave = 0x92,
//        [Description("????????????")] EnvironmentAcq = 0xA0,
//        [Description("???????????????")] FrontEquipment = 0xB0,
//        [Description("UPS??????")] Ups = 0xC0,
//        [Description("?????????????????????")] DataTransServer = 0xD0,
//        [Description("?????????")] Camera = 0xE0
    private String getEquipmentTypeInfo(int equipmentTypeInfo) {
        switch (equipmentTypeInfo) {
            case (byte) 0x10:
                return "?????????";
            case (byte) 0x20:
                return "?????????";
            case (byte) 0x30:
                return "PCS??????";
            case (byte) 0x40:
                return "PCS??????";
            case (byte) 0x50:
                return "??????";
            case (byte) 0x60:
                return "????????????";
            case (byte) 0x70:
                return "????????????";
            case (byte) 0x80:
                return "????????????";
            case (byte) 0x90:
                return "??????";
            case (byte) 0x91:
                return "??????????????????";
            case (byte) 0x92:
                return "??????????????????";
            case (byte) 0xA0:
                return "????????????";
            case (byte) 0xB0:
                return "???????????????";
            case (byte) 0xC0:
                return "UPS??????";
            case (byte) 0xD0:
                return "?????????????????????";
            case (byte) 0xE0:
                return "?????????";
            default:
                return "?????????";

        }
    }

    private void cacheDeviceInfo(JSONObject dataJson, String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            jedis.set("DEVICEINFO-" + deviceName, dataJson.toString());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private void cabinMsgHanlder(String
                                         deviceName, List<Equipment> equipmentDbList, List<CabinsInfo> cabinsInfos) {
        Map<String, List<String>> cabinEquipmentIds = equipmentDbList.stream().filter(equipment ->
                equipment.getCabinId() != null && equipment.getCubeId() != null
                        && equipment.getPccId() == null && equipment.getStationId() == null).collect(Collectors.groupingBy(equipment ->
                equipment.getCubeId() + "---" + equipment.getCabinId(), Collectors.mapping(Equipment::getId, Collectors.toList())));

        /**
         * ??????????????????
         */
        Map<Integer, List<String>> cameraEquipmentIds = equipmentDbList.stream().filter(equipment ->
                equipment.getCabinId() != null && equipment.getCubeId() == null
                        && equipment.getPccId() == null && equipment.getStationId() == null).collect(Collectors.groupingBy(Equipment::getCabinId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Cabin> cabinList = cabinsInfos.stream().map(cabinsInfo -> {
            Cabin cabin = new Cabin();
            BeanUtils.copyProperties(cabinsInfo, cabin, Const.IGNORE_ID);
            Query query = new Query();
            query.addCriteria(Criteria.where("deviceName").is(deviceName)).addCriteria(Criteria.where("cubeId").is(cabin.getCubeId()));
            Cube cube = cubeDao.findCube(query);
//            Cube cube = cubeDao.query().is("deviceName", deviceName).is("cubeId", cabin.getCubeId()).result();
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
            Query cabinQuery = new Query();
            cabinQuery.addCriteria(Criteria.where("deviceName").is(deviceName))
                    .addCriteria(Criteria.where("stationId").is(cabin.getStationId()))
                    .addCriteria(Criteria.where("pccId").is(cabin.getPccId()))
                    .addCriteria(Criteria.where("cubeId").is(cabin.getCubeId()))
                    .addCriteria(Criteria.where("cabinId").is(cabin.getCabinId()));
//            BuguQuery<Cabin> query = cabinDao.query().is("deviceName", deviceName).is("stationId", cabin.getStationId())
//                    .is("pccId", cabin.getPccId()).is("cubeId", cabin.getCubeId())
//                    .is("cabinId", cabin.getCabinId());

            if (cabinDao.exists(cabinQuery)) {
                Update update = new Update();
                update.set("equipmentIds", cabin.getEquipmentIds())
                        .set("cabinType", cabin.getCabinType())
                        .set("name", cabin.getName());
                cabinDao.updateByQuery(cabinQuery, update);
//                cabinDao.update().set("equipmentIds", cabin.getEquipmentIds())
//                        .set("cabinType", cabin.getCabinType())
//                        .set("name", cabin.getName())
//                        .execute(query);
            } else {
                cabinDao.saveBatchCabin(cabinList);
//                cabinDao.insert(cabinList);
            }
        }
    }

    private void cubeMsgHanlder(String
                                        deviceName, List<Equipment> equipmentDbList, List<CubesInfo> cubesInfos) {
        Map<Integer, List<String>> cubeEquipmentIds = equipmentDbList.stream().filter(equipment ->
                equipment.getCabinId() == null && equipment.getCubeId() != null
                        && equipment.getPccId() == null && equipment.getStationId() == null).collect(Collectors.groupingBy(Equipment::getCubeId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Cube> cubeList = cubesInfos.stream().map(cubesInfo -> {
            Cube cube = new Cube();
            BeanUtils.copyProperties(cubesInfo, cube, Const.IGNORE_ID);
            Query query = new Query();
            query.addCriteria(Criteria.where("pccsId").is(cubesInfo.getPccId())).addCriteria(Criteria.where("deviceName").is(deviceName));
            Pccs pccs = pccsDao.findPccs(query);
//            Pccs pccs = pccsDao.query().is("pccsId", cubesInfo.getPccId()).is("deviceName", deviceName).result();
            cube.setCubeId(cubesInfo.getId());
            cube.setDeviceName(deviceName);
            cube.setEquipmentIds(cubeEquipmentIds.get(cube.getCubeId()));
            if (pccs != null) {
                cube.setStationId(pccs.getStationId());
            }
            return cube;
        }).collect(Collectors.toList());
        for (Cube cube : cubeList) {
            Query query = new Query();
            query.addCriteria(Criteria.where("deviceName").is(deviceName))
                    .addCriteria(Criteria.where("stationId").is(cube.getStationId()))
                    .addCriteria(Criteria.where("pccId").is(cube.getPccId()))
                    .addCriteria(Criteria.where("cubeId").is(cube.getCubeId()));
//            BuguQuery<Cube> query = cubeDao.query().is("deviceName", deviceName).is("stationId", cube.getStationId())
//                    .is("pccId", cube.getPccId()).is("cubeId", cube.getCubeId());
            if (cubeDao.exists(query)) {
                Update update = new Update();
                update.set("equipmentIds", cube.getEquipmentIds()).set("batteryType", cube.getBatteryType()).set("batteryLevel", cube.getBatteryLevel())
                        .set("batteryChargeDischargeRate", cube.getBatteryChargeDischargeRate()).set("cubeModel", cube.getCubeModel()).set("cubeCapacity", cube.getCubeCapacity())
                        .set("cubePower", cube.getCubePower()).set("batteryManufacturer", cube.getBatteryManufacturer()).set("cellCapacity", cube.getCellCapacity())
                        .set("coolingMode", cube.getCoolingMode()).set("name", cube.getName()).set("description", cube.getDescription());
                cubeDao.updateByQuery(query, update);
//                cubeDao.update().set("equipmentIds", cube.getEquipmentIds())
//                        .set("batteryType", cube.getBatteryType())
//                        .set("batteryLevel", cube.getBatteryLevel())
//                        .set("batteryChargeDischargeRate", cube.getBatteryChargeDischargeRate())
//                        .set("cubeModel", cube.getCubeModel())
//                        .set("cubeCapacity", cube.getCubeCapacity())
//                        .set("cubePower", cube.getCubePower())
//                        .set("batteryManufacturer", cube.getBatteryManufacturer())
//                        .set("cellCapacity", cube.getCellCapacity())
//                        .set("coolingMode", cube.getCoolingMode())
//                        .set("name", cube.getName())
//                        .set("description", cube.getDescription())
//                        .execute(query);
            } else {
                cubeDao.saveCube(cube);
//                cubeDao.insert(cube);
            }
        }
    }

    private void pccsMsgHanlder(String deviceName, List<Equipment> equipmentDbList, List<DeviceStationsInfo> deviceStationsInfos, List<PccsInfo> pccsInfos) {
        /**
         * ??????????????????
         */
        DeviceStationsInfo deviceStationsInfo = deviceStationsInfos.get(0);
        Device device = new Device();
        BeanUtils.copyProperties(deviceStationsInfo, device, Const.IGNORE_ID);
        device.setStationId(deviceStationsInfo.getId());
        device.setDeviceName(deviceName);
        Map<Integer, List<String>> stationEquipmentIds = equipmentDbList.stream().filter(equipment ->
                equipment.getCabinId() == null && equipment.getCubeId() == null
                        && equipment.getPccId() == null && equipment.getStationId() != null).collect(Collectors.groupingBy(Equipment::getStationId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        device.setEquipmentIds(stationEquipmentIds.get(device.getStationId()));
        Map<Integer, List<String>> pccEquipmentIds = equipmentDbList.stream().filter(equipment -> equipment.getCabinId() == null && equipment.getCubeId() == null
                && equipment.getPccId() != null && equipment.getStationId() == null).collect(Collectors.groupingBy(Equipment::getPccId, Collectors.mapping(Equipment::getId, Collectors.toList())));
        List<Pccs> pccsList = pccsInfos.stream().map(pccsInfo -> {
            Pccs pccs = new Pccs();
            BeanUtils.copyProperties(pccsInfo, pccs, Const.IGNORE_ID);
            pccs.setPccsId(pccsInfo.getId());
            pccs.setDeviceName(deviceName);
            pccs.setEquipmentIds(pccEquipmentIds.get(pccsInfo.getId()));
            return pccs;
        }).collect(Collectors.toList());
        for (Pccs pccs : pccsList) {
            Query query = new Query();
            query.addCriteria(Criteria.where("deviceName").is(deviceName)).addCriteria(Criteria.where("stationId").is(pccs.getStationId()))
                    .addCriteria(Criteria.where("pccsId").is(pccs.getPccsId()));
//            Pccs pccs1 = pccsDao.query().is("deviceName", deviceName)
//                    .is("stationId", pccs.getStationId())
//                    .is("pccsId", pccs.getPccsId()).result();
            Pccs pccs1 = pccsDao.findPccs(query);
            if (pccs1 != null) {
                Update update = new Update();
                update.set("equipmentIds", pccs.getEquipmentIds()).set("name", pccs.getName())
                        .set("description", pccs.getDescription());
                pccsDao.updateByQuery(query, update);
//                pccsDao.update().set("equipmentIds", pccs.getEquipmentIds())
//                        .set("name", pccs.getName())
//                        .set("description", pccs.getDescription())
//                        .execute(pccs1);
            } else {
                pccsDao.savePccs(pccs);
//                pccsDao.insert(pccs);
            }
        }
        List<String> pccIds = pccsList.stream().map(pccs -> pccs.getId()).collect(Collectors.toList());
        device.setPccsIds(pccIds);
        device.setAccessTime(new Date());
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(deviceName))
                .addCriteria(Criteria.where("stationId").is(device.getStationId()));
//        deviceDao.query().is("deviceName", deviceName)
//                .is("stationId", device.getStationId()).exists()
        if (deviceDao.exists(query)) {
            Update update = new Update();
            update.set("equipmentIds", device.getEquipmentIds()).set("name", device.getName()).set("description", device.getDescription())
                    .set("stationCapacity", device.getStationCapacity()).set("stationPower", device.getStationPower()).set("province", device.getProvince())
                    .set("city", device.getCity()).set("longitude", device.getLongitude()).set("latitude", device.getLatitude()).set("applicationScenarios", device.getApplicationScenarios())
                    .set("applicationScenariosItem", device.getApplicationScenariosItem());
            deviceDao.updateFirst(query, update);
//            deviceDao.update().set("equipmentIds", device.getEquipmentIds())
//                    .set("name", device.getName())
//                    .set("description", device.getDescription())
//                    .set("stationCapacity", device.getStationCapacity())
//                    .set("stationPower", device.getStationPower())
//                    .set("province", device.getProvince())
//                    .set("city", device.getCity())
//                    .set("longitude", device.getLongitude())
//                    .set("latitude", device.getLatitude())
//                    .set("applicationScenarios", device.getApplicationScenarios())
//                    .set("applicationScenariosItem", device.getApplicationScenariosItem())
//                    .execute(deviceDao.query().is("deviceName", deviceName)
//                            .is("stationId", device.getStationId()));
        } else {
            deviceDao.saveDevice(device);
//            deviceDao.insert(device);
        }
    }

    private void updateDeviceList(List<Equipment> equipmentList, String deviceName) {
        if (CollectionUtils.isEmpty(equipmentList)) {
            return;
        }
        /**
         * ??????????????????????????????
         */
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceName").is(deviceName));
        List<Equipment> equipmentDbList = equipmentDao.queryEquipment(query);
//        List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();

        /**
         * ??????????????????ID????????????  ????????????????????????ID
         */
        List<Integer> oldIds = equipmentDbList.stream().map(Equipment::getEquipmentId).collect(Collectors.toList());

        List<Integer> newIds = equipmentList.stream().map(Equipment::getEquipmentId).collect(Collectors.toList());

        if (oldIds.containsAll(newIds)) {
            log.info("??????????????????????????? deviceName:{}", deviceName);
        } else {

            if (equipmentDbList == null && equipmentDbList.size() == 0) {
                /**
                 * ????????????
                 */
                equipmentDao.saveBatchEquipment(equipmentList);
//                equipmentDao.insert(equipmentList);
            } else {
                /**
                 * ??????
                 */
                List<Integer> collectDelete = oldIds.stream().filter(it -> !newIds.contains(it)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collectDelete) && !collectDelete.isEmpty()) {
                    for (Integer deleteId : collectDelete) {
                        Query removeQuery = new Query();
                        removeQuery.addCriteria(Criteria.where("deviceName").is(deviceName))
                                .addCriteria(Criteria.where("equipmentId").is(deleteId));
                        equipmentDao.removeByQuery(query);
//                        equipmentDao.remove(equipmentDao.query().is("deviceName", deviceName).is("equipmentId", deleteId));
                    }
                }
                /**
                 * ??????
                 */
                List<Integer> collectAdd = newIds.stream().filter(it -> !oldIds.contains(it)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collectAdd) && !collectAdd.isEmpty()) {
                    List<Equipment> addEquipment = new ArrayList<>();
                    for (Equipment equipment : equipmentList) {
                        if (collectAdd.contains(equipment.getEquipmentId())) {
                            addEquipment.add(equipment);
                        }
                    }
                    equipmentDao.saveBatchEquipment(addEquipment);
//                    equipmentDao.insert(addEquipment);
                }
            }
            log.info("???????????????????????????????????? deviceName:{}", deviceName);
        }
        /**
         * ????????????????????????????????????
         */
        List<Equipment> equipments = equipmentList.stream().filter(equipment -> oldIds.contains(equipment.getEquipmentId())).collect(Collectors.toList());
        equipments.stream().forEach(oldEquipment -> {
                    Update update = new Update();
                    update.set("enabled", oldEquipment.isEnabled()).set("model", oldEquipment.getModel()).set("manufacturer", oldEquipment.getManufacturer())
                            .set("stationId", oldEquipment.getStationId()).set("description", oldEquipment.getDescription()).set("name", oldEquipment.getName())
                            .set("pccid", oldEquipment.getPccId()).set("cubeId", oldEquipment.getCubeId()).set("cabinId", oldEquipment.getCabinId());
                    Query updateQuery = new Query();
                    updateQuery.addCriteria(Criteria.where("deviceName").is(deviceName))
                            .addCriteria(Criteria.where("equipmentId").is(oldEquipment.getEquipmentId()));
                    equipmentDao.updateByQuery(updateQuery,update);
                });
//                equipmentDao.update().set("enabled", oldEquipment.isEnabled())
//                        .set("model", oldEquipment.getModel())
//                        .set("manufacturer", oldEquipment.getManufacturer())
//                        .set("stationId", oldEquipment.getStationId())
//                        .set("description", oldEquipment.getDescription())
//                        .set("name", oldEquipment.getName())
//                        .set("pccid", oldEquipment.getPccId())
//                        .set("cubeId", oldEquipment.getCubeId())
//                        .set("cabinId", oldEquipment.getCabinId())
//                        .execute(equipmentDao.query().is("deviceName", deviceName)
//                                .is("equipmentId", oldEquipment.getEquipmentId())));
        log.info("??????????????????????????????deviceName:{}", deviceName);
    }

    /**
     * ????????????????????????
     * ???????????? ??????????????????????????? ?????????????????????
     */
    @Scheduled(cron = "${untrans.report.interval}")
    public void editBreakDownlogStatus() {
        int alterBreakdownlogStatusStart = propertiesConfig.getAlterBreakdownlogStatusStart();
        int alterBreakdownlogStatusEnd = propertiesConfig.getAlterBreakdownlogStatusEnd();
        Date startTime = DateUtil.addDateDays(new Date(), -alterBreakdownlogStatusStart);
        Date endTime = DateUtil.addDateDays(new Date(), -alterBreakdownlogStatusEnd);
        Update update = new Update();
        update.set("status", false).set("removeReason", "????????????").set("removeData", new Date());
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(true))
                .addCriteria(Criteria.where("generationDataTime").gte(startTime))
                .addCriteria(Criteria.where("generationDataTime").lte(endTime));
        breakDownLogDao.updateByQuery(query,update);
//        breakDownLogDao.update().set("status", false)
//                .set("removeReason", "????????????")
//                .set("removeData", new Date()).execute(breakDownLogDao.query().is("status", true)
//                .greaterThanEquals("generationDataTime", startTime)
//                .lessThanEquals("generationDataTime", endTime));
        log.info("????????????????????????????????????????????????:{}", new Date());
    }
}
