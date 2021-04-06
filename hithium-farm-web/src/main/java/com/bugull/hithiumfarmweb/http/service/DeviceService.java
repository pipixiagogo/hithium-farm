package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.CityVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.ProvinceVo;
import com.bugull.hithiumfarmweb.http.vo.dao.DeviceAreaEntityDao;
import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguAggregation;
import com.mongodb.DBObject;
import org.springframework.beans.BeanUtils;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PccsDao pccsDao;
    @Resource
    private CubeDao cubeDao;
    @Resource
    private CabinDao cabinDao;
    @Resource
    private DeviceAreaEntityDao deviceAreaEntityDao;
    @Resource
    private EquipmentDao equipmentDao;

    @Resource
    private MessageHandler mqttOutbound;
    public static final String SQL_PREFIX = "$ESS/";
    public static final String S2D_SUFFIX = "/MSG/D2S";



//    @Scheduled(cron = "${energy.untrans.report.interval}")
    public void sendMqttMsg(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", "REAL_TIME_DATA");
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        jsonObject.put("DATA", "456");
        Message<String> message = MessageBuilder.withPayload(jsonObject.toString()).setHeader(MqttHeaders.TOPIC, getEssSendTopic("456"))
                .setHeader(MqttHeaders.QOS, 0)
                .build();
        mqttOutbound.handleMessage(message);
    }
    public static final String getEssSendTopic(String deviceName) {
        return SQL_PREFIX + deviceName + S2D_SUFFIX;
    }


    public ResHelper<List<ProvinceVo>> aggrationArea() {
        BuguAggregation<Device> aggregate = deviceDao.aggregate();
        aggregate.group("{_id:'$province', count:{$sum:1}}");
        aggregate.sort("{count:-1}");
        Iterable<DBObject> results = aggregate.results();
        List<ProvinceVo> provinceVos = new ArrayList<>();
        results.forEach(res -> {
            ProvinceVo provinceVo = new ProvinceVo();
            String province = (String) res.get("_id");
            Integer count = (Integer) res.get("count");
            DBObject condition = deviceDao.query().is("province", province).getCondition();
            Iterable<DBObject> iterable = deviceDao.aggregate().match(condition).group("{_id:'$city', count:{$sum:1}}").sort("{count:-1}").results();
            List<CityVo> cityVoList = new ArrayList<>();
            iterable.forEach(it -> {
                CityVo cityVo = new CityVo();
                String city = (String) it.get("_id");
                Integer num = (Integer) it.get("count");
                cityVo.setCity(city);
                cityVo.setNum(num);
                List<DeviceAreaEntity> deviceAreaEntities = deviceAreaEntityDao.query().is("province", province).is("city", city).results();
                cityVo.setDeviceAreaEntities(deviceAreaEntities);
                cityVoList.add(cityVo);
            });
            provinceVo.setProvince(province);
            provinceVo.setNum(count);
            provinceVo.setCityVoList(cityVoList);
            provinceVos.add(provinceVo);
        });
        return ResHelper.success("", provinceVos);
    }

    public ResHelper<List<Device>> queryDetailAreaByCity(String city) {
        List<Device> deviceList = deviceDao.query().is("city", city).returnFields("longitude", "latitude").results();
        return ResHelper.success("", deviceList);
    }

    public ResHelper<DeviceVo> query(String deviceName) {
        Device device = deviceDao.query().is("deviceName", deviceName).result();
        DeviceVo deviceVo = new DeviceVo();
        if(device != null){
            deviceVo.setDeviceName(device.getDeviceName());
            deviceVo.setName(device.getName());
            deviceVo.setDescription(device.getDescription());
            List<Equipment> equipmentList = equipmentDao.query().in("_id", device.getEquipmentIds()).results();
            List<Map<String, Map<String, Map<String, Map<String, List<String>>>>>> list = new ArrayList<>();
            Map<String, Map<String, Map<String, Map<String, List<String>>>>> map = new HashMap<>();
            equipmentList.stream().forEach(equipment -> {
                map.put(equipment.getName(), new HashMap<>());
            });
            List<Pccs> pccsList = pccsDao.query().is("deviceName", deviceName).is("stationId", device.getStationId()).results();
            pccsList.stream().forEach(pccs -> {
                Map<String, Map<String, Map<String, List<String>>>> cubeNameList = new HashMap<>();
                /**
                 * 集装箱
                 */
                List<Cube> cubes = cubeDao.query().is("deviceName", deviceName).is("stationId", device.getStationId())
                        .is("pccId", pccs.getPccsId()).results();
                cubes.stream().forEach(cube -> {
                    Map<String, Map<String, List<String>>> cubeNames = new HashMap<>();
                    /**
                     * 查询 舱
                     */
                    List<Cabin> cabinList = cabinDao.query().is("deviceName", deviceName)
                            .is("stationId", device.getStationId())
                            .is("pccId", pccs.getPccsId()).is("cubeId", cube.getCubeId())
                            .results();
                    cabinList.stream().forEach(cabin -> {
                        Map<String, List<String>> cabinNames = new HashMap<>();
                        List<Equipment> equipment = equipmentDao.query().in("_id", cabin.getEquipmentIds()).returnFields("name", "description").results();
                        equipment.stream().forEach(equip -> {
                            cabinNames.put(equip.getName(), new ArrayList<>());
                        });
                        cubeNames.put(cabin.getName()+"-"+cabin.getCabinId(), cabinNames);
                    });
                    List<Equipment> equipment = equipmentDao.query().in("_id", cube.getEquipmentIds()).returnFields("name", "description").results();
                    equipment.stream().forEach(equip -> {
                        cubeNames.put(equip.getName(), new HashMap<>());
                    });
                    cubeNameList.put(cube.getName() + "-" + cube.getCubeId(), cubeNames);
                });
                /**
                 * 查询并网口下的设备
                 */
                List<Equipment> equipment = equipmentDao.query().in("_id", pccs.getEquipmentIds()).returnFields("name", "description").results();
                equipment.stream().forEach(equip -> {
                    cubeNameList.put(equip.getName(), new HashMap<>());
                });
                /**
                 * 并网口名称 加 并网口ID
                 */
                map.put(pccs.getName()+"-"+pccs.getPccsId(), cubeNameList);
            });
            list.add(map);
            deviceVo.setDeviceMap(list);
        }

        return ResHelper.success("", deviceVo);
    }
}
