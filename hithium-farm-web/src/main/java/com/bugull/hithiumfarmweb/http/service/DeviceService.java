package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.Corp;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.http.entity.DeviceAreaEntity;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PropertyUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.hithiumfarmweb.utils.SecretUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.utils.DateUtils.*;

@Service
public class DeviceService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PccsDao pccsDao;
    @Resource
    private EquipmentDao equipmentDao;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private IncomeEntityDao incomeEntityDao;
    @Resource
    private PcsChannelDicDao pcsChannelDicDao;
    @Resource
    private EssStationService  essStationService;
    @Resource
    private CorpDao corpDao;
    @Resource
    private ImgUploadsDao imgUploadsDao;
    @Resource
    private CubeDao cubeDao;
    @Resource
    private GridFsTemplate gridFsTemplate;

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    public ResHelper<List<ProvinceVo>> aggregationArea() {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                Criteria criteria = new Criteria();
                criteria.andOperator(Criteria.where("deviceNameList").in(deviceNames));
                aggregationOperations.add(Aggregation.match(criteria));
            }
        }
        aggregationOperations.add(Aggregation.group(PROVINCE).count().as("count"));
        aggregationOperations.add(Aggregation.sort(Sort.by(Sort.Order.desc("count"))));
        List<BasicDBObject> aggregation = essStationService.aggregation(Aggregation.newAggregation(aggregationOperations));
        List<ProvinceVo> provinceVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(aggregation) && !aggregation.isEmpty()) {
            for(BasicDBObject object : aggregation){
                ProvinceVo provinceVo = new ProvinceVo();
                String province = object.get("_id").toString();
                long count = (long)object.get("count");
                Criteria matchCriteria = new Criteria();
                matchCriteria.andOperator(Criteria.where(PROVINCE).is(province));
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                        matchCriteria.andOperator(Criteria.where(DEVICE_NAME).in(deviceNames));
                    }
                }
                Aggregation deviceAggregation = Aggregation.newAggregation(
                        Aggregation.match(matchCriteria),
                        Aggregation.group("city").count().as("count"),
                        Aggregation.sort(Sort.by(Sort.Order.desc("count"))));
                List<BasicDBObject> areaAggregationCityResults = deviceDao.aggregationResult(deviceAggregation);
                List<CityVo> cityVoList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(areaAggregationCityResults) && !areaAggregationCityResults.isEmpty()) {
                    areaAggregationCityResults.forEach(areaAggregationCityResult -> {
                        CityVo cityVo = new CityVo();
                        String city = areaAggregationCityResult.get("_id").toString();
                        long cityResultCount = (long) areaAggregationCityResult.get("count");
                        cityVo.setCity(city);
                        cityVo.setNum(cityResultCount);
                        Query deviceQuery = new Query();
                        deviceQuery.addCriteria(Criteria.where(PROVINCE).is(province)).addCriteria(Criteria.where("city").is(city));
                        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                                deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
                            }
                        }
                        List<DeviceAreaEntity> deviceAreaEntities = deviceDao.findBatchDevicesArea(deviceQuery);
                        cityVo.setDeviceAreaEntities(deviceAreaEntities);
                    });
                }
                provinceVo.setProvince(province);
                provinceVo.setNum(count);
                provinceVo.setCityVoList(cityVoList);
                provinceVos.add(provinceVo);
            };
        }
        return ResHelper.success("", provinceVos);
    }

    public ResHelper<List<Device>> queryDetailAreaByCity(String city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("city").is(city)).fields().include("longitude").include("latitude");
        List<Device> deviceList = deviceDao.findBatchDevices(query);
        return ResHelper.success("", deviceList);
    }

    public DeviceVo query(String deviceName) {
        Device device = deviceDao.findDevice(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)));
        DeviceVo deviceVo = new DeviceVo();
        if (device != null) {
            deviceVo.setDeviceName(device.getDeviceName());
            deviceVo.setName(device.getName());
            deviceVo.setDescription(device.getDescription());
            EquipmentBo equipmentBo = new EquipmentBo();
            equipmentBo.setName(device.getName());
            equipmentBo.setId(device.getId());
            List<EquipmentBo> equipmentBos = new ArrayList<>();
            equipmentBo.setEquipmentBo(equipmentBos);
            Query pccsQuery = new Query();
            pccsQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("stationId").is(device.getStationId()));
            List<Pccs> pccsList = pccsDao.findBatchPccs(pccsQuery);
            pccsList.stream().forEach(pccs -> {
                EquipmentBo equipment = new EquipmentBo();
                equipment.setName(pccs.getName());
                equipment.setId(pccs.getId());
                List<EquipmentBo> equipmentBoList = new ArrayList<>();
                equipment.setEquipmentBo(equipmentBoList);
                /**
                 * 并网口后下级目录   电池堆、PCS、其他、电表
                 */
                Query batchEquipmentQuery = new Query();
                batchEquipmentQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("enabled").is(true));
                List<Equipment> equipments = equipmentDao.findBatchEquipment(batchEquipmentQuery);
                EquipmentBo equipmentBoOfBams = new EquipmentBo();
                /**
                 * 电池堆
                 */
                getBoOfBamsData(equipments, deviceName, equipmentBoOfBams, equipmentBoList);
                /**
                 * PCS
                 */
                getBoOfPcsData(equipments, equipmentBoList);
                /**
                 * 电表
                 */
                getBoOfAmmeterData(equipments, deviceName, equipmentBoList);
                /**
                 * 消防主机
                 */
                getBoOfFireData(equipments, deviceName, equipmentBoList);
                /**
                 * 附属表
                 */
                getBoOfSubsidiaryData(equipments, deviceName, equipmentBoList, pccs, equipmentBos);
                equipmentBos.add(equipment);
            });
            deviceVo.setEquipmentBo(equipmentBo);
        }
        return deviceVo;
    }

    private void getBoOfSubsidiaryData(List<Equipment> equipments, String deviceName, List<EquipmentBo> equipmentBoList, Pccs pccs, List<EquipmentBo> equipmentBos) {
        EquipmentBo subsidiaryBo = new EquipmentBo();
        subsidiaryBo.setName("附属件");
        List<Equipment> subsidiaryEquipments;
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            subsidiaryEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 13 || equip.getEquipmentId() == 14 || equip.getEquipmentId() == 55)
                    .collect(Collectors.toList());
        } else {
            subsidiaryEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 1 || equip.getEquipmentId() == 12 || equip.getEquipmentId() == 13)
                    .collect(Collectors.toList());
        }
        List<EquipmentBo> subsidiaryEquipBo = subsidiaryEquipments.stream().map(subsidiaryEquipment -> {
            EquipmentBo equipmentBo1 = new EquipmentBo();
            BeanUtils.copyProperties(subsidiaryEquipment, equipmentBo1);
            return equipmentBo1;
        }).collect(Collectors.toList());
        subsidiaryBo.setEquipmentBo(subsidiaryEquipBo);
        equipmentBoList.add(subsidiaryBo);
        Query findBatchEquipmentQuery = new Query();
        findBatchEquipmentQuery.addCriteria(Criteria.where("enabled").is(true))
                .addCriteria(Criteria.where("id").in(pccs.getEquipmentIds())).fields().include("name").include("id").include("equipmentId");
        List<Equipment> equips = equipmentDao.findBatchEquipment(findBatchEquipmentQuery);
        equips.stream().forEach(ip -> {
            EquipmentBo mentBo = new EquipmentBo();
            mentBo.setName(ip.getName());
            mentBo.setId(ip.getId());
            mentBo.setEquipmentBo(null);
            mentBo.setEquipmentId(ip.getEquipmentId());
            equipmentBos.add(mentBo);
        });
    }

    private void getBoOfFireData(List<Equipment> equipments, String deviceName, List<EquipmentBo> equipmentBoList) {
        EquipmentBo fireEquipmentBo = new EquipmentBo();
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            List<Equipment> fireEquipments = equipments.stream().filter(equip ->
                    equip.getEquipmentId() == 2
            ).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fireEquipments) && !fireEquipments.isEmpty()) {
                Equipment fireEquipment = fireEquipments.get(0);
                fireEquipmentBo.setName(fireEquipment.getName());
                fireEquipmentBo.setEquipmentId(2);
                fireEquipmentBo.setEquipmentBo(null);
            }
        } else {
            List<Equipment> fireEquipments = equipments.stream().filter(equip ->
                    equip.getEquipmentId() == 1
            ).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fireEquipments) && !fireEquipments.isEmpty()) {
                Equipment fireEquipment = fireEquipments.get(0);
                fireEquipmentBo.setName(fireEquipment.getName());
                fireEquipmentBo.setEquipmentId(1);
                fireEquipmentBo.setEquipmentBo(null);
            }
        }
        if (!StringUtils.isEmpty(fireEquipmentBo.getName())) {
            equipmentBoList.add(fireEquipmentBo);
        }
    }

    private void getBoOfAmmeterData(List<Equipment> equipments, String deviceName, List<EquipmentBo> equipmentBoList) {
        EquipmentBo ammeterBo = new EquipmentBo();
        ammeterBo.setName("电表");
        List<Equipment> ammeterEquipments;

        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            ammeterEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 4 || equip.getEquipmentId() == 7 || equip.getEquipmentId() == 12)
                    .collect(Collectors.toList());
        } else {
            ammeterEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 3 || equip.getEquipmentId() == 6 || equip.getEquipmentId() == 11)
                    .collect(Collectors.toList());
        }
        List<EquipmentBo> equipmentBoList1 = ammeterEquipments.stream().map(ammeterEquipment -> {
            EquipmentBo equip = new EquipmentBo();
            BeanUtils.copyProperties(ammeterEquipment, equip);
            return equip;
        }).collect(Collectors.toList());
        ammeterBo.setEquipmentBo(equipmentBoList1);
        equipmentBoList.add(ammeterBo);
    }

    private void getBoOfPcsData(List<Equipment> equipments, List<EquipmentBo> equipmentBoList) {
        List<Equipment> pcsCabinetEquipments = equipments.stream().
                filter(equip -> equip.getEquipmentId() == 15).collect(Collectors.toList());
        Equipment pcsCabinet = pcsCabinetEquipments.get(0);
        List<Equipment> pcsChannelEquipments = equipments.stream()
                .filter(equip -> equip.getEquipmentId() > 15 && equip.getEquipmentId() < 34).collect(Collectors.toList());
        List<EquipmentBo> boList = pcsChannelEquipments.stream().map(pcsChannelEquipment -> {
            EquipmentBo equip = new EquipmentBo();
            BeanUtils.copyProperties(pcsChannelEquipment, equip);
            return equip;
        }).collect(Collectors.toList());
        EquipmentBo pcsCabinetBo = new EquipmentBo();
        BeanUtils.copyProperties(pcsCabinet, pcsCabinetBo);
        pcsCabinetBo.setEquipmentBo(boList);
        equipmentBoList.add(pcsCabinetBo);
    }

    private void getBoOfBamsData(List<Equipment> equipments, String deviceName, EquipmentBo equipmentBoOfBams, List<EquipmentBo> equipmentBoList) {
        List<Equipment> bamEquipments;
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            bamEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 36).collect(Collectors.toList());
        } else {
            bamEquipments = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 14).collect(Collectors.toList());
        }
        Equipment bamsEquipment = bamEquipments.get(0);
        BeanUtils.copyProperties(bamsEquipment, equipmentBoOfBams);
        equipmentBoList.add(equipmentBoOfBams);
        List<Equipment> bamsClusterEquipment = new ArrayList<>();
        List<Equipment> clusterEquipment;
        List<Equipment> cabinEquipment;
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            clusterEquipment = equipments.stream().
                    filter(equip -> equip.getEquipmentId() > 36 && equip.getEquipmentId() < 53).collect(Collectors.toList());
            cabinEquipment = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 34 || equip.getEquipmentId() == 35 || equip.getEquipmentId() == 53 || equip.getEquipmentId() == 54).collect(Collectors.toList());
        } else {
            clusterEquipment = equipments.stream().
                    filter(equip -> equip.getEquipmentId() > 35 && equip.getEquipmentId() < 54 && equip.getEquipmentId() != 44 && equip.getEquipmentId() != 45).collect(Collectors.toList());
            cabinEquipment = equipments.stream().
                    filter(equip -> equip.getEquipmentId() == 34 || equip.getEquipmentId() == 35 || equip.getEquipmentId() == 44 || equip.getEquipmentId() == 45).collect(Collectors.toList());
        }
        bamsClusterEquipment.addAll(clusterEquipment);
        bamsClusterEquipment.addAll(cabinEquipment);
        List<EquipmentBo> bos = bamsClusterEquipment.stream().map(clusterEquip -> {
            EquipmentBo equipOfBo = new EquipmentBo();
            BeanUtils.copyProperties(clusterEquip, equipOfBo);
            return equipOfBo;
        }).collect(Collectors.toList());
        equipmentBoOfBams.setEquipmentBo(bos);
    }




    public ResHelper<Void> modifyDeviceInfo(ModifyDeviceBo modifyDeviceBo) {
        List<TimeOfPriceBo> priceOfTime = modifyDeviceBo.getPriceOfTime();
        if (!priceOfTime.isEmpty()) {
            SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
            List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());

            String[] arrayDeviceNames = modifyDeviceBo.getDeviceNames().split(",");
            if (arrayDeviceNames == null || arrayDeviceNames.length == 0) {
                return ResHelper.pamIll();
            }
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.containsAll(Arrays.asList(arrayDeviceNames))) {
                    return ResHelper.error(NO_MODIFY_PERMISSION);
                }
            }
            log.info("批量修改设备电价  设备列表为:{}", Arrays.asList(arrayDeviceNames));
            /**
             * 格式校验map
             */
            List<String> list = new ArrayList<>();
            for (TimeOfPriceBo timeOfPriceBo : priceOfTime) {
                if (timeOfPriceBo != null && !StringUtils.isEmpty(timeOfPriceBo.getPrice()) && !StringUtils.isEmpty(timeOfPriceBo.getTime())) {
                    String time = timeOfPriceBo.getTime();
                    String[] strs = time.split(",");
                    for (String str : strs) {
                        list.add(str);
                        String[] strings = str.split("-");
                        for (String st : strings) {
                            if (!st.matches(CHECK_TIME_FORMAT)) {
                                return ResHelper.pamIll();
                            }
                        }
                    }
                }
            }
            try {
                int num = DAY_OF_SECONDS;
                int sum = 0;
                for (String setTime : list) {
                    String[] setTimes = setTime.split("-");
                    if (setTimes.length != 2) {
                        return ResHelper.pamIll();
                    }
                    String endTime = setTimes[1];
                    String startTime = setTimes[0];
                    Integer endTimeIntHour = Integer.valueOf(endTime.split(":")[0]);
                    Integer startTimeIntHour = Integer.valueOf(startTime.split(":")[0]);
                    Integer endTimeIntMin = Integer.valueOf(endTime.split(":")[1]);
                    Integer startTimeIntMin = Integer.valueOf(startTime.split(":")[1]);
                    if (endTimeIntHour > startTimeIntHour) {
                        sum += (endTimeIntHour - startTimeIntHour) * HOUR_OF_SECONDS + (endTimeIntMin - startTimeIntMin) * 60;
                    } else if (endTimeIntHour == startTimeIntHour) {
                        if (endTimeIntMin <= startTimeIntMin) {
                            endTimeIntHour = endTimeIntHour + 24;
                        }
                        sum += (endTimeIntHour - startTimeIntHour) * HOUR_OF_SECONDS + (endTimeIntMin - startTimeIntMin) * 60;
                    } else {
                        endTimeIntHour = endTimeIntHour + 24;
                        sum += (endTimeIntHour - startTimeIntHour) * HOUR_OF_SECONDS + (endTimeIntMin - startTimeIntMin) * 60;
                    }
                }
                if (num != sum) {
                    return ResHelper.pamIll();
                }
            } catch (Exception e) {
                return ResHelper.pamIll();
            }
            for (String deviceName : deviceNames) {
                if (!deviceDao.exists(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)))) {
                    return ResHelper.error("设备不存在");
                }
            }
            Update deviceUpdate = new Update();
            deviceUpdate.set("priceOfTime", priceOfTime);
            deviceDao.updateByQuery(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(Arrays.asList(arrayDeviceNames))), deviceUpdate);
            return ResHelper.success("修改成功");
        }
        return ResHelper.pamIll();
    }

    public List<TimeOfPriceBo> selectPriceOfTime(Device device) {
        if (device != null) {
            /**
             * 获取设备时间段电价
             */
            List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
            if (!CollectionUtils.isEmpty(priceOfTime) && !priceOfTime.isEmpty()) {
                return priceOfTime;
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public List<PriceOfPercenVo> selectPriceOfPercentage(Device device) {
        if (device != null) {
            List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
            if (!CollectionUtils.isEmpty(priceOfTime) && !priceOfTime.isEmpty()) {
                Map<Integer, List<String>> map = new HashMap<>();
                for (TimeOfPriceBo priceBo : priceOfTime) {
                    String[] split = priceBo.getTime().split(",");
                    map.put(priceBo.getType(), Arrays.asList(split));
                }
                List<PriceOfPercenVo> priceOfPercentVoList = new ArrayList<>();
                List<String> outOfDay = new ArrayList<>();
                for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                    List<String> entryValue = entry.getValue();
                    for (String value : entryValue) {
                        createPriceOfPercentVo(value, entry.getKey(), priceOfPercentVoList, outOfDay);
                    }
                }
                if (!CollectionUtils.isEmpty(outOfDay) && !outOfDay.isEmpty()) {
                    for (String day : outOfDay) {
                        String[] typeStr = day.split("_");
                        createPriceOfPercentVo(typeStr[1], Integer.valueOf(typeStr[0]), priceOfPercentVoList, outOfDay);
                    }
                }
                return priceOfPercentVoList;
            }
        }
        return new ArrayList<>();
    }

    private void createPriceOfPercentVo(String value, Integer type, List<PriceOfPercenVo> priceOfPercenVoList, List<String> outOfDay) {
        try {
            PriceOfPercenVo priceOfPercenVo = new PriceOfPercenVo();
            String[] split = value.split("-");
            Calendar begin = Calendar.getInstance();
            begin.setTime(DateUtils.dateToStrWithHHmmWith(split[0]));
            Calendar end = Calendar.getInstance();
            end.setTime(DateUtils.dateToStrWithHHmmWith(split[1]));
            if (begin.before(end)) {
                String s = DateUtils.timeDifferent(end.getTime(), begin.getTime());
                priceOfPercenVo.setType(type);
                priceOfPercenVo.setTime(value);
                priceOfPercenVo.setMinute(s);
                priceOfPercenVoList.add(priceOfPercenVo);
            } else if (begin.after(end)) {
                String timeOfFirst = split[0] + END_TIME;
                String timeOfLast = START_TIME + split[1];
                outOfDay.add(type + "_" + timeOfFirst);
                outOfDay.add(type + "_" + timeOfLast);
            }
        } catch (ParseException e) {
            log.error("解析日期出错", e);
        }
    }


    public Map<String, Object> getPriceOfPercentAndTime(String deviceName) {
        Map<String, Object> result = new HashMap<>();
        Query deviceQuery = new Query();
        deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).fields().include("priceOfTime").include("timeOfPower");
        Device device = deviceDao.findDevice(deviceQuery);
        result.put("PERCENT", selectPriceOfPercentage(device));
        result.put("PRICE", selectPriceOfTime(device));
        result.put("POWER", selectPowerOfTypeTime(device));
        return result;
    }

    public List<TimeOfPriceBo> selectPriceOfTimeByDevice(String deviceName) {
        Query deviceQuery = new Query();
        deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).fields().include("priceOfTime");
        Device device = deviceDao.findDevice(deviceQuery);
        return selectPriceOfTime(device);
    }

    public DeviceInfoVo queryDeviceName(String deviceName) {
        Device device = deviceDao.findDevice(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)));
        if (device != null) {
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
            BeanUtils.copyProperties(device, deviceInfoVo);
            deviceInfoVo.setAreaCity(device.getProvince() + "-" + device.getCity());
            deviceInfoVo.setApplicationScenariosMsg(PropertyUtil.getApplicationScenariosMsg(device.getApplicationScenarios()));
            deviceInfoVo.setApplicationScenariosItemMsg(PropertyUtil.getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
            getDeviceRunStatusMsg(deviceInfoVo);
            getDeviceIncome(deviceInfoVo);
            getDeviceDayIncome(deviceInfoVo);
            getPcsDataMsg(deviceInfoVo);
            return deviceInfoVo;
        }
        return null;
    }

    private void getDeviceDayIncome(DeviceInfoVo deviceInfoVo) {
        String dateToStr = DateUtils.dateToStr(new Date());
        Query incomeQuery = new Query();
        incomeQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceInfoVo.getDeviceName()))
                .addCriteria(Criteria.where("incomeOfDay").is(dateToStr));
        IncomeEntity incomeEntity = incomeEntityDao.findIncome(incomeQuery);
        if (incomeEntity != null) {
            BigDecimal incomeBigDecimal = incomeEntity.getIncome().setScale(4, BigDecimal.ROUND_HALF_UP);
            deviceInfoVo.setDayDeviceIncome(incomeBigDecimal.toString());
        }
    }

    private void getPcsDataMsg(DeviceInfoVo deviceInfoVo) {
        Query batchEquipmentQuery = new Query();
        batchEquipmentQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceInfoVo.getDeviceName()));
        List<Equipment> equipmentList = equipmentDao.findBatchEquipment(batchEquipmentQuery);
        List<PcsChannelDic> pcsChannelDicsList = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            Criteria criteria = new Criteria();
            criteria.and(DEVICE_NAME).is(equipment.getDeviceName());
            criteria.and(EQUIPMENT_ID).is(equipment.getEquipmentId());
            Query pcsQuery = new Query();
            pcsQuery.addCriteria(criteria);
            pcsQuery.with(Sort.by(Sort.Order.desc("generationDataTime")));
            pcsQuery.limit(1);
            PcsChannelDic channelDic = pcsChannelDicDao.findPcsChannel(pcsQuery);
            if(channelDic != null){
                pcsChannelDicsList.add(channelDic);
            }

        }
        if (!CollectionUtils.isEmpty(pcsChannelDicsList) && !pcsChannelDicsList.isEmpty()) {
            List<PcsStatusVo> pcsStatusVos = pcsChannelDicsList.stream().sorted(Comparator.comparing(PcsChannelDic::getEquipmentId))
                    .map(pcsChannelDic -> {
                        PcsStatusVo pcsStatusVo = new PcsStatusVo();
                        pcsStatusVo.setName(pcsChannelDic.getName());
                        pcsStatusVo.setEquipmentId(pcsChannelDic.getEquipmentId());
                        pcsStatusVo.setActivePower(pcsChannelDic.getActivePower());
                        pcsStatusVo.setChargeDischargeStatusMsg(getChargeDischageStatus(pcsChannelDic.getChargeDischargeStatus()));
                        pcsStatusVo.setReactivePower(pcsChannelDic.getReactivePower());
                        pcsStatusVo.setExchangeVol(getExchangeVol(pcsChannelDic));
                        pcsStatusVo.setExchangeCurrent(getExchangeCurrent(pcsChannelDic));
                        pcsStatusVo.setExchangeRate(getExchangeRate(pcsChannelDic));
                        pcsStatusVo.setPower(keepDecimalTwoPlaces(pcsChannelDic.getDCPower()));
                        pcsStatusVo.setCurrent(keepDecimalTwoPlaces(pcsChannelDic.getDCCurrent()));
                        pcsStatusVo.setVoltage(keepDecimalTwoPlaces(pcsChannelDic.getDCVoltage()));
                        pcsStatusVo.setRunningStatusMsg(pcsChannelDic.getRunningStatus() ? RUNNING_STATUS_MSG_START : RUNNING_STATUS_MSG_DOWN);
                        pcsStatusVo.setSoc(pcsChannelDic.getSoc());
                        return pcsStatusVo;
                    }).collect(Collectors.toList());
            deviceInfoVo.setPcsStatusVoList(pcsStatusVos);
        }
    }

    private void getDeviceIncome(DeviceInfoVo deviceInfoVo) {
        Criteria criteria = new Criteria();
        criteria.and(DEVICE_NAME).is(deviceInfoVo.getDeviceName());
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group().sum("income").as("count"),
                Aggregation.limit(1)
        );
        List<BasicDBObject> basicDBObjects = incomeEntityDao.aggregate(aggregation);
        if(!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()){
            for (DBObject obj : basicDBObjects) {
                String count = obj.get("count").toString();
                deviceInfoVo.setIncome(new BigDecimal(count).setScale(4, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
    }


    private void getDeviceRunStatusMsg(DeviceInfoVo deviceInfoVo) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where(DEVICE_NAME).is(deviceInfoVo.getDeviceName()));
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))),
                Aggregation.limit(1)
        );
        BamsDataDicBA dataDicBA = bamsDataDicBADao.aggregationResult(aggregation);
        Date date = new Date(System.currentTimeMillis());
        Date addDateMinutes = DateUtils.addDateMinutes(dataDicBA.getGenerationDataTime(), 30);
        if (!addDateMinutes.before(date)) {
            deviceInfoVo.setDeviceStatus("正常运行");
        } else {
            deviceInfoVo.setDeviceStatus("停机");
        }
    }

    private String getChargeDischageStatus(Short chargeDischargeStatus) {
        switch (chargeDischargeStatus) {
            case 0:
                return "待机";
            case 1:
                return "充电";
            case 2:
                return "放电";
            default:
                return "未知数据";
        }
    }

    private String getExchangeRate(PcsChannelDic pcsChannelDic) {
        return keepDecimalTwoPlaces(pcsChannelDic.getARate()) + SEPARATOR_OF_MAP +
                keepDecimalTwoPlaces(pcsChannelDic.getBRate()) + SEPARATOR_OF_MAP +
                keepDecimalTwoPlaces(pcsChannelDic.getCRate());
    }

    private String getExchangeCurrent(PcsChannelDic pcsChannelDic) {
        return keepDecimalTwoPlaces(pcsChannelDic.getACurrent()) + SEPARATOR_OF_MAP +
                keepDecimalTwoPlaces(pcsChannelDic.getBCurrent()) + SEPARATOR_OF_MAP +
                keepDecimalTwoPlaces(pcsChannelDic.getCCurrent());
    }

    private String getExchangeVol(PcsChannelDic pcsChannelDic) {
        return keepDecimalTwoPlaces(pcsChannelDic.getAVoltage()) + SEPARATOR_OF_MAP +
                keepDecimalTwoPlaces(pcsChannelDic.getBVoltage()) + SEPARATOR_OF_MAP + keepDecimalTwoPlaces(pcsChannelDic.getCVoltage());
    }

    private String keepDecimalTwoPlaces(String value) {
        return BigDecimal.valueOf(Double.valueOf(value)).setScale(2, BigDecimal.ROUND_DOWN).toString();
    }

    public Map<Integer, List<TimeOfPowerBo>> selectPowerOfTypeTime(Device device) {
        if (device != null) {
            if (!CollectionUtils.isEmpty(device.getTimeOfPower()) && !device.getTimeOfPower().isEmpty()) {
                List<TimeOfPowerBo> timeOfPower = device.getTimeOfPower();
                Map<Integer, List<TimeOfPowerBo>> listMap = timeOfPower.stream().collect(Collectors.groupingBy(
                        TimeOfPowerBo::getRunMode
                ));
                return listMap;
            }
        }
        return null;
    }

    public ResHelper<Void> modifyDevicePowerInfo(ModifyDevicePowerBo modifyDevicePowerBo, List<String> deviceNames) {
        log.info("批量修改设备功率  设备列表为:{}", deviceNames);
        Map<Integer, List<TimeOfPowerBo>> powerOfTime = modifyDevicePowerBo.getPowerOfTime();
        if (!CollectionUtils.isEmpty(powerOfTime) && !powerOfTime.isEmpty()) {

            List<TimeOfPowerBo> timeOfPowerBos = new ArrayList<>();
            for (Map.Entry<Integer, List<TimeOfPowerBo>> entry : powerOfTime.entrySet()) {
                List<TimeOfPowerBo> powerBos = entry.getValue();
                for (TimeOfPowerBo timeOfPowerBo : powerBos) {
                    if (timeOfPowerBo.getRunMode() != entry.getKey()) {
                        return ResHelper.pamIll();
                    }
                    if (timeOfPowerBo.getRunMode() == null || timeOfPowerBo.getType() == null) {
                        return ResHelper.pamIll();
                    }
                    //充电类型 为0  充电类型为1
                    if (timeOfPowerBo.getType() == 0 || timeOfPowerBo.getType() == 1) {
                        if (timeOfPowerBo != null && !StringUtils.isEmpty(timeOfPowerBo.getPower()) && !StringUtils.isEmpty(timeOfPowerBo.getTime())) {
                            String time = timeOfPowerBo.getTime();
                            String[] strs = time.split(",");
                            for (String str : strs) {
                                String[] strings = str.split("-");
                                for (String st : strings) {
                                    if (!st.matches(CHECK_TIME_FORMAT)) {
                                        return ResHelper.pamIll();
                                    }
                                }
                            }
                        } else {
                            return ResHelper.pamIll();
                        }
                    } else {
                        return ResHelper.pamIll();
                    }
                }
                timeOfPowerBos.addAll(entry.getValue());
            }
            for (String deviceName : deviceNames) {
                if (!deviceDao.exists(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)))) {
                    return ResHelper.error("设备不存在");
                }
            }
            Query updateQuery = new Query();
            Update update = new Update();
            updateQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
            if (!CollectionUtils.isEmpty(timeOfPowerBos) && !timeOfPowerBos.isEmpty()) {
                update.set("timeOfPower", timeOfPowerBos);
                deviceDao.updateByQuery(updateQuery, update);
            }
            return ResHelper.success("修改成功");
        }
        return ResHelper.pamIll();
    }

    public Map<Integer, List<TimeOfPowerBo>> selectPowerOfTypeTimeByDeviceName(String deviceName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        Device device = deviceDao.findDevice(query);
        if (!CollectionUtils.isEmpty(device.getTimeOfPower()) && !device.getTimeOfPower().isEmpty()) {
            List<TimeOfPowerBo> timeOfPower = device.getTimeOfPower();
            Map<Integer, List<TimeOfPowerBo>> listMap = timeOfPower.stream().collect(Collectors.groupingBy(
                    TimeOfPowerBo::getRunMode
            ));
            return listMap;
        }
        return null;
    }


    public boolean verificationDeviceNameList(List<String> deviceNameList, SysUser user) {
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
            if (deviceNames.containsAll(deviceNameList)) {
                return true;
            }
        }
        return false;
    }


    public ResHelper<Page<DeviceVo>> queryDevicesByPage(Integer page, Integer pageSize, String deviceName) {
        Query deviceQuery = new Query();
        if (!StringUtils.isEmpty(deviceName)) {
            deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (StringUtils.isEmpty(deviceName)) {
                    deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
                }
            }
        }
        deviceQuery.with(Sort.by(Sort.Order.desc("accessTime")));
        long totalRecord = deviceDao.count(deviceQuery);
        deviceQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        List<Device> deviceList = deviceDao.findBatchDevices(deviceQuery);

        if (!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()) {
            List<DeviceVo> deviceVos = new ArrayList<>();
            for (Device device : deviceList) {
                DeviceVo deviceVo = query(device.getDeviceName());
                deviceVos.add(deviceVo);
            }
            Page<DeviceVo> deviceVoPage = new Page<>(page, pageSize, totalRecord, deviceVos);
            return ResHelper.success("", deviceVoPage);
        }
        return ResHelper.success("");
    }

    public ResHelper<Page<DeviceInfoVo>> queryDeviceWithoutBindByPage(Integer page, Integer pageSize, String deviceName) {
        Query deviceQuery = new Query();
        if (!StringUtils.isEmpty(deviceName)) {
            deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        deviceQuery.addCriteria(Criteria.where("bindStation").is(false));
        deviceQuery.with(Sort.by(Sort.Order.desc("accessTime")));
        long totalRecord = deviceDao.count(deviceQuery);
        deviceQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        List<Device> deviceList = deviceDao.findBatchDevices(deviceQuery);

        List<DeviceInfoVo> deviceInfoVos = new ArrayList<>();
        for (Device device : deviceList) {
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
            BeanUtils.copyProperties(device, deviceInfoVo);
            deviceInfoVo.setAreaCity(device.getProvince() + "-" + device.getCity());
            deviceInfoVo.setApplicationScenariosMsg(PropertyUtil.getApplicationScenariosMsg(device.getApplicationScenarios()));
            deviceInfoVo.setApplicationScenariosItemMsg(PropertyUtil.getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
            deviceInfoVos.add(deviceInfoVo);
        }
        Page<DeviceInfoVo> deviceInfoVoPage = new Page<>(page, pageSize, totalRecord, deviceInfoVos);
        return ResHelper.success("", deviceInfoVoPage);
    }

    public ResHelper<Page<DeviceInfoVo>> deviceAreaList(Integer page, Integer pageSize, String name, String country, String province, String city) {
        Query deviceQuery = new Query();
        if (!StringUtils.isEmpty(province)) {
            deviceQuery.addCriteria(Criteria.where(PROVINCE).is(province));
        }
        if (!StringUtils.isEmpty(city)) {
            deviceQuery.addCriteria(Criteria.where("city").is(city));
        }
        if (!StringUtils.isEmpty(name)) {
            deviceQuery.addCriteria(Criteria.where("name").regex(name));
        }
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
            }
        }
        deviceQuery.with(Sort.by(Sort.Order.desc("accessTime")));
        long totalRecord = deviceDao.count(deviceQuery);
        deviceQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        List<Device> data = deviceDao.findBatchDevices(deviceQuery);

        Page<DeviceInfoVo> deviceInfoVoPage = new Page<>(page, pageSize,
                totalRecord, getResHelperResult(data));
        return ResHelper.success("", deviceInfoVoPage);

    }

    private List<DeviceInfoVo> getResHelperResult(List<Device> datas) {
        List<DeviceInfoVo> deviceInfoVos = new ArrayList<>();
        for (Device data : datas) {
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
            BeanUtils.copyProperties(data, deviceInfoVo);
            deviceInfoVo.setAreaCity(data.getProvince() + "-" + data.getCity());
            deviceInfoVo.setApplicationScenariosMsg(PropertyUtil.getApplicationScenariosMsg(data.getApplicationScenarios()));
            deviceInfoVo.setApplicationScenariosItemMsg(PropertyUtil.getApplicationScenariosItemMsg(data.getApplicationScenariosItem()));
            Criteria criteria = new Criteria();
            criteria.andOperator(Criteria.where(DEVICE_NAME).is(data.getDeviceName()));
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Sort.by(Sort.Order.desc("generationDataTime"))), Aggregation.limit(1));
            BamsDataDicBA dataDicBA = bamsDataDicBADao.aggregationResult(aggregation);
            Date date = new Date();
            Date addDateMinutes = DateUtils.addDateMinutes(dataDicBA.getGenerationDataTime(), 30);
            if (!addDateMinutes.before(date)) {
                deviceInfoVo.setDeviceStatus("正常运行");
            } else {
                deviceInfoVo.setDeviceStatus("停机");
            }
            deviceInfoVos.add(deviceInfoVo);
        }
        return deviceInfoVos;
    }

    public Map<String, Object> allEquipmentData(String accessToken) {
        String corpSecret = SecretUtil.decryptWithBase64(accessToken.replaceAll(" ", "+"), propertiesConfig.getSecretKey()).split("-")[1];
        Corp corp = corpDao.findCorp(new Query().addCriteria(Criteria.where("corpAccessSecret").is(corpSecret)));
        List<Device> deviceList = deviceDao.findBatchDevices(new Query().addCriteria(Criteria.where(DEVICE_NAME).in(corp.getDeviceName())));
        List<JSONObject> resultObject = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for (Device device : deviceList) {
            jsonObject.put("deviceName", device.getDeviceName());
            jsonObject.put("province", device.getProvince());
            jsonObject.put("city", device.getCity());
            jsonObject.put("stationCapacity", device.getStationCapacity());
            jsonObject.put("stationPower", device.getStationPower());
            jsonObject.put("applicationScenariosInfo", PropertyUtil.getApplicationScenariosMsg(device.getApplicationScenarios()));
            jsonObject.put("applicationScenariosItemInfo", PropertyUtil.getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
            jsonObject.put("description", device.getDescription());
            jsonObject.put("name", device.getName());
            jsonObject.put("equipment", getEquipmentJsonObjectList(device, device.getDeviceName()));
        }
        resultObject.add(jsonObject);
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultObject);
        return result;
    }

    private List<JSONObject> getEquipmentJsonObjectList(Device device, String deviceName) {
        List<JSONObject> equipmentJsonObjectList = new ArrayList<>();
        DeviceVo deviceVo = new DeviceVo();
        if (device != null) {
            deviceVo.setDeviceName(device.getDeviceName());
            deviceVo.setName(device.getName());
            deviceVo.setDescription(device.getDescription());
            EquipmentBo equipmentBo = new EquipmentBo();
            equipmentBo.setName(device.getName());
            equipmentBo.setId(device.getId());
            List<EquipmentBo> equipmentBos = new ArrayList<>();
            equipmentBo.setEquipmentBo(equipmentBos);
            Query pccsQuery = new Query();
            pccsQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("stationId").is(device.getStationId()));
            List<Pccs> pccList = pccsDao.findBatchPccs(pccsQuery);
            pccList.stream().forEach(pcc -> {
                Query cubeQuery = new Query();
                cubeQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("pccId").is(pcc.getPccsId()).and("stationId").is(pcc.getStationId()));
                Cube cube = cubeDao.findCube(cubeQuery);
                EquipmentBo equipment = new EquipmentBo();
                equipment.setName(cube.getName());
                equipment.setId(cube.getId());
                List<EquipmentBo> equipmentBoList = new ArrayList<>();
                equipment.setEquipmentBo(equipmentBoList);
                JSONObject equipmentObject = new JSONObject();
                equipmentObject.put("cubeModel", cube.getCubeModel());
                equipmentObject.put("cubePower", cube.getCubePower());
                equipmentObject.put("batteryManufacturer", cube.getBatteryManufacturer());
                equipmentObject.put("cellCapacity", cube.getCellCapacity());
                equipmentObject.put("coolingModeInfo", getCoolingModeInfo(cube.getCoolingMode()));
                equipmentObject.put("description", cube.getDescription());
                equipmentObject.put("name", cube.getName());
                List<JSONObject> equipmentOfBam = new ArrayList<>();
                equipmentObject.put("equipmentOfSbmu", equipmentOfBam);
                List<JSONObject> equipmentOfPcs = new ArrayList<>();
                equipmentObject.put("equipmentOfPcs", equipmentOfPcs);
                equipmentJsonObjectList.add(equipmentObject);
                /**
                 * 并网口后下级目录   电池堆、PCS、其他、电表
                 */
                Query equipmentQuery = new Query();
                equipmentQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName))
                        .addCriteria(Criteria.where("enabled").is(true))
                        .addCriteria(Criteria.where("cubeId").is(cube.getCubeId()));
                List<Equipment> equipments =equipmentDao.findBatchEquipment(equipmentQuery);
                List<Equipment> bamEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 36).collect(Collectors.toList());
                for (Equipment bam : bamEquipments) {
                    JSONObject jsonObjectBam = new JSONObject();
                    jsonObjectBam.put("name", bam.getName());
                    jsonObjectBam.put("description", bam.getDescription());
                    jsonObjectBam.put("manufacturer", bam.getManufacturer());
                    jsonObjectBam.put("model", bam.getModel());
                    jsonObjectBam.put("equipmentId", bam.getEquipmentId());
                    List<JSONObject> jsonObjectBcusList = new ArrayList<>();
                    List<Equipment> clusterEquipment = equipments.stream().
                            filter(equip -> equip.getEquipmentId() > 36 && equip.getEquipmentId() < 53).collect(Collectors.toList());
                    for (Equipment bcuEquipment : clusterEquipment) {
                        JSONObject bcuJsonObject = new JSONObject();
                        bcuJsonObject.put("name", bcuEquipment.getName());
                        bcuJsonObject.put("description", bcuEquipment.getDescription());
                        bcuJsonObject.put("manufacturer", bcuEquipment.getManufacturer());
                        bcuJsonObject.put("model", bcuEquipment.getModel());
                        bcuJsonObject.put("equipmentId", bcuEquipment.getEquipmentId());
                        jsonObjectBcusList.add(bcuJsonObject);
                    }
                    jsonObjectBam.put("equipmentOfCbmu", jsonObjectBcusList);
                    equipmentOfBam.add(jsonObjectBam);
                }
                List<Equipment> pcsCabinetEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 15).collect(Collectors.toList());

                for (Equipment pcsCabinetEquipment : pcsCabinetEquipments) {
                    JSONObject pcsCabinetJsonObject = new JSONObject();
                    pcsCabinetJsonObject.put("name", pcsCabinetEquipment.getName());
                    pcsCabinetJsonObject.put("description", pcsCabinetEquipment.getDescription());
                    pcsCabinetJsonObject.put("manufacturer", pcsCabinetEquipment.getManufacturer());
                    pcsCabinetJsonObject.put("model", pcsCabinetEquipment.getModel());
                    pcsCabinetJsonObject.put("equipmentId", pcsCabinetEquipment.getEquipmentId());
                    List<JSONObject> jsonObjectPcsChannelList = new ArrayList<>();
                    List<Equipment> pcsChannelEquipments = equipments.stream()
                            .filter(equip -> equip.getEquipmentId() > 15 && equip.getEquipmentId() < 32).collect(Collectors.toList());
                    for (Equipment pcsChannelEquipment : pcsChannelEquipments) {
                        JSONObject pcsChannelJSONObject = new JSONObject();
                        pcsChannelJSONObject.put("name", pcsChannelEquipment.getName());
                        pcsChannelJSONObject.put("description", pcsChannelEquipment.getDescription());
                        pcsChannelJSONObject.put("manufacturer", pcsChannelEquipment.getManufacturer());
                        pcsChannelJSONObject.put("model", pcsChannelEquipment.getModel());
                        pcsChannelJSONObject.put("equipmentId", pcsChannelEquipment.getEquipmentId());
                        jsonObjectPcsChannelList.add(pcsChannelJSONObject);
                    }
                    pcsCabinetJsonObject.put("equipmentOfPcsChannel", jsonObjectPcsChannelList);
                    equipmentOfPcs.add(pcsCabinetJsonObject);
                }
                EquipmentBo equipmentBoOfBam = new EquipmentBo();
                /**
                 * 电池堆
                 */
                getBoOfBamsData(equipments, deviceName, equipmentBoOfBam, equipmentBoList);
                /**
                 * PCS
                 */
                getBoOfPcsData(equipments, equipmentBoList);
                equipmentBos.add(equipment);
            });
            deviceVo.setEquipmentBo(equipmentBo);
        }
        return equipmentJsonObjectList;
    }

    private String getCoolingModeInfo(Integer coolingMode) {
        switch (coolingMode) {
            case 0:
                return "风冷";
            case 1:
                return "水冷";
            default:
                return "其他";
        }
    }

    public ResHelper<Void> updateDeviceImg(MultipartFile[] files, String deviceName, List<String> imgFiles) {

        try {
            List<String> deviceImgIdWithUrls = new ArrayList<>();
            List<ImgUpload> imgUploads = new ArrayList<>();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    List<String> fullPath = new ArrayList<>();
                    String filename = files[i].getOriginalFilename();
                    ImgUpload imgUpload = new ImgUpload();
                    imgUpload.setOriginalFilename(filename);
                    imgUpload.setType(ENERGY_STORAGE);
                    Date date = new Date(System.currentTimeMillis());
                    String dateToStr = DateUtils.dateToStr(date);
                    String fileF = filename.substring(filename.lastIndexOf("."), filename.length());
                    String imgUrlStr = System.currentTimeMillis() + "_" + ENERGY_STORAGE + "_" + i + fileF;
                    String deviceNameImgUrlPC = propertiesConfig.getImageGenerationLocationPc() + dateToStr + File.separator + imgUrlStr;
                    File f = new File(deviceNameImgUrlPC);
                    makeSureFileExist(f);
                    files[i].transferTo(f);
                    String deviceNameImgUrlPh = propertiesConfig.getImageGenerationLocationPh() + dateToStr + File.separator + imgUrlStr;
                    File fPh = new File(deviceNameImgUrlPh);
                    makeSureFileExist(fPh);
                    files[i].transferTo(fPh);
                    ObjectId objectId = gridFsTemplate.store(files[i].getInputStream(), files[i].getOriginalFilename(),files[i].getContentType());
                    String deviceImgIdWithUrl = objectId + "~" + File.separator + dateToStr + File.separator + imgUrlStr;
                    imgUpload.setImgId(objectId.toString());
                    fullPath.add(deviceNameImgUrlPC);
                    fullPath.add(deviceNameImgUrlPh);
                    imgUpload.setImgOfFullPath(fullPath);
                    deviceImgIdWithUrls.add(deviceImgIdWithUrl);
                    imgUploads.add(imgUpload);
                }
                if (!CollectionUtils.isEmpty(imgFiles) && !imgFiles.isEmpty()) {
                    imgFiles.addAll(deviceImgIdWithUrls);
                } else {
                    imgFiles = deviceImgIdWithUrls;
                }
            }
            imgUploadsDao.saveImgUploads(imgUploads);
            Query deviceQuery = new Query();
            deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
            Update deviceUpdate= new Update();
            deviceUpdate.set("deviceImgIdWithUrls", imgFiles);
            deviceDao.updateByQuery(deviceQuery,deviceUpdate);
            log.info("修改设备图片成功:deviceName{}", deviceName);
        } catch (Exception e) {
            log.error("修改设备图片失败:{}", e);
            return ResHelper.error("修改设备图片失败");
        }
        return ResHelper.success("修改设备图片成功");
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
