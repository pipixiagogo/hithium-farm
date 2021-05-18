package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.EquipmentBo;
import com.bugull.hithiumfarmweb.http.bo.ModifyDeviceBo;
import com.bugull.hithiumfarmweb.http.bo.TimeOfPriceBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.http.vo.dao.DeviceAreaEntityDao;
import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguAggregation;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.utils.DateUtils.DAY_OF_SECONDS;
import static com.bugull.hithiumfarmweb.utils.DateUtils.HOUR_OF_SECONDS;

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
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private IncomeEntityDao incomeEntityDao;

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

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
            DBObject condition = deviceDao.query().is(PROVINCE, province).getCondition();
            Iterable<DBObject> iterable = deviceDao.aggregate().match(condition).group("{_id:'$city', count:{$sum:1}}").sort("{count:-1}").results();
            List<CityVo> cityVoList = new ArrayList<>();
            if (iterable != null) {
                iterable.forEach(it -> {
                    CityVo cityVo = new CityVo();
                    String city = (String) it.get("_id");
                    Integer num = (Integer) it.get("count");
                    cityVo.setCity(city);
                    cityVo.setNum(num);
                    List<DeviceAreaEntity> deviceAreaEntities = deviceAreaEntityDao.query().is(PROVINCE, province).is("city", city).results();
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
        /**
         *  TODO 设备列表分类 按excel导出那个分类
         */
        Device device = deviceDao.query().is(DEVICE_NAME, deviceName).result();
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
            List<Pccs> pccsList = pccsDao.query().is(DEVICE_NAME, deviceName).is("stationId", device.getStationId()).results();
            pccsList.stream().forEach(pccs -> {
                EquipmentBo equipment = new EquipmentBo();
                equipment.setName(pccs.getName());
                equipment.setId(pccs.getId());
                List<EquipmentBo> equipmentBoList = new ArrayList<>();
                equipment.setEquipmentBo(equipmentBoList);


                /**
                 * 并网口后下级目录   电池堆、PCS、其他、电表
                 */

                List<Equipment> equipments = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).results();
                /**
                 * 电池堆
                 */
                List<Equipment> bamsEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 14).collect(Collectors.toList());
                Equipment bamsEquipment = bamsEquipments.get(0);
                EquipmentBo equipmentBoOfBams = new EquipmentBo();
                BeanUtils.copyProperties(bamsEquipment, equipmentBoOfBams);
                equipmentBoList.add(equipmentBoOfBams);
                List<Equipment> bamsClusterEquipment = equipments.stream().
                        filter(equip -> equip.getEquipmentId() > 35 && equip.getEquipmentId() < 54).collect(Collectors.toList());
                List<EquipmentBo> bos = bamsClusterEquipment.stream().map(clusterEquipment -> {
                    EquipmentBo equipOfBo = new EquipmentBo();
                    BeanUtils.copyProperties(clusterEquipment, equipOfBo);
                    return equipOfBo;
                }).collect(Collectors.toList());
                equipmentBoOfBams.setEquipmentBo(bos);

                /**
                 * PCS
                 */
                List<Equipment> pcsCabinetEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 15).collect(Collectors.toList());
                Equipment pcsCabnet = pcsCabinetEquipments.get(0);
                List<Equipment> pcsChannelEquipments = equipments.stream()
                        .filter(equip -> equip.getEquipmentId() > 15 && equip.getEquipmentId() < 32).collect(Collectors.toList());
                List<EquipmentBo> boList = pcsChannelEquipments.stream().map(pcsChannelEquipment -> {
                    EquipmentBo equip = new EquipmentBo();
                    BeanUtils.copyProperties(pcsChannelEquipment, equip);
                    return equip;
                }).collect(Collectors.toList());
                EquipmentBo pcsCabinetBo = new EquipmentBo();
                BeanUtils.copyProperties(pcsCabnet, pcsCabinetBo);
                pcsCabinetBo.setEquipmentBo(boList);

                equipmentBoList.add(pcsCabinetBo);
                /**
                 * 电表
                 */
                EquipmentBo ammeterBo = new EquipmentBo();
                ammeterBo.setName("电表");
                List<Equipment> ammeterEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 3 || equip.getEquipmentId() == 6 || equip.getEquipmentId() == 11)
                        .collect(Collectors.toList());
                List<EquipmentBo> equipmentBoList1 = ammeterEquipments.stream().map(ammeterEquipment -> {
                    EquipmentBo equip = new EquipmentBo();
                    BeanUtils.copyProperties(ammeterEquipment, equip);
                    return equip;
                }).collect(Collectors.toList());
                ammeterBo.setEquipmentBo(equipmentBoList1);

                equipmentBoList.add(ammeterBo);
                /**
                 * 附属表
                 */
                EquipmentBo subsidiaryBo = new EquipmentBo();
                subsidiaryBo.setName("附属件");
                List<Equipment> subsidiaryEquipments = equipments.stream().
                        filter(equip -> equip.getEquipmentId() == 1 || equip.getEquipmentId() == 12 || equip.getEquipmentId() == 13)
                        .collect(Collectors.toList());
                List<EquipmentBo> subsidiaryEquipBo = subsidiaryEquipments.stream().map(subsidiaryEquipment -> {
                    EquipmentBo equipmentBo1 = new EquipmentBo();
                    BeanUtils.copyProperties(subsidiaryEquipment, equipmentBo1);
                    return equipmentBo1;
                }).collect(Collectors.toList());
                subsidiaryBo.setEquipmentBo(subsidiaryEquipBo);
                equipmentBoList.add(subsidiaryBo);


//                List<Cube> cubes = cubeDao.query().is("deviceName", deviceName).is("stationId", device.getStationId())
//                        .is("pccId", pccs.getPccsId()).results();
//                cubes.stream().forEach(cube -> {
//                    EquipmentBo equipm = new EquipmentBo();
//                    equipm.setName(cube.getName());
//                    equipm.setId(cube.getId());
//                    List<Cabin> cabinList = cabinDao.query().is("deviceName", deviceName)
//                            .is("stationId", device.getStationId())
//                            .is("pccId", pccs.getPccsId()).is("cubeId", cube.getCubeId())
//                            .results();
//                    List<EquipmentBo> boList = new ArrayList<>();
//                    cabinList.stream().forEach(cabin -> {
//                        EquipmentBo mentBo = new EquipmentBo();
//                        mentBo.setName(cabin.getName());
//                        mentBo.setId(cabin.getId());
//                        List<EquipmentBo> bos = new ArrayList<>();
//                        List<Equipment> ment = equipmentDao.query().is("enabled", true).in("_id", cabin.getEquipmentIds())
//                                .returnFields("name", "equipmentId", "_id").results();
//                        ment.stream().forEach(men -> {
//                            EquipmentBo menBo = new EquipmentBo();
//                            menBo.setName(men.getName());
//                            menBo.setId(men.getId());
//                            menBo.setEquipmentBo(null);
//                            menBo.setEquipmentId(men.getEquipmentId());
//                            bos.add(menBo);
//                        });
//                        if (!CollectionUtils.isEmpty(bos) && bos.size() > 0) {
//                            mentBo.setEquipmentBo(bos);
//                        }
//                        boList.add(mentBo);
//                    });
//                    List<Equipment> equip = equipmentDao.query().is("enabled", true).in("_id", cube.getEquipmentIds())
//                            .returnFields("name", "equipmentId", "_id").results();
//                    equip.stream().forEach(equ -> {
//                        EquipmentBo ipmentBo = new EquipmentBo();
//                        ipmentBo.setName(equ.getName());
//                        ipmentBo.setId(equ.getId());
//                        ipmentBo.setEquipmentBo(null);
//                        ipmentBo.setEquipmentId(equ.getEquipmentId());
//                        equipmentBoList.add(ipmentBo);
//                    });
//                    equipm.setEquipmentBo(boList);
//                    if (!CollectionUtils.isEmpty(equipm.getEquipmentBo()) && equipm.getEquipmentBo().size() > 0) {
//                        equipmentBoList.add(equipm);
//                    }
//                });
                equipmentBos.add(equipment);
                List<Equipment> equips = equipmentDao.query().is("enabled", true).in("_id", pccs.getEquipmentIds())
                        .returnFields("name", "equipmentId", "_id").results();
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
        if (propertiesConfig.verify(params)) {
            BuguPageQuery<Device> deviceBuguPageQuery = deviceDao.pageQuery();
            if (!queryOfParams(deviceBuguPageQuery, params)) {
                return ResHelper.pamIll();
            }
            String province = (String) params.get(PROVINCE);
            String city = (String) params.get("city");
            if (!StringUtils.isEmpty(province)) {
                deviceBuguPageQuery.is(PROVINCE, province);
            }
            if (!StringUtils.isEmpty(city)) {
                deviceBuguPageQuery.is("city", city);
            }
            deviceBuguPageQuery.sortDesc("accessTime");
            BuguPageQuery.Page<Device> devicePage = deviceBuguPageQuery.resultsWithPage();
            List<Device> datas = devicePage.getDatas();
            List<DeviceInfoVo> deviceInfoVos = new ArrayList<>();
            for (Device data : datas) {
                DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
                BeanUtils.copyProperties(data, deviceInfoVo);
                deviceInfoVo.setAreaCity(data.getProvince() + "-" + data.getCity());
                deviceInfoVo.setApplicationScenariosMsg(getApplicationScenariosMsg(data.getApplicationScenarios()));
                deviceInfoVo.setApplicationScenariosItemMsg(getApplicationScenariosItemMsg(data.getApplicationScenariosItem()));
                Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is(DEVICE_NAME, data.getDeviceName()))
                        .sort("{generationDataTime:-1}").limit(1).results();
                Date date = new Date();
                if (iterable != null) {
                    for (DBObject dbObject : iterable) {
                        BamsDataDicBA bamsDataDicBA = MapperUtil.fromDBObject(BamsDataDicBA.class, dbObject);
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
        return ResHelper.pamIll();
    }

    private String getApplicationScenariosItemMsg(Integer applicationScenariosItem) {
        switch (applicationScenariosItem) {
            case 1:
                return "防逆流控制";
            case 2:
                return "削峰填谷";
            case 8:
                return "需量控制";
            case 16:
                return "AGC联合调频";
            case 32:
                return "需求响应";
            case 64:
                return "动态扩容";
            case 128:
                return "微网监控管理";
            case 256:
                return "备用电源";
            default:
                return "未知应用场景下策略";
        }
    }

    private String getApplicationScenariosMsg(Integer applicationScenarios) {
        switch (applicationScenarios) {
            case 0:
                return "调峰";
            case 1:
                return "调频";
            case 2:
                return "风光储充";
            case 3:
                return "微网";
            case 4:
                return "备用电源";
            case 5:
                return "台区";
            default:
                return "未知应用场景";
        }
    }

    private boolean queryOfParams(BuguPageQuery<?> query, Map<String, Object> params) {
        String name = (String) params.get("name");
        if (!StringUtils.isEmpty(name)) {
            query.regexCaseInsensitive("name", name);
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
        if (!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()) {
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

    public ResHelper<Void> modifyDeviceInfo(ModifyDeviceBo modifyDeviceBo) {
        if (modifyDeviceBo != null) {
            List<TimeOfPriceBo> priceOfTime = modifyDeviceBo.getPriceOfTime();
            if (!StringUtils.isEmpty(modifyDeviceBo.getDeviceName()) && !priceOfTime.isEmpty()) {
                /**
                 * 格式校验map
                 */
                BuguQuery<Device> query = deviceDao.query().is(DEVICE_NAME, modifyDeviceBo.getDeviceName());
                if (!query.exists()) {
                    return ResHelper.error("设备不存在");
                }
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
                        Integer endTimeintHour = Integer.valueOf(endTime.split(":")[0]);
                        Integer startTimeintHour = Integer.valueOf(startTime.split(":")[0]);
                        Integer endTimeintMin = Integer.valueOf(endTime.split(":")[1]);
                        Integer startTimeintMin = Integer.valueOf(startTime.split(":")[1]);
                        if (endTimeintHour > startTimeintHour) {
                            sum += (endTimeintHour - startTimeintHour) * HOUR_OF_SECONDS + (endTimeintMin - startTimeintMin) * 60;
                        } else {
                            endTimeintHour = endTimeintHour + 24;
                            sum += (endTimeintHour - startTimeintHour) * HOUR_OF_SECONDS + (endTimeintMin - startTimeintMin) * 60;
                        }
                    }
                    if (num != sum) {
                        return ResHelper.pamIll();
                    }
                } catch (Exception e) {
                    return ResHelper.pamIll();
                }
                deviceDao.update().set("priceOfTime", priceOfTime).execute(query);
                return ResHelper.success("修改成功");
            }
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
                List<PriceOfPercenVo> priceOfPercenVoList = new ArrayList<>();
                List<String> outOfDay = new ArrayList<>();
                for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                    List<String> entryValue = entry.getValue();
                    for (String value : entryValue) {
                        createPriceOfPercenVo(value, entry.getKey(), priceOfPercenVoList, outOfDay);
                    }
                }
                if (!CollectionUtils.isEmpty(outOfDay) && !outOfDay.isEmpty()) {
                    for (String day : outOfDay) {
                        String[] typeStr = day.split("_");
                        createPriceOfPercenVo(typeStr[1], Integer.valueOf(typeStr[0]), priceOfPercenVoList, outOfDay);
                    }
                }
                return priceOfPercenVoList;
            }
        }
        return new ArrayList<>();
    }

    private void createPriceOfPercenVo(String value, Integer type, List<PriceOfPercenVo> priceOfPercenVoList, List<String> outOfDay) {
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
            } else {
                String timeOfFirst = split[0] + END_TIME;
                String timeOfLast = START_TIME + split[1];
                outOfDay.add(type + "_" + timeOfFirst);
                outOfDay.add(type + "_" + timeOfLast);
            }
        } catch (ParseException e) {
            log.error("解析日期出错", e);
        }
    }


    public Map<String, Object> getPriceOfPercenAndTime(String deviceName) {
        Map<String, Object> result = new HashMap<>();
        Device device = deviceDao.query().is(DEVICE_NAME, deviceName).returnFields("priceOfTime").result();
        result.put("PERCENT", selectPriceOfPercentage(device));
        result.put("PRICE", selectPriceOfTime(device));
        return result;
    }

    public List<TimeOfPriceBo> selectPriceOfTimeBydevice(String deviceName) {
        Device device = deviceDao.query().is(DEVICE_NAME, deviceName).returnFields("priceOfTime").result();
        return selectPriceOfTime(device);
    }

    public DeviceInfoVo queryDeviceName(String deviceName) {
        Device device = deviceDao.query().is(DEVICE_NAME, deviceName).result();
        if (device != null) {
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
            BeanUtils.copyProperties(device, deviceInfoVo);
            deviceInfoVo.setAreaCity(device.getProvince() + "-" + device.getCity());
            deviceInfoVo.setApplicationScenariosMsg(getApplicationScenariosMsg(device.getApplicationScenarios()));
            deviceInfoVo.setApplicationScenariosItemMsg(getApplicationScenariosItemMsg(device.getApplicationScenariosItem()));
            Iterable <DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityDao.query().is("deviceName", deviceInfoVo.getDeviceName()))
                    .group("{_id:null,count:{$sum:{$toDouble:'$income'}}}").limit(1).results();
            if(iterable != null){
                for(DBObject object:iterable){
                    Double count = (Double) object.get("count");
                    BigDecimal incomeBigDecimal=BigDecimal.valueOf(count).setScale(2,BigDecimal.ROUND_HALF_UP);
                    deviceInfoVo.setIncome(incomeBigDecimal.toString());
                }
            }
            return deviceInfoVo;
        }
        return null;
    }
}
