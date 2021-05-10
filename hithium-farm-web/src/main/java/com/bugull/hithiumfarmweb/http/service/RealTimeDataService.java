package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.excelBo.AirConditionExcelBo;
import com.bugull.hithiumfarmweb.http.vo.BcuDataVolTemVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.BAMS_DATA_SHEET_NAME;
import static com.bugull.hithiumfarmweb.common.Const.PCS_DATA_SHEET_NAME;
import static com.mongodb.client.model.Aggregates.count;
import static com.mongodb.client.model.Aggregates.lookup;

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
    private EquipmentDao equipmentDao;

    public static final List<Integer> AMMETER_EQUIPMENT_IDS = new ArrayList<>();
    public static final List<Integer> FIRE_AIR_UPS_EQUIPMENT_IDS = new ArrayList<>();

    static {
        AMMETER_EQUIPMENT_IDS.add(3);
        AMMETER_EQUIPMENT_IDS.add(6);
        AMMETER_EQUIPMENT_IDS.add(11);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(1);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(12);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(13);

    }

    public ResHelper<BuguPageQuery.Page<AmmeterDataDic>> ammeterDataQuery(Map<String, Object> params) {
        BuguPageQuery<AmmeterDataDic> query = (BuguPageQuery<AmmeterDataDic>) ammeterDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<AmmeterDataDic> ammeterDataDicPage = query.resultsWithPage();
        return ResHelper.success("", ammeterDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BamsDataDicBA>> bamsDataquery(Map<String, Object> params) {
        BuguPageQuery<BamsDataDicBA> query = (BuguPageQuery<BamsDataDicBA>) bamsDataDicBADao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BamsDataDicBA> bamsDataDicBAPage = query.resultsWithPage();
        return ResHelper.success("", bamsDataDicBAPage);
    }

    private boolean queryOfParams(BuguPageQuery<?> query, Map<String, Object> params) {
        String deviceName = (String) params.get("deviceName");
        if (!StringUtils.isEmpty(deviceName)) {
            query.is("deviceName", deviceName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return false;
        if (!PagetLimitUtil.orderField(query, params)) return false;
        return true;
    }

    public ResHelper<BuguPageQuery.Page<BmsCellTempDataDic>> bmsTempDataquery(Map<String, Object> params) {
        BuguPageQuery<BmsCellTempDataDic> query = (BuguPageQuery<BmsCellTempDataDic>) bmsCellTempDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BmsCellTempDataDic> bmsCellTempDataDicPage = query.resultsWithPage();
        return ResHelper.success("", bmsCellTempDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BmsCellVoltDataDic>> bmsVoltDataquery(Map<String, Object> params) {
        BuguPageQuery<BmsCellVoltDataDic> query = (BuguPageQuery<BmsCellVoltDataDic>) bmsCellVoltDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BmsCellVoltDataDic> bmsCellVoltDataDicPage = query.resultsWithPage();
        return ResHelper.success("", bmsCellVoltDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BcuDataDicBCU>> bcuDataquery(Map<String, Object> params) {
        BuguPageQuery<BcuDataDicBCU> query = (BuguPageQuery<BcuDataDicBCU>) bcuDataDicBCUDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BcuDataDicBCU> bcuDataDicBCUPage = query.resultsWithPage();
        return ResHelper.success("", bcuDataDicBCUPage);
    }

    public ResHelper<BuguPageQuery.Page<PcsCabinetDic>> pcsCabinetquery(Map<String, Object> params) {
        BuguPageQuery<PcsCabinetDic> query = (BuguPageQuery<PcsCabinetDic>) pcsCabinetDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<PcsCabinetDic> pcsCabinetDicPage = query.resultsWithPage();
        return ResHelper.success("", pcsCabinetDicPage);
    }

    public ResHelper<BuguPageQuery.Page<PcsChannelDic>> pcsChannelquery(Map<String, Object> params) {
        BuguPageQuery<PcsChannelDic> query = (BuguPageQuery<PcsChannelDic>) pcsChannelDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<PcsChannelDic> pcsChannelDicPage = query.resultsWithPage();
        return ResHelper.success("", pcsChannelDicPage);
    }

    public ResHelper<BuguPageQuery.Page<AirConditionDataDic>> airConditionquery(Map<String, Object> params) {
        BuguPageQuery<AirConditionDataDic> query = (BuguPageQuery<AirConditionDataDic>) airConditionDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<AirConditionDataDic> airConditionDataDicPage = query.resultsWithPage();
        return ResHelper.success("", airConditionDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<TemperatureMeterDataDic>> temperatureMeterquery(Map<String, Object> params) {
        BuguPageQuery<TemperatureMeterDataDic> query = (BuguPageQuery<TemperatureMeterDataDic>) temperatureMeterDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<TemperatureMeterDataDic> temperatureMeterDataDicPage = query.resultsWithPage();
        return ResHelper.success("", temperatureMeterDataDicPage);
    }


    public ResHelper<BuguPageQuery.Page<FireControlDataDic>> fileControlquery(Map<String, Object> params) {
        BuguPageQuery<FireControlDataDic> query = (BuguPageQuery<FireControlDataDic>) fireControlDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<FireControlDataDic> fireControlDataDicPage = query.resultsWithPage();
        return ResHelper.success("", fireControlDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<UpsPowerDataDic>> upsPowerquery(Map<String, Object> params) {
        BuguPageQuery<UpsPowerDataDic> query = (BuguPageQuery<UpsPowerDataDic>) upsPowerDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<UpsPowerDataDic> upsPowerDataDicPage = query.resultsWithPage();
        return ResHelper.success("", upsPowerDataDicPage);
    }

    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(String deviceName) {
        Iterable<DBObject> iterable = temperatureMeterDataDicDao.aggregate().match(temperatureMeterDataDicDao.query().is("deviceName", deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(3).results();
        List<String> queryIds = new ArrayList<>();
        for (DBObject object : iterable) {
            ObjectId dataId = (ObjectId) object.get("dataId");
            queryIds.add(dataId.toString());
        }
        if (!CollectionUtils.isEmpty(queryIds) && queryIds.size() > 0) {
            List<TemperatureMeterDataDic> temperatureMeterDataDics = temperatureMeterDataDicDao.query().in("_id", queryIds).results();
            List<BmsTempMsgBo> tempMsgBos = temperatureMeterDataDics.stream().map(temperatureMeterDataDic -> {
                BmsTempMsgBo msgBo = new BmsTempMsgBo();
                BeanUtils.copyProperties(temperatureMeterDataDic, msgBo);
                return msgBo;
            }).collect(Collectors.toList());
            return ResHelper.success("", tempMsgBos);
        }
        return ResHelper.success("");
    }


    public ResHelper<BamsLatestBo> bamsLatestData(String deviceName) {
        Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is("deviceName", deviceName))
                .sort("{generationDataTime:-1}").limit(1).results();
        if (iterable != null) {
            for (DBObject object : iterable) {
                BamsDataDicBA bamsDataDicBA = MapperUtil.fromDBObject(BamsDataDicBA.class, object);
                BamsLatestBo bamsLatestBo = new BamsLatestBo();
                BeanUtils.copyProperties(bamsDataDicBA, bamsLatestBo);
                bamsLatestBo.setRunningStateMsg(getStateMsg(bamsDataDicBA.getRunningState()));
                return ResHelper.success("", bamsLatestBo);
            }
        }
        return ResHelper.success("");
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

    public ResHelper<List<BcuDataBo>> bcuDataqueryByName(String deviceName) {
        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(16).results();
        List<String> queryIds = new ArrayList<>();
        if (iterable != null) {
            for (DBObject object : iterable) {
                ObjectId dataId = (ObjectId) object.get("dataId");
                String queryId = dataId.toString();
                queryIds.add(queryId);
            }
        }
        if (!CollectionUtils.isEmpty(queryIds) && queryIds.size() > 0) {
            List<BcuDataDicBCU> bcuDataDicBCUs = bcuDataDicBCUDao.query().in("_id", queryIds).results();
            List<BcuDataBo> bcuDataBoList = bcuDataDicBCUs.stream().map(bcuDataDicBCU -> {
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

    public ResHelper<List<BmsCellDataBo>> bmscellDataQuery(String deviceName) {
        Iterable<DBObject> tempIterable = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is("deviceName", deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(16).results();
        Iterable<DBObject> volIterable = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is("deviceName", deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(16).results();
        List<String> tempIterableIds = new ArrayList<>();
        List<String> volIterableIds = new ArrayList<>();
        if (tempIterable != null) {
            for (DBObject object : tempIterable) {
                ObjectId dataId = (ObjectId) object.get("dataId");
                tempIterableIds.add(dataId.toString());
            }
        }
        if (volIterable != null) {
            for (DBObject object : volIterable) {
                ObjectId dataId = (ObjectId) object.get("dataId");
                volIterableIds.add(dataId.toString());
            }
        }
        List<BmsCellTempDataDic> bmsCellTempDataDicbyIds = bmsCellTempDataDicDao.query().in("_id", tempIterableIds).results();
        List<BmsCellVoltDataDic> bmsCellVoltDataDicbyIds = bmsCellVoltDataDicDao.query().in("_id", volIterableIds).results();
        List<BmsCellDataBo> bmsCellDataBos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempDataDicbyIds) && bmsCellTempDataDicbyIds.size() > 0 &&
                !CollectionUtils.isEmpty(bmsCellVoltDataDicbyIds) && bmsCellVoltDataDicbyIds.size() > 0) {
            for (BmsCellTempDataDic bmsCellTempDataDic : bmsCellTempDataDicbyIds) {
                for (BmsCellVoltDataDic bmsCellVoltDataDic : bmsCellVoltDataDicbyIds) {
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

    public ResHelper<Object> stationInformationQuery(String deviceName, Integer equipmentId) {
        if (StringUtils.isEmpty(deviceName) || equipmentId == null) {
            return ResHelper.pamIll();
        }
        if (equipmentId == 1) {
            return ResHelper.success("", stationInfoQuery(fireControlDataDicDao, deviceName, equipmentId));
        }
        if (equipmentId == 3 || equipmentId == 6 || equipmentId == 11) {
            return ResHelper.success("", stationInfoQuery(ammeterDataDicDao, deviceName, equipmentId));
        }
        if (equipmentId == 12) {
            return ResHelper.success("", stationInfoQuery(upsPowerDataDicDao, deviceName, equipmentId));
        }
        if (equipmentId == 13) {
            return ResHelper.success("", stationInfoQuery(airConditionDataDicDao, deviceName, equipmentId));
        }
        if (equipmentId == 14) {
            return ResHelper.success("", stationInfoQuery(bamsDataDicBADao, deviceName, equipmentId));
        }
        if (equipmentId == 15) {
            return ResHelper.success("", stationInfoQuery(pcsCabinetDicDao, deviceName, equipmentId));
        }
        if (equipmentId > 15 && equipmentId < 32) {
            return ResHelper.success("", stationInfoQuery(pcsChannelDicDao, deviceName, equipmentId));
        }
        if (equipmentId == 32 || equipmentId == 34 || equipmentId == 44) {
            return ResHelper.success("", stationInfoQuery(temperatureMeterDataDicDao, deviceName, equipmentId));
        }
        if (equipmentId > 35 && equipmentId < 54) {
            /**
             * 电池簇信息  bcudata、bmscelltemp、bmscellvol
             */
            return ResHelper.success("", bcuDataVolTemp(deviceName, equipmentId));
        }
        return ResHelper.success("");
    }

    private BcuDataVolTemVo bcuDataVolTemp(String deviceName, Integer equipmentId) {
        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", deviceName)
                .is("equipmentId", equipmentId)).sort("{generationDataTime:-1}").limit(1).results();
        BcuDataVolTemVo bcuDataVolTemVo = null;
        if (iterable != null) {
            for (DBObject object : iterable) {
                bcuDataVolTemVo = new BcuDataVolTemVo();
                BcuDataDicBCU bcuDataDicBCU = MapperUtil.fromDBObject(BcuDataDicBCU.class, object);
                BeanUtils.copyProperties(bcuDataDicBCU, bcuDataVolTemVo);
            }
        }
        if (bcuDataVolTemVo == null) {
            return null;
        }
        Iterable<DBObject> cellTempIte = bmsCellTempDataDicDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", deviceName)
                .is("equipmentId", equipmentId))
                .sort("{generationDataTime:-1}").limit(1).results();
        if (cellTempIte != null) {
            for (DBObject object : cellTempIte) {
                BmsCellTempDataDic bmsCellTempDataDic = MapperUtil.fromDBObject(BmsCellTempDataDic.class, object);
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                bcuDataVolTemVo.setTempMap(tempMap);
            }
        }

        Iterable<DBObject> cellVolIte = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentId))
                .sort("{generationDataTime:-1}").limit(1).results();
        if (cellVolIte != null) {
            for (DBObject object : cellVolIte) {
                BmsCellVoltDataDic bmsCellVoltDataDic = MapperUtil.fromDBObject(BmsCellVoltDataDic.class, object);
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                bcuDataVolTemVo.setVolMap(volMap);
            }
        }
        return bcuDataVolTemVo;
    }

    public Object stationInfoQuery(BuguPageDao buguPageDao, String deviceName, Integer equipmentId) {
        Iterable<DBObject> iterable = buguPageDao.aggregate().match(buguPageDao.query().is("deviceName", deviceName)
                .is("equipmentId", equipmentId)).sort("{generationDataTime:-1}").limit(1).results();
        if (iterable != null) {
            for (DBObject object : iterable) {
                return MapperUtil.fromDBObject(buguPageDao.getEntityClass(), object);
            }
        }
        return null;
    }

    public List<BamsDataDicBA> getDataBams(String deviceName, Date startTime, Date endTime) {
        List<BamsDataDicBA> bamsDataDicBAS = bamsDataDicBADao.query().is("deviceName", deviceName)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        return bamsDataDicBAS;
    }

    public List<AirConditionExcelBo> getAirConditionData(String deviceName, Date startTime, Date endTime) {
        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is("deviceName", deviceName)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        List<AirConditionExcelBo> airConditionExcelBos = airConditionDataDics.stream().map(airConditionDataDic -> {
            AirConditionExcelBo airConditionExcelBo = new AirConditionExcelBo();
            BeanUtils.copyProperties(airConditionDataDic, airConditionExcelBo);
            //TODO 直接这样类型转换
            airConditionExcelBo.setSensorFaultMsg(airConditionDataDic.getSensorFault() == 1 ? "故障" : "正常");
            airConditionExcelBo.setArefactionOnhumidity(airConditionDataDic.getArefactionOnhumidity());
            airConditionExcelBo.setArefactionOffhumidity(airConditionDataDic.getArefactionOffhumidity());
            return airConditionExcelBo;
        }).collect(Collectors.toList());
        return airConditionExcelBos;
    }

    public List<List<String>> getTempHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellTempIte = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is("deviceName", deviceName)
                .is("equipmentId", equipmentId)).sort("{generationDataTime:-1}").limit(1).results();
        BmsCellTempDataDic bmsCellTempDataDic = null;
        if (cellTempIte != null) {
            for (DBObject object : cellTempIte) {
                bmsCellTempDataDic = MapperUtil.fromDBObject(BmsCellTempDataDic.class, object);
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
            }
        }
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        head0.add("设备名称");
        List<String> head1 = new ArrayList<>();
        head1.add("时间");
        List<String> head2 = new ArrayList<>();
        head2.add("前置机通道状态");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        if (bmsCellTempDataDic != null) {
            for (Map.Entry<String, Integer> entry : bmsCellTempDataDic.getTempMap().entrySet()) {
                List<String> head3 = new ArrayList<>();
                head3.add(entry.getKey());
                list.add(head3);
            }
        }
        return list;
    }


    public List<List<Object>> getTempData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellTempDataDic> bmsCellTempDataDics = bmsCellTempDataDicDao.query().
                is("deviceName", deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempDataDics) && bmsCellTempDataDics.size() > 0) {
            for (BmsCellTempDataDic bmsCellTempDataDic : bmsCellTempDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellTempDataDic.getName());
                data.add(bmsCellTempDataDic.getGenerationDataTime());
                data.add(bmsCellTempDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                    data.add(entry.getValue());
                }
                list.add(data);
            }
        }

        return list;
    }

    public void exportStationOfBattery(ExcelWriter writer, String deviceName, String time, Integer type) throws ParseException {
        Date startTime = null;
        Date endTime = null;
        if (StringUtils.isEmpty(time)) {
            Date date = new Date();
            startTime = DateUtils.getStartTime(date);
            endTime = DateUtils.getEndTime(date);
        } else {
            //根据传入的数值获取
            startTime = DateUtils.dateToStrWithHHmm(time);
            endTime = DateUtils.getEndTime(startTime);
        }
        if (type == 0) {
            /**
             * TODO 具体excel字段转换
             */
            exportPcsData(writer, deviceName, startTime, endTime);
        } else if (type == 1) {
            /**
             * TODO 具体excel字段转换
             */
            exportBamsData(writer, deviceName, startTime, endTime);
        } else if (type == 2) {
            /**
             * TODO 具体excel字段转换
             */
            exportAmmeterData(writer, deviceName, startTime, endTime);
        } else {
            /**
             * TODO 空调消防 UPS
             */
            exportUpsAirFireData(writer, deviceName, startTime, endTime);
        }
    }

    private void exportUpsAirFireData(ExcelWriter writer, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).is("enabled", true).in("equipmentId", FIRE_AIR_UPS_EQUIPMENT_IDS)
                .results();
        if (!CollectionUtils.isEmpty(equipmentList) && equipmentList.size() > 0) {
            for (int i = 0; i < equipmentList.size(); i++) {
                if (equipmentList.get(i).getEquipmentId() == 1) {
                    exportFireControllerData(writer, deviceName, equipmentList, startTime, endTime, i);
                } else if (equipmentList.get(i).getEquipmentId() == 12) {
                    exportUpspowerData(writer, deviceName, equipmentList, startTime, endTime, i);
                } else if (equipmentList.get(i).getEquipmentId() == 13) {
                    exportAirconditionData(writer, deviceName, equipmentList, startTime, endTime, i);
                }
            }
        }
    }

    private void exportAirconditionData(ExcelWriter writer, String deviceName, List<Equipment> equipmentList, Date startTime, Date endTime, int sheetNo) {
        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentList.get(sheetNo).getEquipmentId())
                .greaterThanEquals("generationDataTime", startTime).lessThanEquals("generationDataTime", endTime).results();
        if (!CollectionUtils.isEmpty(airConditionDataDics) && airConditionDataDics.size() > 0) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, equipmentList.get(sheetNo).getName()).head(AirConditionDataDic.class).build();
            writer.write(airConditionDataDics, writeSheet);
        }
    }

    private void exportUpspowerData(ExcelWriter writer, String deviceName, List<Equipment> equipmentList, Date startTime, Date endTime, int sheetNo) {
        List<UpsPowerDataDic> upsPowerDataDics = upsPowerDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentList.get(sheetNo).getEquipmentId())
                .greaterThanEquals("generationDataTime", startTime).lessThanEquals("generationDataTime", endTime).results();
        if (!CollectionUtils.isEmpty(upsPowerDataDics) && upsPowerDataDics.size() > 0) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, equipmentList.get(sheetNo).getName()).head(UpsPowerDataDic.class).build();
            writer.write(upsPowerDataDics, writeSheet);
        }
    }

    private void exportFireControllerData(ExcelWriter writer, String deviceName, List<Equipment> equipmentList, Date startTime, Date endTime, int sheetNo) {
        List<FireControlDataDic> fireControlDataDics = fireControlDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentList.get(sheetNo).getEquipmentId())
                .greaterThanEquals("generationDataTime", startTime).lessThanEquals("generationDataTime", endTime).results();
        if (!CollectionUtils.isEmpty(fireControlDataDics) && fireControlDataDics.size() > 0) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, equipmentList.get(sheetNo).getName()).head(FireControlDataDic.class).build();
            writer.write(fireControlDataDics, writeSheet);
        }
    }

    private void exportAmmeterData(ExcelWriter writer, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).is("enabled", true).in("equipmentId", AMMETER_EQUIPMENT_IDS).results();
        if (!CollectionUtils.isEmpty(equipmentList) && equipmentList.size() > 0) {
            for (int i = 0; i < equipmentList.size(); i++) {
                List ammeterData = getAmmeData(deviceName, equipmentList.get(i).getEquipmentId(), startTime, endTime);
                if (!CollectionUtils.isEmpty(ammeterData) && ammeterData.size() > 0) {
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, equipmentList.get(i).getName()).head(AmmeterDataDic.class).build();
                    writer.write(ammeterData, writeSheet);
                }
            }
        }
    }

    private List getAmmeData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<AmmeterDataDic> ammeterDataDics = ammeterDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        return ammeterDataDics;
    }

    private void exportPcsData(ExcelWriter writer, String deviceName, Date startTime, Date endTime) {
        List pcsCabinData = getPcsCabinData(deviceName, startTime, endTime);
        if (!CollectionUtils.isEmpty(pcsCabinData) && pcsCabinData.size() > 0) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0, PCS_DATA_SHEET_NAME).head(PcsCabinetDic.class).build();
            writer.write(pcsCabinData, writeSheet);
        }
        List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).is("enabled", true).greaterThan("equipmentId", 15).lessThan("equipmentId", 32).results();
        if (!CollectionUtils.isEmpty(equipmentList) && equipmentList.size() > 0) {
            for (int i = 1, z = 0; i < equipmentList.size() + 1; i++, z++) {
                List channelData = getPcsChannelData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                if (!CollectionUtils.isEmpty(channelData) && channelData.size() > 0) {
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, equipmentList.get(z).getName()).head(PcsChannelDic.class).build();
                    writer.write(channelData, writeSheet);
                }
            }
        }
    }

    private List getPcsChannelData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<PcsChannelDic> pcsChannelDics = pcsChannelDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        return pcsChannelDics;
    }

    private List getPcsCabinData(String deviceName, Date startTime, Date endTime) {
        List<PcsCabinetDic> pcsCabinetDics = pcsCabinetDicDao.query().is("deviceName", deviceName)
                .is("equipmentId", 15)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        return pcsCabinetDics;
    }

    private void exportBamsData(ExcelWriter writer, String deviceName, Date startTime, Date endTime) {
        List bamsData = getDataBams(deviceName, startTime, endTime);
        if (!CollectionUtils.isEmpty(bamsData) && bamsData.size() > 0) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0, BAMS_DATA_SHEET_NAME).head(BamsDataDicBA.class).build();
            writer.write(bamsData, writeSheet);
        }
        List<Equipment> equipmentList = equipmentDao.query().is("deviceName", deviceName).is("enabled", true).greaterThan("equipmentId", 35).lessThan("equipmentId", 54).results();
        if (!CollectionUtils.isEmpty(equipmentList) && equipmentList.size() > 0) {
            int z = 0;
            int j;
            for (j = 1; j <= equipmentList.size(); j++, z++) {
                WriteSheet writeSheet = EasyExcel.writerSheet(j, equipmentList.get(z).getName()).head(BcuDataDicBCU.class).build();
                List data = getBcuData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                writer.write(data, writeSheet);
            }
            int x;
            for (x = j + 1, z = 0; x <= equipmentList.size() + j; x++, z++) {
                WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                WriteFont headWriteFont = new WriteFont();
                headWriteFont.setFontHeightInPoints((short) 12);
                headWriteCellStyle.setWriteFont(headWriteFont);
                HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                        new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                WriteSheet writeSheet = EasyExcel.writerSheet(x, equipmentList.get(z).getName() + "电芯温度")
                        .registerWriteHandler(horizontalCellStyleStrategy).head(getTempHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                List data = getTempData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                writer.write(data, writeSheet);
            }
            int y;
            for (y = x + 1, z = 0; y <= equipmentList.size() + x; y++, z++) {
                WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                WriteFont headWriteFont = new WriteFont();
                headWriteFont.setFontHeightInPoints((short) 12);
                headWriteCellStyle.setWriteFont(headWriteFont);
                HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                        new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                WriteSheet writeSheet = EasyExcel.writerSheet(y, equipmentList.get(z).getName() + "电芯电压")
                        .registerWriteHandler(horizontalCellStyleStrategy).head(getVolHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                List data = getVolData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                writer.write(data, writeSheet);
            }
        }
    }

    private List<List<String>> getVolHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellVolIte = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is("deviceName", deviceName).is("equipmentId", equipmentId))
                .sort("{generationDataTime:-1}").limit(1).results();
        BmsCellVoltDataDic bmsCellVoltDataDic = null;
        if (cellVolIte != null) {
            for (DBObject object : cellVolIte) {
                bmsCellVoltDataDic = MapperUtil.fromDBObject(BmsCellVoltDataDic.class, object);
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
            }
        }
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        head0.add("设备名称");
        List<String> head1 = new ArrayList<>();
        head1.add("时间");
        List<String> head2 = new ArrayList<>();
        head2.add("前置机通道状态");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        if (bmsCellVoltDataDic != null) {
            for (Map.Entry<String, Integer> entry : bmsCellVoltDataDic.getVolMap().entrySet()) {
                List<String> head3 = new ArrayList<>();
                head3.add(entry.getKey());
                list.add(head3);
            }
        }
        return list;
    }

    private List getVolData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellVoltDataDic> bmsCellVoltDataDics = bmsCellVoltDataDicDao.query().
                is("deviceName", deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellVoltDataDics) && bmsCellVoltDataDics.size() > 0) {
            for (BmsCellVoltDataDic bmsCellVoltDataDic : bmsCellVoltDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellVoltDataDic.getName());
                data.add(bmsCellVoltDataDic.getGenerationDataTime());
                data.add(bmsCellVoltDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                for (Map.Entry<String, Integer> entry : volMap.entrySet()) {
                    data.add(entry.getValue());
                }
                list.add(data);
            }
        }

        return list;
    }

    private List getBcuData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BcuDataDicBCU> bcuDataDicBCUS = bcuDataDicBCUDao.query().is("deviceName", deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals("generationDataTime", startTime)
                .lessThanEquals("generationDataTime", endTime).results();
        return bcuDataDicBCUS;
    }

}
