package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.PropertyUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
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
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private ImgUploadsDao imgUploadsDao;
    @Resource
    private GridFsTemplate gridFsTemplate;

    public List<EssStation> getEssStationList(List<String> essStationIds) {
        if (!CollectionUtils.isEmpty(essStationIds) && !essStationIds.isEmpty()) {
            Query batchQuery = new Query();
            batchQuery.addCriteria(Criteria.where("id").in(essStationIds));
            List<EssStation> essStations = essStationDao.findEssStationBatch(batchQuery);
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


    public ResHelper<Void> deleteStation(List<String> stationIds) {
        try {
            if (!CollectionUtils.isEmpty(stationIds) && !stationIds.isEmpty()) {
                Query essStationQuery = new Query();
                essStationQuery.addCriteria(Criteria.where("id").in(stationIds));
                List<EssStation> essStations = essStationDao.findEssStationBatch(essStationQuery);
                if (sysUserDao.exists(new Query().addCriteria(Criteria.where("stationList").in(stationIds)))) {
                    return ResHelper.error("请先解除用户与电站的绑定");
                }
                for (EssStation essStation : essStations) {
                    Query updateQuery = new Query().addCriteria(Criteria.where(DEVICE_NAME).in(essStation.getDeviceNameList()));
                    Update update = new Update().set("bindStation", false);
                    deviceDao.updateByQuery(updateQuery, update);
                }
                Query removeQuery = new Query();
                removeQuery.addCriteria(Criteria.where("id").in(stationIds));
                essStationDao.remove(removeQuery);
            }
            return ResHelper.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败:{}", e);
            return ResHelper.error("删除失败");
        }
    }

    public EssStationVo infoStationById(String stationId) {
        Query findQuery = new Query().addCriteria(Criteria.where("id").is(stationId));
        EssStation essStation = essStationDao.findEssStation(findQuery);
        EssStationVo essStationVo = new EssStationVo();
        if (essStation != null) {
            BeanUtils.copyProperties(essStation, essStationVo);
            List<String> deviceNameList = essStation.getDeviceNameList();
            essStationVo.setAreaCity(essStation.getProvince() + "-" + essStation.getCity());
            List<Device> deviceList = deviceDao.findBatchDevices(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(deviceNameList)));
            List<DeviceInfoVo> deviceInfoVos = deviceList.stream().map(device -> {
                DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
                BeanUtils.copyProperties(device, deviceInfoVo);
                deviceInfoVo.setAreaCity(device.getProvince() + "-" + device.getCity());
                deviceInfoVo.setApplicationScenariosItemMsg(PropertyUtil.getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
                deviceInfoVo.setApplicationScenariosMsg(PropertyUtil.getApplicationScenariosMsg(device.getApplicationScenarios()));
                Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where(DEVICE_NAME).is(device.getDeviceName())),
                        Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))),
                        Aggregation.limit(1));
                BamsDataDicBA bamsDataDicBA = bamsDataDicBADao.aggregationResult(aggregation);
                Date date = new Date();
                Date addDateMinutes = DateUtils.addDateMinutes(bamsDataDicBA.getGenerationDataTime(), 30);
                if (!addDateMinutes.before(date)) {
                    deviceInfoVo.setDeviceStatus("正常运行");
                } else {
                    deviceInfoVo.setDeviceStatus("停机");
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


    public ResHelper<Page<EssStationVo>> selectStation(Integer page, Integer pageSize, String name, String country, String province, String city, String stationName, SysUser user) {
        Query findEssStationQuery = new Query();

        if (!StringUtils.isEmpty(province)) {
            findEssStationQuery.addCriteria(Criteria.where(PROVINCE).is(province));
        }
        if (!StringUtils.isEmpty(city)) {
            findEssStationQuery.addCriteria(Criteria.where("city").is(city));
        }
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            findEssStationQuery.addCriteria(Criteria.where("id").in(user.getStationList()));
        }
        if (!StringUtils.isEmpty(stationName)) {
            findEssStationQuery.addCriteria(Criteria.where("stationName").regex(stationName));
        }
        findEssStationQuery.skip(((long) page - 1) * pageSize).limit(pageSize).with(Sort.by(Sort.Order.desc("id")));
        List<EssStation> essStationList = essStationDao.findEssStationBatch(findEssStationQuery);
        long countQuery = essStationDao.countQuery(findEssStationQuery);
        List<EssStationVo> essStationVos = getEssStationVos(essStationList);
        return ResHelper.success("", new Page<>(page, pageSize, countQuery, essStationVos));
    }

    private List<EssStationVo> getEssStationVos(List<EssStation> essStationList) {
        List<EssStationVo> essStationVos = essStationList.stream().map(essStation -> {
            EssStationVo essStationVo = new EssStationVo();
            BeanUtils.copyProperties(essStation, essStationVo);
            essStationVo.setAreaCity(essStation.getProvince() + "-" + essStation.getCity());
            if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                List<Device> deviceList = deviceDao.findBatchDevices(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(essStation.getDeviceNameList())));
                List<DeviceInfoVo> deviceVos = deviceList.stream().map(device -> {
                    DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
                    BeanUtils.copyProperties(device, deviceInfoVo);
                    return deviceInfoVo;
                }).collect(Collectors.toList());
                essStationVo.setDeviceInfoVos(deviceVos);
            }
            return essStationVo;
        }).collect(Collectors.toList());
        return essStationVos;
    }

    public ResHelper<Void> save(String stationName, List<String> deviceNameList, MultipartFile[] files, String province, String city) {
        try {
            for (String deviceName : deviceNameList) {
                if (!deviceDao.exists(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("bindStation").is(false)))) {
                    return ResHelper.pamIll();
                }
            }
            EssStation essStation = new EssStation();
            Device device = deviceDao.findDevice(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceNameList.get(0))));
            if (StringUtils.isEmpty(province) || StringUtils.isEmpty(city)) {
                essStation.setProvince(device.getProvince());
                essStation.setCity(device.getCity());
            } else {
                essStation.setProvince(province);
                essStation.setCity(city);
            }
            essStation.setStationName(stationName);
            essStation.setLatitude(device.getLatitude());
            essStation.setLongitude(device.getLongitude());
            essStation.setDeviceNameList(deviceNameList);
            if (files.length > 0) {
                for (MultipartFile file : files) {
                    byte[] b = new byte[3];
                    file.getInputStream().read(b, 0, b.length);
                    String hexString = PatternUtil.bytesToHexString(b);
                    String ooo = PatternUtil.checkType(hexString.toUpperCase());
                    if (StringUtils.isEmpty(ooo)) {
                        return ResHelper.pamIll();
                    }
                    if (ooo.equals("0000")) {
                        return ResHelper.pamIll();
                    }
                }
                List<String> stationImgIdWithUrls = uploadFiles(files);
                if (CollectionUtils.isEmpty(stationImgIdWithUrls) && stationImgIdWithUrls.isEmpty()) {
                    return ResHelper.error("电站绑定设备失败");
                }
                essStation.setStationImgIdWithUrls(stationImgIdWithUrls);
            }
            deviceDao.updateByQuery(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(deviceNameList)), new Update().set("bindStation", true));
            essStationDao.saveEssStation(essStation);
            return ResHelper.success("添加电站成功");
        } catch (Exception e) {
            log.error("添加电站失败:{}", e);
            return ResHelper.error("添加电站失败");
        }
    }

    private List<String> uploadFiles(MultipartFile[] files) {
        List<ImgUpload> imgUploads = new ArrayList<>();
        List<String> imgResultList = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                List<String> fullPaths = new ArrayList<>();
                String filename = files[i].getOriginalFilename();
                if (!StringUtils.isEmpty(filename)) {
                    ImgUpload imgUpload = new ImgUpload();
                    imgUpload.setOriginalFilename(filename);
                    Date date = new Date(System.currentTimeMillis());
                    String fileF = filename.substring(filename.lastIndexOf("."), filename.length());
                    String dateToStr = DateUtils.dateToStr(date);
                    String imgUrlStr = System.currentTimeMillis() + "_" + STATION + "_" + i + fileF;
                    String stationImgUrlPc = propertiesConfig.getImageGenerationLocationPc() + dateToStr + File.separator + imgUrlStr;
                    File fPc = new File(stationImgUrlPc);
                    makeSureFileExist(fPc);
                    files[i].transferTo(fPc);
                    String stationImgUrlPh = propertiesConfig.getImageGenerationLocationPh() + dateToStr + File.separator + imgUrlStr;
                    File fPh = new File(stationImgUrlPh);
                    makeSureFileExist(fPh);
                    files[i].transferTo(fPh);
                    fullPaths.add(stationImgUrlPc);
                    fullPaths.add(stationImgUrlPh);
                    imgUpload.setImgOfFullPath(fullPaths);
                    ObjectId objectId = gridFsTemplate.store(files[i].getInputStream(), files[i].getOriginalFilename(),files[i].getContentType());
                    String fileId = objectId.toString();
                    String imgUrl = File.separator + dateToStr + File.separator + imgUrlStr;
                    String imgResult = fileId + "~" + imgUrl;
                    imgUpload.setImgId(fileId);
                    imgUpload.setType(STATION);
                    imgResultList.add(imgResult);
                    imgUploads.add(imgUpload);
                }
            }
            imgUploadsDao.saveImgUploads(imgUploads);
            log.info("上传电站图片成功");
        } catch (Exception e) {
            log.info("上传电站图片失败:{}", e);
            return new ArrayList<>();
        }
        return imgResultList;
    }

    public ResHelper<Void> updateStation(String stationName, List<String> deviceNameList, MultipartFile[] files, String province, String city, String stationId, List<String> filesId) {
        try {
            Update essStationUpdate = new Update();
            essStationUpdate.set("stationName", stationName);
            if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city)) {
                essStationUpdate.set("province", province);
                essStationUpdate.set("city", city);
            }
            if (files.length > 0) {
                for (MultipartFile file : files) {
                    byte[] b = new byte[3];
                    file.getInputStream().read(b, 0, b.length);
                    String hexString = PatternUtil.bytesToHexString(b);
                    String ooo = PatternUtil.checkType(hexString.toUpperCase());
                    if (StringUtils.isEmpty(ooo)) {
                        return ResHelper.pamIll();
                    }
                    if (ooo.equals("0000")) {
                        return ResHelper.pamIll();
                    }
                }
                List<String> stationImgIdWithUrls = uploadFiles(files);
                if (!CollectionUtils.isEmpty(filesId) && !filesId.isEmpty()) {
                    filesId.addAll(stationImgIdWithUrls);
                } else {
                    filesId = stationImgIdWithUrls;
                }
            }
            EssStation essStationOld = essStationDao.findEssStation(new Query().addCriteria(Criteria.where("id").is(stationId)));
            essStationUpdate.set("deviceNameList", deviceNameList);
            essStationUpdate.set("stationImgIdWithUrls", filesId);
            essStationDao.updateByQuery(new Query().addCriteria(Criteria.where("id").is(stationId)), essStationUpdate);
            deviceDao.updateByQuery(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(essStationOld.getDeviceNameList())), new Update().set("bindStation", false));
            deviceDao.updateByQuery(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(deviceNameList)), new Update().set("bindStation", true));
            log.info("修改电站图片成功:{}");
        } catch (Exception e) {
            log.error("修改电站图片失败:{}", e);
            return ResHelper.error("修改电站信息失败");
        }
        return ResHelper.success("修改电站信息成功");
    }

    public boolean existStation(String stationName) {

        return essStationDao.exists(new Query().addCriteria(Criteria.where("stationName").is(stationName)));
    }

    public boolean existStationWithId(String stationName, String stationId) {
        return essStationDao.exists(new Query().addCriteria(Criteria.where("id").is(stationId)).addCriteria(Criteria.where("stationName").is(stationName)));
    }


    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return essStationDao.aggregation(aggregation);
    }
}
