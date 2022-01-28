package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.Corp;
import com.bugull.hithiumfarmweb.http.vo.BcuDataVolTemVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.hithiumfarmweb.utils.SecretUtil;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;

@Service
public class RealTimeDataService {


    public static final Logger log = LoggerFactory.getLogger(RealTimeDataService.class);
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
    private PcsChannelDicDao pcsChannelDicDao;
    @Resource
    private AirConditionDataDicDao airConditionDataDicDao;
    @Resource
    private TemperatureMeterDataDicDao temperatureMeterDataDicDao;
    @Resource
    private FireControlDataDicDao fireControlDataDicDao;
    @Resource
    private UpsPowerDataDicDao upsPowerDataDicDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private EssStationService essStationService;
    @Resource
    private CorpDao corpDao;


    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(String deviceName) {
        Criteria criteria = new Criteria();
        criteria.and(DEVICE_NAME).is(deviceName);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("equipmentId").first("equipmentId").as("equipmentId"));
        List<BasicDBObject> aggregationResults = temperatureMeterDataDicDao.aggregation(aggregation);
        if (!CollectionUtils.isEmpty(aggregationResults) && !aggregationResults.isEmpty()) {
            List<TemperatureMeterDataDic> temperatureMeterList = new ArrayList<>();
            for (BasicDBObject aggregationResult : aggregationResults) {
                Query findQuery = new Query();
                Integer equipmentId = (Integer) aggregationResult.get("equipmentId");
                Criteria criteriaTemperature=new Criteria();
                criteriaTemperature.and(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId);
                findQuery.addCriteria(criteriaTemperature);
                findQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                TemperatureMeterDataDic temperatureMeterDataDic = temperatureMeterDataDicDao.findTemperatureMeterData(findQuery);
                temperatureMeterList.add(temperatureMeterDataDic);
            }
            List<BmsTempMsgBo> tempMsgBos = temperatureMeterList.stream().map(temperatureMeterDataDic -> {
                BmsTempMsgBo msgBo = new BmsTempMsgBo();
                BeanUtils.copyProperties(temperatureMeterDataDic, msgBo);
                return msgBo;
            }).collect(Collectors.toList());
            return ResHelper.success("", tempMsgBos);
        }
        return ResHelper.success("");
    }


    public ResHelper<BamsLatestBo> bamLatestData(String deviceName) {
        Query bamQuery = new Query();
        bamQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        bamQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
        BamsDataDicBA bamsDataDicBA = bamsDataDicBADao.findBamData(bamQuery);
        BamsLatestBo bamsLatestBo = new BamsLatestBo();
        BeanUtils.copyProperties(bamsDataDicBA, bamsLatestBo);
        bamsLatestBo.setRunningStateMsg(getStateMsg(bamsDataDicBA.getRunningState()));
        return ResHelper.success("", bamsLatestBo);
    }

    private String getStateMsg(Integer runningState) {
        switch (runningState) {
            case -1:
                return "断开";
            case 0:
                return "充满";
            case 1:
                return "放空";
            case 2:
                return "告警";
            case 3:
                return "故障";
            case 4:
                return "正常";
            case 5:
                return "预警";
            case 6:
                return "跳机";
            case 7:
                return "待机";
            default:
                return "未知状态";
        }
    }

    public ResHelper<List<BcuDataBo>> bcuDataQueryByName(String deviceName) {
        Criteria criteria = new Criteria();
        criteria.and(DEVICE_NAME).is(deviceName);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("equipmentId").first("equipmentId").as("equipmentId"));
        List<BasicDBObject> basicDBObjects = bcuDataDicBCUDao.aggregation(aggregation);
        if (!CollectionUtils.isEmpty(basicDBObjects) && !basicDBObjects.isEmpty()) {
            List<BcuDataDicBCU> bcuDataDicBCUS = new ArrayList<>();
            for (BasicDBObject basicDBObject : basicDBObjects) {
                Query findBatchQuery = new Query();
                Integer equipmentId = (Integer) basicDBObject.get("equipmentId");
                criteria.and(EQUIPMENT_ID).is(equipmentId);
                findBatchQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                BcuDataDicBCU bcuDataDicBCU = bcuDataDicBCUDao.findBcuData(findBatchQuery);
                bcuDataDicBCUS.add(bcuDataDicBCU);
            }
            List<BcuDataBo> bcuDataBoList = bcuDataDicBCUS.stream().map(bcuDataDicBCU -> {
                BcuDataBo bcuDataBo = new BcuDataBo();
                BeanUtils.copyProperties(bcuDataDicBCU, bcuDataBo);
                bcuDataBo.setGroupRunStatusMsg(getStateMsg(bcuDataDicBCU.getGroupRunStatus()));
                bcuDataBo.setGroupChargeDischargeStatusMsg(getDischargeStatusMsg(bcuDataDicBCU.getGroupChargeDischargeStatus()));
                return bcuDataBo;
            }).collect(Collectors.toList());

            return ResHelper.success("", bcuDataBoList);
        }
        return ResHelper.success("");
    }

    private String getDischargeStatusMsg(Integer groupChargeDischargeStatus) {
        switch (groupChargeDischargeStatus) {
            case 0:
                return "未定义";
            case 1:
                return "开路";
            case 2:
                return "搁置";
            case 3:
                return "充电";
            case 4:
                return "放电";
            default:
                return "未知数据";
        }
    }

    public ResHelper<List<BmsCellDataBo>> cellDataQuery(String deviceName) {
        Criteria criteriaTemp = new Criteria();
        criteriaTemp.and(DEVICE_NAME).is(deviceName);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteriaTemp),
                Aggregation.group("equipmentId").first("equipmentId").as("equipmentId"));
        List<BasicDBObject> aggregationTempResults = bmsCellTempDataDicDao.aggregation(aggregation);
        List<BasicDBObject> aggregationVolResults = bmsCellVoltDataDicDao.aggregation(aggregation);
        List<BmsCellTempDataDic> bmsCellTempData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(aggregationTempResults) && !aggregationTempResults.isEmpty()) {

            for (BasicDBObject aggregationResult : aggregationTempResults) {
                Criteria criteriaEquipmentId=new Criteria();
                Query bmsTempQuery = new Query();
                Integer equipmentId = (Integer) aggregationResult.get("equipmentId");
                criteriaEquipmentId.and(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId);
                bmsTempQuery.addCriteria(criteriaEquipmentId);
                bmsTempQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                BmsCellTempDataDic bmsCellTempDataDic = bmsCellTempDataDicDao.findBmsCellTemp(bmsTempQuery);
                bmsCellTempData.add(bmsCellTempDataDic);
            }
        }
        List<BmsCellVoltDataDic> bmsCellVolData = new ArrayList<>();

        if (!CollectionUtils.isEmpty(aggregationVolResults) && !aggregationVolResults.isEmpty()) {
            for (BasicDBObject aggregationResult : aggregationVolResults) {
                Criteria criteriaVol = new Criteria();
                Query bmsVolQuery = new Query();
                Integer equipmentId = (Integer) aggregationResult.get("equipmentId");
                criteriaVol.and(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId);
                bmsVolQuery.addCriteria(criteriaVol);
                bmsVolQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                BmsCellVoltDataDic bmsCellVoltDataDic = bmsCellVoltDataDicDao.findBmsCellVol(bmsVolQuery);
                bmsCellVolData.add(bmsCellVoltDataDic);
            }
        }
        List<BmsCellDataBo> bmsCellDataBos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempData) && !bmsCellTempData.isEmpty() &&
                !CollectionUtils.isEmpty(bmsCellVolData) && !bmsCellVolData.isEmpty()) {
            for (BmsCellTempDataDic bmsCellTempDataDic : bmsCellTempData) {
                for (BmsCellVoltDataDic bmsCellVoltDataDic : bmsCellVolData) {
                    if (bmsCellTempDataDic.getEquipmentId() == bmsCellVoltDataDic.getEquipmentId()) {
                        BmsCellDataBo bmsCellDataBo = new BmsCellDataBo();
                        bmsCellDataBo.setName(bmsCellTempDataDic.getName());
                        bmsCellDataBo.setDeviceName(bmsCellTempDataDic.getDeviceName());
                        bmsCellDataBo.setTempMap(bmsCellTempDataDic.getTempMap());
                        bmsCellDataBo.setVolMap(bmsCellVoltDataDic.getVolMap());
                        bmsCellDataBo.setEquipmentId(bmsCellTempDataDic.getEquipmentId());
                        bmsCellDataBo.setGenerationDataTime(bmsCellTempDataDic.getGenerationDataTime());
                        bmsCellDataBos.add(bmsCellDataBo);
                    }
                }
            }
        }
        return ResHelper.success("", bmsCellDataBos);
    }

    public ResHelper<Object> stationInformationQuery(String deviceName, Integer equipmentId, Integer userType) {

        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            if (equipmentId == 2) {
                return ResHelper.success("", fireControlDataDicDao.findFireControlQuery(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 4 || equipmentId == 7 || equipmentId == 12) {
                return ResHelper.success("",ammeterDataDicDao.findAmmeterQuery(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 13) {
                return ResHelper.success("", upsPowerDataDicDao.findUpsPower(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 14 || equipmentId == 55) {
                return ResHelper.success("", airConditionDataDicDao.findAirCondition(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 36) {
                return ResHelper.success("", bamsDataDicBADao.findBamData(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 32 || equipmentId == 34 || equipmentId == 53) {
                return ResHelper.success("",  temperatureMeterDataDicDao.findTemperatureMeterData(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId > 36 && equipmentId < 53) {
                /**
                 * 电池簇信息  bcudata、bmscelltemp、bmscellvol
                 */
                return ResHelper.success("", bcuDataVolTemp(deviceName, equipmentId));
            }
        } else {
            if (equipmentId == 1) {
                return ResHelper.success("", fireControlDataDicDao.findFireControlQuery(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 3 || equipmentId == 6 || equipmentId == 11) {
                return ResHelper.success("", ammeterDataDicDao.findAmmeterQuery(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 12) {
                return ResHelper.success("", upsPowerDataDicDao.findUpsPower(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 13) {
                return ResHelper.success("", airConditionDataDicDao.findAirCondition(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 14) {
                return ResHelper.success("",  bamsDataDicBADao.findBamData(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId == 32 || equipmentId == 34 || equipmentId == 44) {
                return ResHelper.success("",temperatureMeterDataDicDao.findTemperatureMeterData(getQuery(deviceName,equipmentId,userType)));
            }
            if (equipmentId > 35 && equipmentId < 54) {
                /**
                 * 电池簇信息  bcudata、bmscelltemp、bmscellvol
                 */
                return ResHelper.success("", bcuDataVolTemp(deviceName, equipmentId));
            }
        }
        if (equipmentId == 15) {
            return ResHelper.success("", pcsChannelDicDao.findPcsChannel(getQuery(deviceName,equipmentId,userType)));
        }
        if (equipmentId > 15 && equipmentId < 32) {
            return ResHelper.success("", pcsChannelDicDao.findPcsChannel(getQuery(deviceName,equipmentId,userType)));
        }
        return ResHelper.success("");
    }

    private Query getQuery(String deviceName, Integer equipmentId, Integer userType) {
        Query fireControlQuery = new Query();
        fireControlQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        if (userType == 2) {
            fireControlQuery.with(Sort.by(Sort.Order.asc("generationDataTime"))).limit(1);
        } else {
            fireControlQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
        }
        return fireControlQuery;
    }

    private BcuDataVolTemVo bcuDataVolTemp(String deviceName, Integer equipmentId) {
        Query bcuDataQuery = new Query();
        bcuDataQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        bcuDataQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
        BcuDataDicBCU bcuDataDicBCU = bcuDataDicBCUDao.findBcuData(bcuDataQuery);
        BcuDataVolTemVo bcuDataVolTemVo = new BcuDataVolTemVo();
        BeanUtils.copyProperties(bcuDataDicBCU, bcuDataVolTemVo);
        if (bcuDataVolTemVo == null) {
            return null;
        }
        Query bmsCellTempQuery = new Query();
        bmsCellTempQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        bmsCellTempQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
        BmsCellTempDataDic bmsCellTempDataDic = bmsCellTempDataDicDao.findBmsCellTemp(bmsCellTempQuery);
        Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
        Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            if (next.getValue() == 0) {
                iterator.remove();
            }
        }
        bcuDataVolTemVo.setTempMap(tempMap);
        Query bmsCellVolQuery = new Query();
        bmsCellVolQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        bmsCellVolQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
        BmsCellVoltDataDic bmsCellVoltDataDic = bmsCellVoltDataDicDao.findBmsCellVol(bmsCellVolQuery);
        Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
        Iterator<Map.Entry<String, Integer>> iteratorVol = volMap.entrySet().iterator();
        while (iteratorVol.hasNext()) {
            Map.Entry<String, Integer> next = iteratorVol.next();
            if (next.getValue() == 0) {
                iteratorVol.remove();
            }
        }
        bcuDataVolTemVo.setVolMap(volMap);
        return bcuDataVolTemVo;
    }


    public boolean verificationDeviceName(String deviceName, SysUser sysUser) {
        List<EssStation> essStationList = essStationService.getEssStationList(sysUser.getStationList());
        if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
            for (EssStation essStation : essStationList) {
                if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                    if (essStation.getDeviceNameList().contains(deviceName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ResHelper<Page<AmmeterDataDic>> ammeterDataQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query ammeterQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            ammeterQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            ammeterQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        ammeterQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = ammeterDataDicDao.count(ammeterQuery);
        ammeterQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                ammeterQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                ammeterQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            ammeterQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<AmmeterDataDic> ammeterDataList = ammeterDataDicDao.findBatchAmmeter(ammeterQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, ammeterDataList));
    }

    public ResHelper<Page<BamsDataDicBA>> bamDataQuery(Integer page, Integer pageSize, String deviceName, String orderField, Integer order, Integer equipmentId, SysUser user) {
        Query bamDataQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            bamDataQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            bamDataQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        bamDataQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = bamsDataDicBADao.count(bamDataQuery);
        bamDataQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                bamDataQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                bamDataQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            bamDataQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<BamsDataDicBA> bamDataDicList = bamsDataDicBADao.findBatchBamData(bamDataQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, bamDataDicList));
    }

    public ResHelper<Page<BmsCellTempDataDic>> bmsTempDataQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, Date startTime, Date endTime, SysUser user) {
        Query bmsCellTempQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            bmsCellTempQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            bmsCellTempQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        if (startTime != null && endTime != null) {
            bmsCellTempQuery.addCriteria(Criteria.where(GENERATION_DATA_TIME).gte(startTime).lte(endTime));
        }
        bmsCellTempQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = bmsCellTempDataDicDao.count(bmsCellTempQuery);
        bmsCellTempQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                bmsCellTempQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                bmsCellTempQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            bmsCellTempQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<BmsCellTempDataDic> bmsCellTempDataList = bmsCellTempDataDicDao.findBatchBmsCellTemp(bmsCellTempQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, bmsCellTempDataList));
    }

    public ResHelper<Page<BmsCellVoltDataDic>> bmsVoltDataQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query bmsCellVoltQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            bmsCellVoltQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            bmsCellVoltQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        bmsCellVoltQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = bmsCellVoltDataDicDao.count(bmsCellVoltQuery);
        bmsCellVoltQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                bmsCellVoltQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                bmsCellVoltQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            bmsCellVoltQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<BmsCellVoltDataDic> bmsCellVoltDataList = bmsCellVoltDataDicDao.findBatchQuery(bmsCellVoltQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, bmsCellVoltDataList));
    }

    public ResHelper<Page<BcuDataDicBCU>> bcuDataQuery(Integer page, Integer pageSize, String deviceName, String orderField, Integer order, Integer equipmentId, SysUser user) {
        Query bcuDataQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            bcuDataQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            bcuDataQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        bcuDataQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = bcuDataDicBCUDao.count(bcuDataQuery);
        bcuDataQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                bcuDataQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                bcuDataQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            bcuDataQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<BcuDataDicBCU> bcuDataDicBCUS = bcuDataDicBCUDao.findBatchBcuData(bcuDataQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, bcuDataDicBCUS));
    }

    public ResHelper<Page<PcsCabinetDic>> pcsCabinetQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query pcsCabinetQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            pcsCabinetQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            pcsCabinetQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        long totalRecord = pcsCabinetDicDao.count(pcsCabinetQuery);
        pcsCabinetQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                pcsCabinetQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                pcsCabinetQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            pcsCabinetQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<PcsCabinetDic> pcsCabinetList = pcsCabinetDicDao.findBatchPcsCabinet(pcsCabinetQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, pcsCabinetList));
    }

    public ResHelper<Page<PcsChannelDic>> pcsChannelQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query pcsChannelQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            pcsChannelQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            pcsChannelQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        pcsChannelQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = pcsChannelDicDao.count(pcsChannelQuery);
        pcsChannelQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                pcsChannelQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                pcsChannelQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            pcsChannelQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<PcsChannelDic> pcsChannelDicList = pcsChannelDicDao.findBatchPcsChannel(pcsChannelQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, pcsChannelDicList));
    }

    public ResHelper<Page<AirConditionDataDic>> airConditionQuery(Integer page, Integer pageSize, String deviceName, String orderField, Integer order, Integer equipmentId, SysUser user) {
        Query airConditionQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            airConditionQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            airConditionQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        airConditionQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = airConditionDataDicDao.count(airConditionQuery);
        airConditionQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                airConditionQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                airConditionQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            airConditionQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<AirConditionDataDic> airConditionDataList = airConditionDataDicDao.findBatchAirCondition(airConditionQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, airConditionDataList));
    }

    public ResHelper<Page<TemperatureMeterDataDic>> temperatureMeterQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query temperatureQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            temperatureQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            temperatureQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        temperatureQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = temperatureMeterDataDicDao.count(temperatureQuery);
        temperatureQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                temperatureQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                temperatureQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            temperatureQuery.with(Sort.by(Sort.Order.desc("generationDataTime")));
        }
        List<TemperatureMeterDataDic> temperatureMeterDataList = temperatureMeterDataDicDao.findBatchTemperature(temperatureQuery);

        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, temperatureMeterDataList));
    }

    public ResHelper<Page<FireControlDataDic>> fireControlQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query fireControlQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, new ArrayList<>()));
                }
            }
            fireControlQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            fireControlQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        fireControlQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = fireControlDataDicDao.count(fireControlQuery);
        fireControlQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                fireControlQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                fireControlQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            fireControlQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<FireControlDataDic> fireControlDataList = fireControlDataDicDao.findBatchFireControl(fireControlQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, fireControlDataList));
    }

    public ResHelper<Page<UpsPowerDataDic>> upsPowerQuery(Integer page, Integer pageSize, String deviceName, Integer order, String orderField, Integer equipmentId, SysUser user) {
        Query upsPowerQuery = new Query();
        List<String> deviceNames = essStationService.getDeviceNames(user.getStationList());
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0L, new ArrayList<>()));
                }
            }
            upsPowerQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty() && StringUtils.isEmpty(deviceName)) {
            upsPowerQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        upsPowerQuery.addCriteria(Criteria.where(EQUIPMENT_ID).is(equipmentId));
        long totalRecord = upsPowerDataDicDao.count(upsPowerQuery);
        upsPowerQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!StringUtils.isEmpty(orderField)) {
            if (DESC.equals(order)) {
                upsPowerQuery.with(Sort.by(Sort.Order.desc(orderField)));
            } else {
                upsPowerQuery.with(Sort.by(Sort.Order.asc(orderField)));
            }
        } else {
            upsPowerQuery.with(Sort.by(Sort.Order.desc("_id")));
        }
        List<UpsPowerDataDic> upsPowerDataList = upsPowerDataDicDao.findBatchUpsPower(upsPowerQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, upsPowerDataList));
    }

    public Map<String, Object> equipmentRealTimeData(String accessToken) {
        String corpSecret = SecretUtil.decryptWithBase64(accessToken.replaceAll(" ", "+"), propertiesConfig.getSecretKey()).split("-")[1];
        Query corpQuery = new Query();
        corpQuery.addCriteria(Criteria.where("corpAccessSecret").is(corpSecret));
        Corp corp = corpDao.findCorp(corpQuery);
        List<JSONObject> resultObject = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();

        for (String deviceName : corp.getDeviceName()) {
            List<BamsDataDicBA> bamDataDicBAS = new ArrayList<>();
            Query bamQuery = new Query();
            bamQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("equipmentId").is(36));
            bamQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
            BamsDataDicBA bamsDataDicBA = bamsDataDicBADao.findBamData(bamQuery);
            bamDataDicBAS.add(bamsDataDicBA);

            List<BcuDataDicBCU> bcuDataDicBCUS = new ArrayList<>();
            for (int i = 37; i < 45; i++) {
                Query bcuQuery = new Query();
                bcuQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("equipmentId").is(i));
                bcuQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                BcuDataDicBCU bcuDataDicBCU = bcuDataDicBCUDao.findBcuData(bcuQuery);
                bcuDataDicBCUS.add(bcuDataDicBCU);
            }
            List<PcsCabinetDic> pcsCabinets = new ArrayList<>();
            Query pcsCabinetQuery = new Query();
            pcsCabinetQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("equipmentId").is(15));
            pcsCabinetQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
            PcsCabinetDic pcsCabinetDic = pcsCabinetDicDao.findPcsCabinet(pcsCabinetQuery);
            pcsCabinets.add(pcsCabinetDic);
            List<PcsChannelDic> pcsChannels = new ArrayList<>();
            for (int i = 16; i < 24; i++) {
                Query pcsChannelQuery = new Query();
                pcsChannelQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("equipmentId").is(i));
                pcsChannelQuery.with(Sort.by(Sort.Order.desc("generationDataTime"))).limit(1);
                PcsChannelDic pcsChannelDic = pcsChannelDicDao.findPcsChannel(pcsChannelQuery);
                pcsChannels.add(pcsChannelDic);
            }
            jsonObject.put("deviceName", deviceName);
            jsonObject.put("pcsChannelData", getJsonOfPcsChannel(pcsChannels));
            jsonObject.put("pcsCabinetData", getJsonOfPcsCabinet(pcsCabinets));
            jsonObject.put("cbmuData", getJsonOfBcuData(bcuDataDicBCUS));
            jsonObject.put("sbmuData", getJsonOfBamData(bamDataDicBAS));

            resultObject.add(jsonObject);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultObject);
        return result;
    }

    private List<JSONObject> getJsonOfPcsChannel(List<PcsChannelDic> pcsChannels) {
        List<JSONObject> jsonOfPcsData = new ArrayList<>();

        for (PcsChannelDic pcsChannelDic : pcsChannels) {
            JSONObject channelJsonObject = new JSONObject();
            channelJsonObject.put("generationDataTime", pcsChannelDic.getGenerationDataTime().getTime());
            channelJsonObject.put("equipmentId", pcsChannelDic.getEquipmentId());
            channelJsonObject.put("equipChannelStatus", pcsChannelDic.getEquipChannelStatus());
            channelJsonObject.put("powerOnOff", pcsChannelDic.getPowerOnOff());


            channelJsonObject.put("maxChargeCurrent", pcsChannelDic.getMaxChargeCurrent());
            channelJsonObject.put("maxDischargeCurrent", pcsChannelDic.getMaxDischargeCurrent());

            channelJsonObject.put("voltageProtectHighLimit", pcsChannelDic.getVoltageProtectHighLimit());
            channelJsonObject.put("voltageProtectLowLimit", pcsChannelDic.getVoltageProtectLowLimit());

            channelJsonObject.put("modelEnable", pcsChannelDic.getModelEnable());

            channelJsonObject.put("activePower", pcsChannelDic.getActivePower());
            channelJsonObject.put("reactivePower", pcsChannelDic.getReactivePower());

            channelJsonObject.put("chargeDischargeStatus", pcsChannelDic.getChargeDischargeStatus());
            channelJsonObject.put("disableCharge", pcsChannelDic.getDisableCharge());
            channelJsonObject.put("disableDischarge", pcsChannelDic.getDisableDischarge());

            channelJsonObject.put("runningStatus", pcsChannelDic.getRunningStatus());

            channelJsonObject.put("inEnvHighTemp", pcsChannelDic.getInEnvHighTemp());
            channelJsonObject.put("bmsCommFault", pcsChannelDic.getBmsCommFault());
            channelJsonObject.put("dcReverse", pcsChannelDic.getDcReverse());
            channelJsonObject.put("dcOverCurrent", pcsChannelDic.getDcOverCurrent());
            channelJsonObject.put("overLoadAlarm", pcsChannelDic.getOverLoadAlarm());
            channelJsonObject.put("dcOverVoltage", pcsChannelDic.getDcOverVoltage());
            channelJsonObject.put("dcUnderVoltage", pcsChannelDic.getDcUnderVoltage());

            channelJsonObject.put("dcInputOverVolt", pcsChannelDic.getDcInputOverVolt());
            channelJsonObject.put("elecVoltException", pcsChannelDic.getElecVoltException());
            channelJsonObject.put("acOverCurrProtect", pcsChannelDic.getAcOverCurrProtect());
            channelJsonObject.put("powerGridReverse", pcsChannelDic.getPowerGridReverse());
            channelJsonObject.put("outputOverLoadTime", pcsChannelDic.getOutputOverLoadTime());
            channelJsonObject.put("envTempDerating", pcsChannelDic.getEnvTempDerating());

            channelJsonObject.put("dCPower", pcsChannelDic.getDCPower());
            channelJsonObject.put("dCVoltage", pcsChannelDic.getDCVoltage());
            channelJsonObject.put("dCCurrent", pcsChannelDic.getDCCurrent());


            channelJsonObject.put("bmsWorkStatus", pcsChannelDic.getBmsWorkStatus());

            channelJsonObject.put("bClusterVol", pcsChannelDic.getBClusterVol());
            channelJsonObject.put("bClusterCur", pcsChannelDic.getBClusterCur());
            channelJsonObject.put("groupRechargeableQuantity", pcsChannelDic.getGroupRechargeableQuantity());
            channelJsonObject.put("soc", pcsChannelDic.getSoc());
            channelJsonObject.put("groupRedischargeableQuantity", pcsChannelDic.getGroupRedischargeableQuantity());
            channelJsonObject.put("minVoltage", pcsChannelDic.getMinVoltage());
            channelJsonObject.put("maxVoltage", pcsChannelDic.getMaxVoltage());
            channelJsonObject.put("maxSoc", pcsChannelDic.getMaxSoc());
            channelJsonObject.put("minSoc", pcsChannelDic.getMinSoc());
            channelJsonObject.put("minTemp", pcsChannelDic.getMinTemp());
            channelJsonObject.put("maxTemp", pcsChannelDic.getMaxTemp());

            channelJsonObject.put("aVoltage", pcsChannelDic.getAVoltage());
            channelJsonObject.put("bVoltage", pcsChannelDic.getBVoltage());
            channelJsonObject.put("cVoltage", pcsChannelDic.getCVoltage());
            channelJsonObject.put("aBVoltage", pcsChannelDic.getABVoltage());
            channelJsonObject.put("bCVoltage", pcsChannelDic.getBCVoltage());
            channelJsonObject.put("cAVoltage", pcsChannelDic.getCAVoltage());
            channelJsonObject.put("aRate", pcsChannelDic.getARate());
            channelJsonObject.put("bRate", pcsChannelDic.getBRate());
            channelJsonObject.put("cRate", pcsChannelDic.getCRate());
            channelJsonObject.put("aFactor", pcsChannelDic.getAFactor());
            channelJsonObject.put("bFactor", pcsChannelDic.getBFactor());
            channelJsonObject.put("cFactor", pcsChannelDic.getCFactor());
            channelJsonObject.put("aCurrent", pcsChannelDic.getACurrent());
            channelJsonObject.put("bCurrent", pcsChannelDic.getBCurrent());
            channelJsonObject.put("cCurrent", pcsChannelDic.getCCurrent());
            channelJsonObject.put("aPower", pcsChannelDic.getAPower());
            channelJsonObject.put("bPower", pcsChannelDic.getBPower());
            channelJsonObject.put("cPower", pcsChannelDic.getCPower());

            channelJsonObject.put("aReactivePower", pcsChannelDic.getAReactivePower());
            channelJsonObject.put("bReactivePower", pcsChannelDic.getBReactivePower());
            channelJsonObject.put("cReactivePower", pcsChannelDic.getCReactivePower());
            channelJsonObject.put("aApparentPower", pcsChannelDic.getAApparentPower());
            channelJsonObject.put("bApparentPower", pcsChannelDic.getBApparentPower());
            channelJsonObject.put("cApparentPower", pcsChannelDic.getCApparentPower());

            channelJsonObject.put("sumPower", pcsChannelDic.getSumPower());
            channelJsonObject.put("sumRelativePower", pcsChannelDic.getSumRelativePower());
            channelJsonObject.put("sumApparentPower", pcsChannelDic.getSumApparentPower());
            channelJsonObject.put("sumPowerFactor", pcsChannelDic.getSumPowerFactor());

            channelJsonObject.put("maxChargePower", pcsChannelDic.getMaxChargePower());
            channelJsonObject.put("maxDischargePower", pcsChannelDic.getMaxDischargePower());
            jsonOfPcsData.add(channelJsonObject);
        }
        return jsonOfPcsData;
    }

    private List<JSONObject> getJsonOfPcsCabinet(List<PcsCabinetDic> pcsCabinets) {
        List<JSONObject> jsonOfPcsData = new ArrayList<>();
        for (PcsCabinetDic pcsCabinetDic : pcsCabinets) {
            JSONObject pcsJsonObject = new JSONObject();
            pcsJsonObject.put("generationDataTime", pcsCabinetDic.getGenerationDataTime().getTime());
            pcsJsonObject.put("equipmentId", pcsCabinetDic.getEquipmentId());
            pcsJsonObject.put("equipChannelStatus", pcsCabinetDic.getEquipChannelStatus());

            pcsJsonObject.put("activePower", pcsCabinetDic.getActivePower());
            pcsJsonObject.put("reactivePower", pcsCabinetDic.getReactivePower());
            pcsJsonObject.put("isOffGrid", pcsCabinetDic.getIsOffGrid());
            pcsJsonObject.put("chargeDischargeStatus", pcsCabinetDic.getChargeDischargeStatus());
            pcsJsonObject.put("aBVol", pcsCabinetDic.getABVol());
            pcsJsonObject.put("bCVol", pcsCabinetDic.getBCVol());
            pcsJsonObject.put("cAVol", pcsCabinetDic.getCAVol());
            pcsJsonObject.put("aVol", pcsCabinetDic.getAVol());
            pcsJsonObject.put("bVol", pcsCabinetDic.getBVol());
            pcsJsonObject.put("cVol", pcsCabinetDic.getCVol());
            pcsJsonObject.put("aRate", pcsCabinetDic.getARate());
            pcsJsonObject.put("bRate", pcsCabinetDic.getBRate());
            pcsJsonObject.put("cRate", pcsCabinetDic.getCRate());
            pcsJsonObject.put("aPF", pcsCabinetDic.getAPF());
            pcsJsonObject.put("bPF", pcsCabinetDic.getBPF());
            pcsJsonObject.put("cPF", pcsCabinetDic.getCPF());
            pcsJsonObject.put("aCurrent", pcsCabinetDic.getACurrent());
            pcsJsonObject.put("bCurrent", pcsCabinetDic.getBCurrent());
            pcsJsonObject.put("cCurrent", pcsCabinetDic.getCCurrent());
            pcsJsonObject.put("aPower", pcsCabinetDic.getAPower());
            pcsJsonObject.put("bPower", pcsCabinetDic.getBPower());
            pcsJsonObject.put("cPower", pcsCabinetDic.getCPower());
            pcsJsonObject.put("aReactivePower", pcsCabinetDic.getAReactivePower());
            pcsJsonObject.put("bReactivePower", pcsCabinetDic.getBReactivePower());
            pcsJsonObject.put("cReactivePower", pcsCabinetDic.getCReactivePower());

            pcsJsonObject.put("sumPower", pcsCabinetDic.getSumPower());
            pcsJsonObject.put("sumReactivePower", pcsCabinetDic.getSumReactivePower());
            pcsJsonObject.put("sumApparentPower", pcsCabinetDic.getSumApparentPower());
            pcsJsonObject.put("sumPowerFactor", pcsCabinetDic.getSumPowerFactor());
            pcsJsonObject.put("sumCharge", pcsCabinetDic.getSumCharge());
            pcsJsonObject.put("sumDischarge", pcsCabinetDic.getSumDischarge());
            pcsJsonObject.put("daySumCharge", pcsCabinetDic.getDaySumCharge());
            pcsJsonObject.put("daySumDischarge", pcsCabinetDic.getDaySumDischarge());
            pcsJsonObject.put("maxChargePower", pcsCabinetDic.getMaxChargePower());
            pcsJsonObject.put("maxDischargePower", pcsCabinetDic.getMaxDischargePower());
            pcsJsonObject.put("desiredPower", pcsCabinetDic.getDesiredPower());
            pcsJsonObject.put("desiredReactivePower", pcsCabinetDic.getDesiredReactivePower());
            jsonOfPcsData.add(pcsJsonObject);
        }
        return jsonOfPcsData;
    }

    private List<JSONObject> getJsonOfBcuData(List<BcuDataDicBCU> bcuDataDicBCUS) {
        List<JSONObject> jsonOfBcuData = new ArrayList<>();

        for (BcuDataDicBCU bcuDataDicBCU : bcuDataDicBCUS) {
            JSONObject bcuJsonObject = new JSONObject();
            bcuJsonObject.put("generationDataTime", bcuDataDicBCU.getGenerationDataTime().getTime());
            bcuJsonObject.put("equipmentId", bcuDataDicBCU.getEquipmentId());
            bcuJsonObject.put("equipChannelStatus", bcuDataDicBCU.getEquipChannelStatus());

            bcuJsonObject.put("groupRunStatus", bcuDataDicBCU.getGroupRunStatus());

            bcuJsonObject.put("groupChargeDischargeStatus", bcuDataDicBCU.getGroupChargeDischargeStatus());

            bcuJsonObject.put("groupTemperatureCount", bcuDataDicBCU.getGroupTemperatureCount());
            bcuJsonObject.put("groupBatteryCount", bcuDataDicBCU.getGroupBatteryCount());
            bcuJsonObject.put("groupPackCount", bcuDataDicBCU.getGroupPackCount());
            bcuJsonObject.put("maxIndexVoltage", bcuDataDicBCU.getMaxIndexVoltage());
            bcuJsonObject.put("minIndexVoltage", bcuDataDicBCU.getMinIndexVoltage());
            bcuJsonObject.put("maxIndexTemperature", bcuDataDicBCU.getMaxIndexTemperature());
            bcuJsonObject.put("minIndexTemperature", bcuDataDicBCU.getMinIndexTemperature());
            bcuJsonObject.put("maxIndexSoc", bcuDataDicBCU.getMaxIndexSoc());
            bcuJsonObject.put("minIndexSoc", bcuDataDicBCU.getMinIndexSoc());
            bcuJsonObject.put("maxIndexSoh", bcuDataDicBCU.getMaxIndexSoh());
            bcuJsonObject.put("minIndexSoh", bcuDataDicBCU.getMinIndexSoh());

            bcuJsonObject.put("groupVoltage", bcuDataDicBCU.getGroupVoltage());
            bcuJsonObject.put("groupCurrent", bcuDataDicBCU.getGroupCurrent());
            bcuJsonObject.put("groupSoc", bcuDataDicBCU.getGroupSoc());
            bcuJsonObject.put("groupSoh", bcuDataDicBCU.getGroupSoh());
            bcuJsonObject.put("envTemperature", bcuDataDicBCU.getEnvTemperature());
            bcuJsonObject.put("groupRechargeableQuantity", bcuDataDicBCU.getGroupRechargeableQuantity());
            bcuJsonObject.put("groupRedischargeableQuantity", bcuDataDicBCU.getGroupRedischargeableQuantity());
            bcuJsonObject.put("groupSingleChargeQuantity", bcuDataDicBCU.getGroupSingleChargeQuantity());
            bcuJsonObject.put("groupSingleDischargeQuantity", bcuDataDicBCU.getGroupSingleDischargeQuantity());
            bcuJsonObject.put("groupAccuChargeQuantity", bcuDataDicBCU.getGroupAccuChargeQuantity());
            bcuJsonObject.put("groupAccuDischargeQuantity", bcuDataDicBCU.getGroupAccuDischargeQuantity());
            bcuJsonObject.put("avgSingleVoltage", bcuDataDicBCU.getAvgSingleVoltage());
            bcuJsonObject.put("maxSingleVoltage", bcuDataDicBCU.getMaxSingleVoltage());
            bcuJsonObject.put("minSingleVoltage", bcuDataDicBCU.getMinSingleVoltage());
            bcuJsonObject.put("cellVoltageDiff", bcuDataDicBCU.getCellVoltageDiff());

            bcuJsonObject.put("avgSingleTemperature", bcuDataDicBCU.getAvgSingleTemperature());
            bcuJsonObject.put("maxSingleTemperature", bcuDataDicBCU.getMaxSingleTemperature());
            bcuJsonObject.put("minSingleTemperature", bcuDataDicBCU.getMinSingleTemperature());
            bcuJsonObject.put("cellTemperatureDiff", bcuDataDicBCU.getCellTemperatureDiff());
            bcuJsonObject.put("avgSingleSoc", bcuDataDicBCU.getAvgSingleSoc());
            bcuJsonObject.put("maxSingleSoc", bcuDataDicBCU.getMaxSingleSoc());
            bcuJsonObject.put("minSingleSoc", bcuDataDicBCU.getMinSingleSoc());
            bcuJsonObject.put("avgSingleSoh", bcuDataDicBCU.getAvgSingleSoh());
            bcuJsonObject.put("maxSingleSoh", bcuDataDicBCU.getMaxSingleSoh());
            bcuJsonObject.put("minSingleSoh", bcuDataDicBCU.getMinSingleSoh());
            bcuJsonObject.put("maxGroupAllowChargePower", bcuDataDicBCU.getMaxGroupAllowChargePower());
            bcuJsonObject.put("maxGroupAllowDischargePower", bcuDataDicBCU.getMaxGroupAllowDischargePower());
            bcuJsonObject.put("maxGroupAllowChargeCurrent", bcuDataDicBCU.getMaxGroupAllowChargeCurrent());
            bcuJsonObject.put("maxGroupAllowDischargeCurrent", bcuDataDicBCU.getMaxGroupAllowDischargeCurrent());
            jsonOfBcuData.add(bcuJsonObject);
        }
        return jsonOfBcuData;
    }

    private List<JSONObject> getJsonOfBamData(List<BamsDataDicBA> bamDataDicBAS) {
        List<JSONObject> jsonOfBamData = new ArrayList<>();
        for (BamsDataDicBA bamsDataDicBA : bamDataDicBAS) {
            JSONObject bamJSONObject = new JSONObject();
            bamJSONObject.put("generationDataTime", bamsDataDicBA.getGenerationDataTime().getTime());
            bamJSONObject.put("equipmentId", bamsDataDicBA.getEquipmentId());
            bamJSONObject.put("equipChannelStatus", bamsDataDicBA.getEquipChannelStatus());
            //运行状态
            bamJSONObject.put("runningState", bamsDataDicBA.getRunningState());
            bamJSONObject.put("maxSocBcuIdx", bamsDataDicBA.getMaxSocBcuIdx());
            bamJSONObject.put("minSocBcuIdx", bamsDataDicBA.getMinSocBcuIdx());

            bamJSONObject.put("maxVolBcuIdx", bamsDataDicBA.getMaxVolBcuIdx());
            bamJSONObject.put("minVolBcuIdx", bamsDataDicBA.getMinVolBcuIdx());

            bamJSONObject.put("maxCellVolBcuIdx", bamsDataDicBA.getMaxCellVolBcuIdx());
            bamJSONObject.put("maxCellVolCellIdx", bamsDataDicBA.getMaxCellVolCellIdx());

            bamJSONObject.put("minCellVolBcuIdx", bamsDataDicBA.getMinCellVolBcuIdx());
            bamJSONObject.put("minCellVolCellIdx", bamsDataDicBA.getMinCellVolCellIdx());

            bamJSONObject.put("maxCellTempBcuIdx", bamsDataDicBA.getMaxCellTempBcuIdx());
            bamJSONObject.put("maxCellTempCellIdx", bamsDataDicBA.getMaxCellTempCellIdx());
            bamJSONObject.put("minCellTempBcuIdx", bamsDataDicBA.getMinCellTempBcuIdx());
            bamJSONObject.put("minCellTempCellIdx", bamsDataDicBA.getMinCellTempCellIdx());


            bamJSONObject.put("voltage", bamsDataDicBA.getVoltage());
            bamJSONObject.put("current", bamsDataDicBA.getCurrent());
            bamJSONObject.put("soc", bamsDataDicBA.getSoc());
            bamJSONObject.put("soh", bamsDataDicBA.getSoh());

            bamJSONObject.put("chargeCapacity", bamsDataDicBA.getChargeCapacity());
            bamJSONObject.put("dischargeCapacity", bamsDataDicBA.getDischargeCapacity());
            bamJSONObject.put("curChargeCapacity", bamsDataDicBA.getCurChargeCapacity());
            bamJSONObject.put("curDischargeCapacity", bamsDataDicBA.getCurDischargeCapacity());
            bamJSONObject.put("chargeCapacitySum", bamsDataDicBA.getChargeCapacitySum());
            bamJSONObject.put("dischargeCapacitySum", bamsDataDicBA.getDischargeCapacitySum());
            bamJSONObject.put("cellVolDiff", bamsDataDicBA.getCellVolDiff());
            bamJSONObject.put("maxCellVol", bamsDataDicBA.getMaxCellVol());
            bamJSONObject.put("minCellVol", bamsDataDicBA.getMinCellVol());
            bamJSONObject.put("cellTempDiff", bamsDataDicBA.getCellTempDiff());
            bamJSONObject.put("maxCellTemp", bamsDataDicBA.getMaxCellTemp());
            bamJSONObject.put("minCellTemp", bamsDataDicBA.getMinCellTemp());
            bamJSONObject.put("bcuSocDiff", bamsDataDicBA.getBcuSocDiff());
            bamJSONObject.put("maxBcuSoc", bamsDataDicBA.getMaxBcuSoc());
            bamJSONObject.put("minBcuSoc", bamsDataDicBA.getMinBcuSoc());
            bamJSONObject.put("bcuVolDiff", bamsDataDicBA.getBcuVolDiff());
            bamJSONObject.put("maxBcuVol", bamsDataDicBA.getMaxBcuVol());
            bamJSONObject.put("minBcuVol", bamsDataDicBA.getMinBcuVol());
            bamJSONObject.put("allowedMaxChargePower", bamsDataDicBA.getAllowedMaxChargePower());
            bamJSONObject.put("allowedMaxDischargePower", bamsDataDicBA.getAllowedMaxDischargePower());
            bamJSONObject.put("allowedMaxChargeCur", bamsDataDicBA.getAllowedMaxChargeCur());
            bamJSONObject.put("allowedMaxDischargeCur", bamsDataDicBA.getAllowedMaxDischargeCur());
            jsonOfBamData.add(bamJSONObject);
        }
        return jsonOfBamData;
    }
}
