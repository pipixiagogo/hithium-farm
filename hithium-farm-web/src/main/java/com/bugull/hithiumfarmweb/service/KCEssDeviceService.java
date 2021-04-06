package com.bugull.hithiumfarmweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.dto.*;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.message.JMessage;
import com.bugull.mongo.BuguQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;


@Component("kCEssDeviceService")
public class KCEssDeviceService {
    private static final Logger log = LoggerFactory.getLogger(KCEssDeviceService.class);


    public static final Map<String, Object> REAL_TIME_DATA_TYPE = new HashMap<>();

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
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("设备上报实时数据:{}", deviceName);
            JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
            JSONObject datajson = (JSONObject) jsonObject.get(Const.DATA);
            /**
             * 测试完成后 放到非空判断里面
             */
            CACHEMAP.put(deviceName + "-REALDATA", datajson);
            List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).results();
            if (equipmentList != null && equipmentList.size() > 0) {
                resolvingRealDataTime(datajson, equipmentList);
            } else {
                log.info("设备上报实时数据未查到对应设备:{}", deviceName);
            }
        } catch (Exception e) {
            log.error("设备上报实时数据格式错误,丢弃:{}", jMessage.getPayloadMsg());
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
        if (ammeterDataDic != null) {
            ammeterDataDic.setDeviceName(equipment.getDeviceName());
            ammeterDataDic.setName(equipment.getName());
            ammeterDataDic.setGenerationDataTime(strToDate(ammeterDataDic.getTime()));
            ammeterDataDicDao.insert(ammeterDataDic);
        }
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
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = formatter.parse(time);
                return date;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("转换日期错误", e);
            return null;
        }
    }

    private Map<String, Short> getListOfTempAndVol(JSONObject jsonObject, String sign) {
        jsonObject.remove("Time");
        jsonObject.remove("EquipChannelStatus");
        jsonObject.remove("EquipmentId");
        String[] temps = jsonObject.toString().replace("{", "").replace("}", "").replace("\"", "").split(",");
        Map<String, Short> resulMap = new HashMap<>();
        for (String temp : temps) {
            String[] tem = temp.split(":");
            resulMap.put(tem[0], Short.parseShort(tem[1]));
        }
        if (resulMap == null && resulMap.isEmpty()) {
            return null;
        }
        Map<String, Short> shortMap = new TreeMap<>(new Comparator<String>() {
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
            log.info("设备告警日志上报 deviceName:{}", deviceName);
            String messagePayloadMsg = jMessage.getPayloadMsg();
            if (!StringUtils.isEmpty(messagePayloadMsg)) {
                JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
                JSONArray dataArray = (JSONArray) jsonObject.get(Const.DATA);
                CACHEMAP.put(deviceName + "-BREAKDOWNLOG", dataArray);
                List<BreakDownLog> breakDownLogs = JSONArray.parseArray(dataArray.toJSONString(), BreakDownLog.class);
                if (breakDownLogs != null && breakDownLogs.size() > 0) {
                    List<Equipment> equipmentDbList = equipmentDao.query().is("deviceName", deviceName).results();
                    if (!CollectionUtils.isEmpty(equipmentDbList)) {
                        List<BreakDownLog> breakDownLogList = new ArrayList<>();
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
                            breakDownLogList.add(breakDownLog);
                        }
                        breakDownLogDao.insert(breakDownLogList);
                    }
                }
            } else {
                log.info("设备告警日志上报为空 Data:{} deviceName:{}", new Date(), deviceName);
            }
        } catch (Exception e) {
            log.error("设备告警处理错误", e);
        }

    }

    private void handleDeviceInfo(JMessage jMessage) {
        try {
            String deviceName = jMessage.getDeviceName();
            log.info("设备列表信息上报 deviceName:{}", deviceName);
            JSONObject jsonObject = JSONObject.parseObject(jMessage.getPayloadMsg());
            JSONObject dataJson = (JSONObject) jsonObject.get(Const.DATA);
            CACHEMAP.put(deviceName + "-DEVICEINFO", dataJson);
            JSONArray stationsArray = (JSONArray) dataJson.get(DEVICE_STATION);
            JSONArray pccsArray = (JSONArray) dataJson.get(DEVICE_PCCS);
            JSONArray cubesArray = (JSONArray) dataJson.get(DEVICE_CUBES);
            JSONArray cabinsArray = (JSONArray) dataJson.get(DEVICE_CABINS);
            JSONArray equipmentsArray = (JSONArray) dataJson.get(DEVICE_EQUIPMENTS);
            List<DeviceStationsInfo> deviceStationsInfos;
            if (!CollectionUtils.isEmpty(stationsArray)) {
                deviceStationsInfos = JSONArray.parseArray(stationsArray.toJSONString(), DeviceStationsInfo.class);
            } else {
                deviceStationsInfos = new ArrayList<>();
            }
            List<PccsInfo> pccsInfos;
            if (!CollectionUtils.isEmpty(pccsArray)) {
                pccsInfos = JSONArray.parseArray(pccsArray.toJSONString(), PccsInfo.class);
            } else {
                pccsInfos = new ArrayList<>();
            }
            List<CubesInfo> cubesInfos;
            if (!CollectionUtils.isEmpty(cubesArray)) {
                cubesInfos = JSONArray.parseArray(cubesArray.toJSONString(), CubesInfo.class);
            } else {
                cubesInfos = new ArrayList<>();
            }
            List<CabinsInfo> cabinsInfos;
            if (!CollectionUtils.isEmpty(cabinsArray)) {
                cabinsInfos = JSONArray.parseArray(cabinsArray.toJSONString(), CabinsInfo.class);
            } else {
                cabinsInfos = new ArrayList<>();
            }
            List<EquipmentInfo> equipmentInfos;
            if (!CollectionUtils.isEmpty(equipmentsArray)) {
                equipmentInfos = JSONArray.parseArray(equipmentsArray.toJSONString(), EquipmentInfo.class);
            } else {
                equipmentInfos = new ArrayList<>();
            }
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
            if(!CollectionUtils.isEmpty(pccsInfos)){
                pccsMsgHanlder(deviceName, equipmentDbList, deviceStationsInfos, pccsInfos);
            }
            /**
             * 储能箱信息处理
             */
            if(!CollectionUtils.isEmpty(cubesInfos)){
                cubeMsgHanlder(deviceName, equipmentDbList, cubesInfos);
            }
            /**
             * 电池舱信息处理
             */
            if(!CollectionUtils.isEmpty(cabinsInfos)){
                cabinMsgHanlder(deviceName, equipmentDbList, cabinsInfos);
            }

        } catch (Exception e) {
            log.error("解析上报设备列表信息失败:{}", e);
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
}
