package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.DeviceImgBo;
import com.bugull.hithiumfarmweb.http.bo.EssStationBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguQuery;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;

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
    @Resource
    private UploadEntityDao uploadEntityDao;

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

    public ResHelper<Void> save(EssStationBo essStationBo) {
        try {
            if (essStationBo == null) {
                return ResHelper.pamIll();
            }
            if (StringUtils.isEmpty(essStationBo.getStationName())) {
                return ResHelper.pamIll();
            }
            if (CollectionUtils.isEmpty(essStationBo.getDeviceNameList()) && essStationBo.getDeviceNameList().isEmpty()) {
                return ResHelper.pamIll();
            }
            for (String deviceName : essStationBo.getDeviceNameList()) {
                if (!deviceDao.query().is(DEVICE_NAME, deviceName).is("bindStation", false).exists()) {
                    return ResHelper.pamIll();
                }
            }
            if (StringUtils.isEmpty(essStationBo.getProvince()) || StringUtils.isEmpty(essStationBo.getCity())) {
                Device device = deviceDao.query().is(DEVICE_NAME, essStationBo.getDeviceNameList().get(0)).result();
                essStationBo.setProvince(device.getProvince());
                essStationBo.setCity(device.getCity());
            }
            EssStation essStation = new EssStation();
            BeanUtils.copyProperties(essStationBo, essStation);
            if (!StringUtils.isEmpty(essStationBo.getUploadImgId())) {
                BuguQuery<UploadEntity> uploadEntityBuguQuery = uploadEntityDao.query().is("_id", essStationBo.getUploadImgId());
                UploadEntity uploadEntity = uploadEntityBuguQuery.result();
                if (uploadEntity.getType().equals(STATION)) {
                    essStation.setStationImgUrls(uploadEntity.getImgUrl());
                    if (uploadEntityDao.update().set("bindImg", true).execute(uploadEntityBuguQuery).isUpdateOfExisting()) {
                        log.info("电站绑定图片成功");
                    } else {
                        log.error("电站绑定图片失败");
                    }
                } else {
                    return ResHelper.pamIll();
                }
            }
            if (deviceDao.update().set("bindStation", true).execute(deviceDao.query().in(DEVICE_NAME, essStationBo.getDeviceNameList())).isUpdateOfExisting()) {
                essStationDao.insert(essStation);
                return ResHelper.success("添加电站成功");
            } else {
                return ResHelper.error("电站绑定设备失败");
            }
        } catch (Exception e) {
            log.error("添加电站失败:{}", e);
            return ResHelper.error("添加电站失败");
        }
    }

    public ResHelper<Void> updateStation(EssStationBo essStationBo) {
        try {
            EssStation essStationOld = essStationDao.query().is("_id", essStationBo.getId()).result();
            if (essStationOld == null) {
                return ResHelper.pamIll();
            }
            BuguUpdater<EssStation> essStationBuguUpdater = essStationDao.update();
            if (!StringUtils.isEmpty(essStationBo.getStationName())) {
                essStationBuguUpdater.set("stationName", essStationBo.getStationName());
            }
            if (!StringUtils.isEmpty(essStationBo.getCode())) {
                essStationBuguUpdater.set("code", essStationBo.getCode());
            }
            if (CollectionUtils.isEmpty(essStationBo.getDeviceNameList()) && essStationBo.getDeviceNameList().isEmpty()) {
                return ResHelper.pamIll();
            }
            essStationBuguUpdater.set("deviceNameList", essStationBo.getDeviceNameList());
            if (!StringUtil.isEmpty(essStationBo.getProvince())) {
                if (!propertiesConfig.getProToCitys().containsKey(essStationBo.getProvince())) {
                    return ResHelper.pamIll();
                }
                if (!StringUtils.isEmpty(essStationBo.getCity()) && !propertiesConfig.getProToCitys().get(essStationBo.getProvince()).contains(essStationBo.getCity())) {
                    return ResHelper.pamIll();
                }
            }
            if (!StringUtils.isEmpty(essStationBo.getCity())) {
                if (!propertiesConfig.getCitys().containsKey(essStationBo.getCity())
                        || !propertiesConfig.getCitys().get(essStationBo.getCity()).equals(essStationBo.getProvince())) {
                    return ResHelper.pamIll();
                }
            }
            if (!StringUtils.isEmpty(essStationBo.getProvince())) {
                essStationBuguUpdater.set("province", essStationBo.getProvince());
            }
            if (!StringUtils.isEmpty(essStationBo.getCity())) {
                essStationBuguUpdater.set("city", essStationBo.getCity());
            }
            if (!StringUtils.isEmpty(essStationBo.getUploadImgId())) {
                BuguQuery<UploadEntity> uploadEntityBuguQuery = uploadEntityDao.query().is("_id", essStationBo.getUploadImgId());
                UploadEntity uploadEntity = uploadEntityBuguQuery.result();
                if (uploadEntity == null || !uploadEntity.getType().equals(STATION)) {
                    return ResHelper.pamIll();
                }
                essStationBuguUpdater.set("stationImgUrls", uploadEntity.getImgUrl());
                essStationBuguUpdater.set("uploadImgId", essStationBo.getUploadImgId());
                if (!StringUtils.isEmpty(essStationOld.getUploadImgId())) {
                    if (uploadEntityDao.update().set("bindImg", false).execute(uploadEntityDao.query().is("_id", essStationOld.getUploadImgId())).isUpdateOfExisting()) {
                        log.info("电站 上传图片解绑成功");
                    } else {
                        log.error("电站 上传图片解绑失败");
                    }
                }
                if (uploadEntityDao.update().set("bindImg", true).execute(uploadEntityBuguQuery).isUpdateOfExisting()) {
                    log.info("电站 上传图片绑定成功");
                } else {
                    log.error("电站 上传图片绑定失败");
                }
            } else {
                if (!StringUtils.isEmpty(essStationOld.getUploadImgId())) {
                    if (uploadEntityDao.update().set("bindImg", false).execute(uploadEntityDao.query().is("_id", essStationOld.getUploadImgId())).isUpdateOfExisting()) {
                        log.info("电站 上传图片解绑成功");
                    } else {
                        log.error("电站 上传图片解绑失败");
                    }
                }
                essStationBuguUpdater.set("stationImgUrls", new ArrayList<>());
                essStationBuguUpdater.set("uploadImgId", null);
            }
            /**
             * 解除绑定
             */
            if (!CollectionUtils.isEmpty(essStationOld.getDeviceNameList()) && !essStationOld.getDeviceNameList().isEmpty()) {
                if (deviceDao.update().set("bindStation", false).execute(deviceDao.query().in(DEVICE_NAME, essStationOld.getDeviceNameList())).isUpdateOfExisting()) {
                    log.info("电站 解绑设备成功");
                } else {
                    log.error("电站 解绑设备失败");
                }
            }
            if (deviceDao.update().set("bindStation", true).execute(deviceDao.query().in(DEVICE_NAME, essStationBo.getDeviceNameList())).isUpdateOfExisting()) {
                log.info("电站 绑定设备成功");
            } else {
                log.error("电站 绑定设备失败");
            }
            if (essStationBuguUpdater.execute(essStationDao.query().is("_id", essStationBo.getId())).isUpdateOfExisting()) {
                return ResHelper.success("修改电站信息成功");
            } else {
                return ResHelper.error("修改电站信息失败");
            }
        } catch (Exception e) {
            log.error("修改电站信息失败", e);
            return ResHelper.error("修改电站信息失败");
        }
    }

    public ResHelper<BuguPageQuery.Page<EssStationVo>> selectStation(Map<String, Object> params, SysUser user) {
        if (propertiesConfig.verify(params)) {
            BuguPageQuery<EssStation> query = essStationDao.pageQuery();
            String province = (String) params.get(PROVINCE);
            String city = (String) params.get("city");
            if (!org.springframework.util.StringUtils.isEmpty(province)) {
                query.is(PROVINCE, province);
            }
            if (!org.springframework.util.StringUtils.isEmpty(city)) {
                query.is("city", city);
            }
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                query.in("_id", user.getStationList());
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
                if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                    List<Device> deviceList = deviceDao.query().in("deviceName", essStation.getDeviceNameList()).results();
                    List<DeviceInfoVo> deviceVos = deviceList.stream().map(device -> {
                        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
                        BeanUtils.copyProperties(device, deviceInfoVo);
                        return deviceInfoVo;
                    }).collect(Collectors.toList());
                    essStationVo.setDeviceInfoVos(deviceVos);
                }
                return essStationVo;
            }).collect(Collectors.toList());
            BuguPageQuery.Page<EssStationVo> essStationVoPage = new BuguPageQuery.Page<>(essStationPage.getPage(), essStationPage.getPageSize(), essStationPage.getTotalRecord(), essStationVos);
            return ResHelper.success("", essStationVoPage);
        } else {
            return ResHelper.pamIll();
        }

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
        } catch (Exception e) {
            log.error("删除失败:{}", e);
            return ResHelper.error("删除失败");
        }

    }

    public EssStationVo infoStationById(String stationId) {
        EssStation essStation = essStationDao.query().is("_id", stationId).result();
        EssStationVo essStationVo = new EssStationVo();
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
            essStationVo.setDeviceInfoVos(deviceInfoVos);
        }
        return essStationVo;
    }

    public ResHelper<Map<String, Set<String>>> queryProvinces() {
        return ResHelper.success("", propertiesConfig.getProToCitys());
    }

    public ResHelper<UploadEntity> upload(MultipartFile[] files, String type, String deviceNames) {
        UploadEntity uploadEntity = new UploadEntity();
        List<String> imgUrl = new ArrayList<>();
        List<String> originalFilenames = new ArrayList<>();
        List<String> fullPaths = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getOriginalFilename();
                if (!StringUtils.isEmpty(filename)) {
                    Date date = new Date();
                    originalFilenames.add(filename);
                    String fileF = filename.substring(filename.lastIndexOf("."), filename.length());
                    String dateToStr = DateUtils.dateToStr(date);
                    if (type.equals(STATION)) {
                        String imgUrlStr = System.currentTimeMillis() + "_" + type + "_" + i + fileF;
                        String stationImgUrlPc = propertiesConfig.getImageGenerationLocationPc() + dateToStr + File.separator + imgUrlStr;
                        File fPc = new File(stationImgUrlPc);
                        makeSureFileExist(fPc);
                        files[i].transferTo(fPc);
                        String stationImgUrlPh = propertiesConfig.getImageGenerationLocationPh() + dateToStr + File.separator + imgUrlStr;
                        File fPh = new File(stationImgUrlPh);
                        makeSureFileExist(fPh);
                        files[i].transferTo(fPh);
                        imgUrl.add(dateToStr + File.separator + imgUrlStr);
                        fullPaths.add(stationImgUrlPc);
                        fullPaths.add(stationImgUrlPh);
                    } else if (type.equals(ENERGY_STORAGE)) {
                        String[] deviceNamesArray = deviceNames.split(",");
                        uploadEntity.setDeviceNames(Arrays.asList(deviceNamesArray));
                        for (String deviceName : uploadEntity.getDeviceNames()) {
                            String imgUrlStr = deviceName + "_" + System.currentTimeMillis() + "_" + type + "_" + i + fileF;
                            String deviceNameImgUrl = propertiesConfig.getImageGenerationLocationPc() + dateToStr + File.separator + imgUrlStr;
                            File f = new File(deviceNameImgUrl);
                            makeSureFileExist(f);
                            files[i].transferTo(f);
                            imgUrl.add(dateToStr + File.separator + imgUrlStr);
                            String deviceNameImgUrlPh = propertiesConfig.getImageGenerationLocationPh() + dateToStr + File.separator + imgUrlStr;
                            File fPh = new File(deviceNameImgUrlPh);
                            makeSureFileExist(fPh);
                            files[i].transferTo(fPh);
                            fullPaths.add(deviceNameImgUrl);
                            fullPaths.add(deviceNameImgUrlPh);
                        }
                    }
                    uploadEntity.setImgOfFullPath(fullPaths);
                    uploadEntity.setType(type);
                    uploadEntity.setImgUrl(imgUrl);
                    uploadEntity.setOriginalFilename(originalFilenames);
                    uploadEntity.setUploadTime(new Date());
                }
            }
            if (!CollectionUtils.isEmpty(imgUrl) && !imgUrl.isEmpty()) {
                if (type.equals(ENERGY_STORAGE)) {
                    List<String> strList = Arrays.asList(deviceNames.split(","));
                    uploadEntityDao.update().set("bindImg", false).execute(uploadEntityDao.query().is("deviceNames", strList));
                    uploadEntityDao.insert(uploadEntity);
                    if (!StringUtils.isEmpty(uploadEntity.getId())) {
                        ResHelper<Void> resHelper = deviceService.saveDeviceImg(new DeviceImgBo(deviceNames, uploadEntity.getId()));
                        if (resHelper.getCode() == 1) {
                            log.info("上传图片成功:设备列表为{}", strList);
                            return ResHelper.success("上传成功", uploadEntity);
                        }
                    }
                    return ResHelper.success("上传失败");
                } else {
                    uploadEntityDao.insert(uploadEntity);
                    log.info("上传电站图片成功");
                    return ResHelper.success("上传成功", uploadEntity);
                }
            }
            return ResHelper.error("上传失败");
        } catch (Exception e) {
            log.error("上传失败:{}", e);
            return ResHelper.error("上传失败");
        }

    }

    private void makeSureFileExist(File file) throws IOException {
        if (file.exists()) {
            return;
        }
        synchronized (Thread.currentThread()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        }
    }


}
