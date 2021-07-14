package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.dao.EssStationDao;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import com.bugull.mongo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bugull.hithiumfarmweb.common.Const.DEVICE_NAME;

@Service
public class EssStationService {

    @Resource
    private EssStationDao essStationDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PropertiesConfig propertiesConfig;

    public List<EssStation> getEssStationList(List<String> essStationIds) {
        List<EssStation> essStations = essStationDao.query().in("_id", essStationIds).results();
        if (!CollectionUtils.isEmpty(essStations) && !essStations.isEmpty()) {
            return essStations;
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
    }

    public ResHelper<Void> updateStation(EssStation essStation) {
        try {
            EssStation essStationOld = essStationDao.query().is("_id", essStation).result();

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
            for (String deviceName : essStation.getDeviceNameList()) {
                if (!deviceDao.query().is(DEVICE_NAME, deviceName).is("bindStation", false).exists()) {
                    return ResHelper.pamIll();
                }
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
            deviceDao.update().set("bindStation", false).execute(deviceDao.query().in(DEVICE_NAME, essStationOld.getDeviceNameList()));
            deviceDao.update().set("bindStation", true).execute(deviceDao.query().in(DEVICE_NAME, essStation.getDeviceNameList()));
            essStationBuguUpdater.execute(essStation);
            return ResHelper.success("修改电站信息成功");
        } catch (Exception e) {
            return ResHelper.error("修改电站信息失败");
        }
    }

    public ResHelper<BuguPageQuery.Page<EssStation>> selectStation(Map<String, Object> params) {
        BuguPageQuery<EssStation> query = essStationDao.pageQuery();
        String stationName = (String) params.get("stationName");
        if (!StringUtils.isEmpty(stationName)) {
            query.regexCaseInsensitive("stationName", stationName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<EssStation> essStationPage = query.resultsWithPage();
        return ResHelper.success("", essStationPage);
    }

    public ResHelper<Void> deleteStation(List<String> stationIds) {
        if (!CollectionUtils.isEmpty(stationIds) && !stationIds.isEmpty()) {
            essStationDao.remove(stationIds);
        }
        return ResHelper.success("删除成功");
    }
}
