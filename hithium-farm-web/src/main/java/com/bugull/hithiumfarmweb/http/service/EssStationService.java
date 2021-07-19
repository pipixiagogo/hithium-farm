package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.BamsDataDicBADao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.dao.EssStationDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.StringUtil;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.DEVICE_NAME;

@Service
public class EssStationService {

    private static final Logger log = LoggerFactory.getLogger(EssStationService.class);
    @Resource
    private EssStationDao essStationDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private DeviceService deviceService;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private SysUserDao sysUserDao;

    public List<EssStation> getEssStationList(List<String> essStationIds) {
        if (!CollectionUtils.isEmpty(essStationIds) && !essStationIds.isEmpty()) {
            List<EssStation> essStations = essStationDao.query().in("_id", essStationIds).results();
            if (!CollectionUtils.isEmpty(essStations) && !essStations.isEmpty()) {
                return essStations;
            }
        }
        return new ArrayList<>();
    }

    public List<String> getDeviceNames(List<String> essStationIds) {
        List<EssStation> essStationList = getEssStationList(essStationIds);
        if (!CollectionUtils.isEmpty(essStationList) && !essStationIds.isEmpty()) {
            List<String> deviceNames = new ArrayList<>();
            for (EssStation essStation : essStationList) {
                if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                    deviceNames.addAll(essStation.getDeviceNameList());
                }
            }
            return deviceNames;
        }
        return new ArrayList<>();
    }

    public ResHelper<Void> save(EssStation essStation) {
        try {
            if (StringUtils.isEmpty(essStation.getStationName())) {
                return ResHelper.pamIll();
            }
            if (CollectionUtils.isEmpty(essStation.getDeviceNameList()) && essStation.getDeviceNameList().isEmpty()) {
                return ResHelper.pamIll();
            }
            for (String deviceName : essStation.getDeviceNameList()) {
                if (!deviceDao.query().is(DEVICE_NAME, deviceName).is("bindStation", false).exists()) {
                    return ResHelper.pamIll();
                }
            }
            Device device = deviceDao.query().is(DEVICE_NAME, essStation.getDeviceNameList().get(0)).result();
            essStation.setProvince(device.getProvince());
            essStation.setCity(device.getCity());
            deviceDao.update().set("bindStation", true).execute(deviceDao.query().in(DEVICE_NAME, essStation.getDeviceNameList()));
            essStationDao.insert(essStation);
            return ResHelper.success("添加电站成功");
        }catch (Exception e){
            log.error("添加电站失败:{}",e);
            return ResHelper.error("添加电站失败");
        }

    }

    public ResHelper<Void> updateStation(EssStation essStation) {
        try {
            EssStation essStationOld = essStationDao.query().is("_id", essStation.getId()).result();
            if (essStationOld == null) {
                return ResHelper.pamIll();
            }
            BuguUpdater<EssStation> essStationBuguUpdater = essStationDao.update();
            if (!StringUtils.isEmpty(essStation.getStationName())) {
                essStationBuguUpdater.set("stationName", essStation.getStationName());
            }
            if (!StringUtils.isEmpty(essStation.getCode())) {
                essStationBuguUpdater.set("code", essStation.getCode());
            }
            if (CollectionUtils.isEmpty(essStation.getDeviceNameList()) && essStation.getDeviceNameList().isEmpty()) {
                return ResHelper.pamIll();
            }
            essStationBuguUpdater.set("deviceNameList", essStation.getDeviceNameList());
            if (!StringUtil.isEmpty(essStation.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(essStation.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!org.springframework.util.StringUtils.isEmpty(essStation.getCity()) && !propertiesConfig.getProToCitys().get(essStation.getProvince()).contains(essStation.getCity())) {
                    return ResHelper.pamIll();
                }
            }
            if (!org.springframework.util.StringUtils.isEmpty(essStation.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(essStation.getCity())
                        || !propertiesConfig.getCitys().get(essStation.getCity()).equals(essStation.getProvince())) {
                    return ResHelper.pamIll();
                }
            }
            if (!StringUtils.isEmpty(essStation.getProvince())) {
                essStationBuguUpdater.set("province", essStation.getProvince());
            }
            if (!StringUtils.isEmpty(essStation.getCity())) {
                essStationBuguUpdater.set("city", essStation.getCity());
            }
            /**
             * 解除绑定
             */
            if (!CollectionUtils.isEmpty(essStationOld.getDeviceNameList()) && !essStationOld.getDeviceNameList().isEmpty()) {
                deviceDao.update().set("bindStation", false).execute(deviceDao.query().in(DEVICE_NAME, essStationOld.getDeviceNameList()));
            }
            deviceDao.update().set("bindStation", true).execute(deviceDao.query().in(DEVICE_NAME, essStation.getDeviceNameList()));
            essStationBuguUpdater.execute(essStation);
            return ResHelper.success("修改电站信息成功");
        } catch (Exception e) {
            log.error("修改电站信息失败", e);
            return ResHelper.error("修改电站信息失败");
        }
    }

    public ResHelper<BuguPageQuery.Page<EssStationVo>> selectStation(Map<String, Object> params, SysUser user) {
        BuguPageQuery<EssStation> query = essStationDao.pageQuery();
        if(!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()){
            query.in("_id",user.getStationList());
        }
        String stationName = (String) params.get("stationName");
        if (!StringUtils.isEmpty(stationName)) {
            query.regexCaseInsensitive("stationName", stationName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<EssStation> essStationPage = query.resultsWithPage();
        List<EssStation> essStationList = essStationPage.getDatas();
        List<EssStationVo> essStationVos = essStationList.stream().map(essStation -> {
            EssStationVo essStationVo = new EssStationVo();
            BeanUtils.copyProperties(essStation, essStationVo);
            essStationVo.setAreaCity(essStation.getProvince() + "-" + essStation.getCity());
            if(!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()){
                List<Device> deviceList = deviceDao.query().in("deviceName", essStation.getDeviceNameList()).results();
                List<DeviceVo> deviceVos = deviceList.stream().map(device -> {
                    DeviceVo deviceVo = new DeviceVo();
                    BeanUtils.copyProperties(device, deviceVo);
                    return deviceVo;
                }).collect(Collectors.toList());
                essStationVo.setDeviceVoList(deviceVos);
            }
            return essStationVo;
        }).collect(Collectors.toList());
        BuguPageQuery.Page<EssStationVo> essStationVoPage = new BuguPageQuery.Page<>(essStationPage.getPage(), essStationPage.getPageSize(), essStationPage.getTotalRecord(), essStationVos);
        return ResHelper.success("", essStationVoPage);
    }

    public ResHelper<Void> deleteStation(List<String> stationIds) {
        try {
            if (!CollectionUtils.isEmpty(stationIds) && !stationIds.isEmpty()) {
                List<EssStation> essStations = essStationDao.query().in("_id", stationIds).results();
                boolean stationList = sysUserDao.query().in("stationList", stationIds).exists();
                if (stationList) {
                    return ResHelper.error("请先解除用户与电站的绑定");
                }
                for (EssStation essStation : essStations) {
                    deviceDao.update().set("bindStation", false).execute(deviceDao.query().in(DEVICE_NAME, essStation.getDeviceNameList()));
                }
                essStationDao.remove(stationIds);
            }
            return ResHelper.success("删除成功");
        }catch (Exception e){
            log.error("删除失败:{}",e);
            return ResHelper.error("删除失败");
        }

    }

    public EssStationWithDeviceVo infoStationById(String stationId) {
        EssStation essStation = essStationDao.query().is("_id", stationId).result();
        EssStationWithDeviceVo essStationVo = new EssStationWithDeviceVo();
        if (essStation != null) {
            BeanUtils.copyProperties(essStation, essStationVo);
            List<String> deviceNameList = essStation.getDeviceNameList();
            essStationVo.setAreaCity(essStation.getProvince() + "-" + essStation.getCity());
            List<Device> deviceList = deviceDao.query().in(DEVICE_NAME, deviceNameList).results();
            List<DeviceInfoVo> deviceInfoVos = deviceList.stream().map(device -> {
                DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
                BeanUtils.copyProperties(device, deviceInfoVo);
                deviceInfoVo.setAreaCity(device.getProvince() + "-" + device.getCity());
                deviceInfoVo.setApplicationScenariosItemMsg(deviceService.getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
                deviceInfoVo.setApplicationScenariosMsg(deviceService.getApplicationScenariosMsg(device.getApplicationScenarios()));
                Iterable<DBObject> bamsDataIterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is(DEVICE_NAME, device.getDeviceName()))
                        .sort("{generationDataTime:-1}").limit(1).results();
                Date date = new Date();
                if (bamsDataIterable != null) {
                    for (DBObject dbObject : bamsDataIterable) {
                        BamsDataDicBA bamsDataDicBA = MapperUtil.fromDBObject(BamsDataDicBA.class, dbObject);
                        Date addDateMinutes = DateUtils.addDateMinutes(bamsDataDicBA.getGenerationDataTime(), 30);
                        if (!addDateMinutes.before(date)) {
                            deviceInfoVo.setDeviceStatus("正常运行");
                        } else {
                            deviceInfoVo.setDeviceStatus("停机");
                        }
                    }
                }
                return deviceInfoVo;
            }).collect(Collectors.toList());
            essStationVo.setDeviceList(deviceInfoVos);
        }
        return essStationVo;
    }

    public ResHelper<Map<String, Set<String>>> queryProvinces() {
        return ResHelper.success("",propertiesConfig.getProToCitys());
    }
}
