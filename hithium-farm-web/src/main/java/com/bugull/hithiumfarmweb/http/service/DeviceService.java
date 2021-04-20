package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.http.bo.EquipmentBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.CityVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.ProvinceVo;
import com.bugull.hithiumfarmweb.http.vo.dao.DeviceAreaEntityDao;
import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguAggregation;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private BamsDataDicBADao bamsDataDicBADao;

    public static final String SQL_PREFIX = "$ESS/";
    public static final String S2D_SUFFIX = "/MSG/D2S";

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
            if (iterable != null) {
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
            }
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

    public DeviceVo query(String deviceName) {
        Device device = deviceDao.query().is("deviceName", deviceName).result();
        DeviceVo deviceVo = new DeviceVo();
        if (device != null) {
            deviceVo.setDeviceName(device.getDeviceName());
            deviceVo.setName(device.getName());
            deviceVo.setDescription(device.getDescription());
            List<Equipment> equipmentList = equipmentDao.query().is("enabled",true).in("_id", device.getEquipmentIds())
                    .returnFields("name","equipmentId","_id").results();
            EquipmentBo equipmentBo = new EquipmentBo();
            equipmentBo.setName(device.getName());
            equipmentBo.setId(device.getId());
            List<EquipmentBo> equipmentBos = new ArrayList<>();
            equipmentBo.setEquipmentBo(equipmentBos);
            equipmentList.forEach(equipment -> {
                EquipmentBo equip = new EquipmentBo();
                equip.setName(equipment.getName());
                equip.setId(equipment.getId());
                equip.setEquipmentId(equipment.getEquipmentId());
                equip.setEquipmentBo(null);
                equipmentBos.add(equip);
            });
            List<Pccs> pccsList = pccsDao.query().is("deviceName", deviceName).is("stationId", device.getStationId()).results();
            pccsList.stream().forEach(pccs -> {
                EquipmentBo equipment = new EquipmentBo();
                equipment.setName(pccs.getName());
                equipment.setId(pccs.getId());
                List<EquipmentBo> equipmentBoList = new ArrayList<>();
                equipment.setEquipmentBo(equipmentBoList);
                List<Cube> cubes = cubeDao.query().is("deviceName", deviceName).is("stationId", device.getStationId())
                        .is("pccId", pccs.getPccsId()).results();
                cubes.stream().forEach(cube -> {
                    EquipmentBo equipm = new EquipmentBo();
                    equipm.setName(cube.getName());
                    equipm.setId(cube.getId());
                    List<Cabin> cabinList = cabinDao.query().is("deviceName", deviceName)
                            .is("stationId", device.getStationId())
                            .is("pccId", pccs.getPccsId()).is("cubeId", cube.getCubeId())
                            .results();
                    List<EquipmentBo> boList = new ArrayList<>();
                    cabinList.stream().forEach(cabin -> {
                        EquipmentBo mentBo = new EquipmentBo();
                        mentBo.setName(cabin.getName());
                        mentBo.setId(cabin.getId());
                        List<EquipmentBo> bos=new ArrayList<>();
                        mentBo.setEquipmentBo(bos);
                        List<Equipment> ment = equipmentDao.query().is("enabled",true).in("_id", cabin.getEquipmentIds())
                                .returnFields("name","equipmentId","_id").results();
                        ment.stream().forEach(men->{
                            EquipmentBo menBo = new EquipmentBo();
                            menBo.setName(men.getName());
                            menBo.setId(men.getId());
                            menBo.setEquipmentBo(null);
                            menBo.setEquipmentId(men.getEquipmentId());
                            bos.add(menBo);
                        });
                        boList.add(mentBo);
                    });
                    List<Equipment> equip = equipmentDao.query().is("enabled",true).in("_id", cube.getEquipmentIds())
                            .returnFields("name","equipmentId","_id").results();
                    equip.stream().forEach(equ -> {
                        EquipmentBo ipmentBo = new EquipmentBo();
                        ipmentBo.setName(equ.getName());
                        ipmentBo.setId(equ.getId());
                        ipmentBo.setEquipmentBo(null);
                        ipmentBo.setEquipmentId(equ.getEquipmentId());
                        equipmentBoList.add(ipmentBo);
                    });
                    equipm.setEquipmentBo(boList);
                    equipmentBoList.add(equipm);

                });
                equipmentBos.add(equipment);
                List<Equipment> equips = equipmentDao.query().is("enabled",true).in("_id", pccs.getEquipmentIds())
                        .returnFields("name","equipmentId","_id").results();
                equips.stream().forEach(ip -> {
                    EquipmentBo mentBo = new EquipmentBo();
                    mentBo.setName(ip.getName());
                    mentBo.setId(ip.getId());
                    mentBo.setEquipmentBo(null);
                    mentBo.setEquipmentId(ip.getEquipmentId());
                    equipmentBos.add(mentBo);
                });
            });
            deviceVo.setEquipmentBo(equipmentBo);
        }

        return deviceVo;
    }


    public ResHelper<BuguPageQuery.Page<DeviceInfoVo>> deviceAreaList(Map<String, Object> params) {
        BuguPageQuery<Device> deviceBuguPageQuery = deviceDao.pageQuery();
        if (!queryOfParams(deviceBuguPageQuery, params)) {
            return ResHelper.pamIll();
        }
        deviceBuguPageQuery.sortDesc("accessTime");
        BuguPageQuery.Page<Device> devicePage = deviceBuguPageQuery.resultsWithPage();
        List<Device> datas = devicePage.getDatas();
        List<DeviceInfoVo> deviceInfoVos = new ArrayList<>();
        for (Device data : datas) {
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
            BeanUtils.copyProperties(data, deviceInfoVo);
            deviceInfoVo.setAreaCity(data.getProvince() + "-" + data.getCity());
            Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is("deviceName", data.getDeviceName()))
                    .group("{'_id':'$deviceName','dataId':{$last:'$_id'}}")
                    .sort("{generationDataTime:-1}").results();
            Date date = new Date();
            if (iterable != null) {
                for (DBObject dbObject : iterable) {
                    ObjectId objectId = (ObjectId) dbObject.get("dataId");
                    BamsDataDicBA bamsDataDicBA = bamsDataDicBADao.query().is("_id", objectId.toString()).result();
                    Date addDateMinutes = DateUtils.addDateMinutes(bamsDataDicBA.getGenerationDataTime(), 30);
                    if (!addDateMinutes.before(date)) {
                        deviceInfoVo.setDeviceStatus("正常运行");
                    } else {
                        deviceInfoVo.setDeviceStatus("停机");
                    }
                }
            }
            deviceInfoVos.add(deviceInfoVo);
        }
        BuguPageQuery.Page<DeviceInfoVo> deviceInfoVoPage = new BuguPageQuery.Page<>(devicePage.getPage(), devicePage.getPageSize(),
                devicePage.getTotalRecord(), deviceInfoVos);
        return ResHelper.success("", deviceInfoVoPage);
    }

    private boolean queryOfParams(BuguPageQuery<?> query, Map<String, Object> params) {
        String name = (String) params.get("name");
        if (name != null) {
            query.regexCaseInsensitive("name", name).or(query.regexCaseInsensitive("description", name));
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return false;
        if (!PagetLimitUtil.orderField(query, params)) return false;
        return true;
    }

    public ResHelper<BuguPageQuery.Page<DeviceVo>> queryDeivcesByPage(Map<String, Object> params) {
        BuguPageQuery<Device> deviceBuguPageQuery = deviceDao.pageQuery();
        if (!queryOfParams(deviceBuguPageQuery, params)) {
            return ResHelper.pamIll();
        }
        deviceBuguPageQuery.sortDesc("accessTime");
        BuguPageQuery.Page<Device> devicePage = deviceBuguPageQuery.resultsWithPage();
        List<Device> deviceList = devicePage.getDatas();
        if (!CollectionUtils.isEmpty(deviceList) && deviceList.size() > 0) {
            List<DeviceVo> deviceVos = new ArrayList<>();
            for (Device device : deviceList) {
                DeviceVo deviceVo = query(device.getDeviceName());
                deviceVos.add(deviceVo);
            }
            BuguPageQuery.Page<DeviceVo> deviceVoPage = new BuguPageQuery.Page<>(devicePage.getPage(), devicePage.getPageSize(), devicePage.getTotalRecord(), deviceVos);
            return ResHelper.success("", deviceVoPage);
        }
        return ResHelper.success("");
    }
}
