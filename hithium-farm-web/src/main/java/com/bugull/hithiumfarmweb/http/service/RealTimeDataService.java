package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.excelBo.AirConditionExcelBo;
import com.bugull.hithiumfarmweb.http.vo.BcuDataVolTemVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.fs.Uploader;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
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
    private EquipmentDao equipmentDao;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private ExportRecordDao exportRecordDao;

    protected static final List<Integer> AMMETER_EQUIPMENT_IDS = new ArrayList<>();
    protected static final List<Integer> FIRE_AIR_UPS_EQUIPMENT_IDS = new ArrayList<>();
    public static final List<Integer> EXPORT_DATA_TYPE = new ArrayList<>();

    static {
        AMMETER_EQUIPMENT_IDS.add(3);
        AMMETER_EQUIPMENT_IDS.add(6);
        AMMETER_EQUIPMENT_IDS.add(11);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(1);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(12);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(13);
        for (int i = 0; i < 4; i++) {
            EXPORT_DATA_TYPE.add(i);
        }
    }

    public ResHelper<BuguPageQuery.Page<AmmeterDataDic>> ammeterDataQuery(Map<String, Object> params) {
        BuguPageQuery<AmmeterDataDic> query = ammeterDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<AmmeterDataDic> ammeterDataDicPage = query.resultsWithPage();
        return ResHelper.success("", ammeterDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BamsDataDicBA>> bamsDataquery(Map<String, Object> params) {
        BuguPageQuery<BamsDataDicBA> query = bamsDataDicBADao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BamsDataDicBA> bamsDataDicBAPage = query.resultsWithPage();
        return ResHelper.success("", bamsDataDicBAPage);
    }

    private boolean queryOfParams(BuguPageQuery<?> query, Map<String, Object> params) {
        String deviceName = (String) params.get(DEVICE_NAME);
        if (!StringUtils.isEmpty(deviceName)) {
            query.is(DEVICE_NAME, deviceName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return false;
        if (!PagetLimitUtil.orderField(query, params)) return false;
        return true;
    }

    public ResHelper<BuguPageQuery.Page<BmsCellTempDataDic>> bmsTempDataquery(Map<String, Object> params) {
        BuguPageQuery<BmsCellTempDataDic> query = bmsCellTempDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BmsCellTempDataDic> bmsCellTempDataDicPage = query.resultsWithPage();
        return ResHelper.success("", bmsCellTempDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BmsCellVoltDataDic>> bmsVoltDataquery(Map<String, Object> params) {
        BuguPageQuery<BmsCellVoltDataDic> query = bmsCellVoltDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BmsCellVoltDataDic> bmsCellVoltDataDicPage = query.resultsWithPage();
        return ResHelper.success("", bmsCellVoltDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<BcuDataDicBCU>> bcuDataquery(Map<String, Object> params) {
        BuguPageQuery<BcuDataDicBCU> query = bcuDataDicBCUDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<BcuDataDicBCU> bcuDataDicBCUPage = query.resultsWithPage();
        return ResHelper.success("", bcuDataDicBCUPage);
    }

    public ResHelper<BuguPageQuery.Page<PcsCabinetDic>> pcsCabinetquery(Map<String, Object> params) {
        BuguPageQuery<PcsCabinetDic> query = pcsCabinetDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<PcsCabinetDic> pcsCabinetDicPage = query.resultsWithPage();
        return ResHelper.success("", pcsCabinetDicPage);
    }

    public ResHelper<BuguPageQuery.Page<PcsChannelDic>> pcsChannelquery(Map<String, Object> params) {
        BuguPageQuery<PcsChannelDic> query = pcsChannelDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<PcsChannelDic> pcsChannelDicPage = query.resultsWithPage();
        return ResHelper.success("", pcsChannelDicPage);
    }

    public ResHelper<BuguPageQuery.Page<AirConditionDataDic>> airConditionquery(Map<String, Object> params) {
        BuguPageQuery<AirConditionDataDic> query = airConditionDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<AirConditionDataDic> airConditionDataDicPage = query.resultsWithPage();
        return ResHelper.success("", airConditionDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<TemperatureMeterDataDic>> temperatureMeterquery(Map<String, Object> params) {
        BuguPageQuery<TemperatureMeterDataDic> query = temperatureMeterDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<TemperatureMeterDataDic> temperatureMeterDataDicPage = query.resultsWithPage();
        return ResHelper.success("", temperatureMeterDataDicPage);
    }


    public ResHelper<BuguPageQuery.Page<FireControlDataDic>> fileControlquery(Map<String, Object> params) {
        BuguPageQuery<FireControlDataDic> query = fireControlDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<FireControlDataDic> fireControlDataDicPage = query.resultsWithPage();
        return ResHelper.success("", fireControlDataDicPage);
    }

    public ResHelper<BuguPageQuery.Page<UpsPowerDataDic>> upsPowerquery(Map<String, Object> params) {
        BuguPageQuery<UpsPowerDataDic> query = upsPowerDataDicDao.pageQuery();
        if (!queryOfParams(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<UpsPowerDataDic> upsPowerDataDicPage = query.resultsWithPage();
        return ResHelper.success("", upsPowerDataDicPage);
    }

    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(String deviceName) {
        Iterable<DBObject> iterable = temperatureMeterDataDicDao.aggregate().match(temperatureMeterDataDicDao.query().is(DEVICE_NAME, deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(3).results();
        List<String> queryIds = new ArrayList<>();
        for (DBObject object : iterable) {
            ObjectId dataId = (ObjectId) object.get("dataId");
            queryIds.add(dataId.toString());
        }
        if (!CollectionUtils.isEmpty(queryIds) && !queryIds.isEmpty()) {
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
        Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is(DEVICE_NAME, deviceName))
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
        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is(DEVICE_NAME, deviceName))
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
        if (!CollectionUtils.isEmpty(queryIds) && !queryIds.isEmpty()) {
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
        Iterable<DBObject> tempIterable = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is(DEVICE_NAME, deviceName))
                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").limit(16).results();
        Iterable<DBObject> volIterable = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is(DEVICE_NAME, deviceName))
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
        if (!CollectionUtils.isEmpty(bmsCellTempDataDicbyIds) && !bmsCellTempDataDicbyIds.isEmpty() &&
                !CollectionUtils.isEmpty(bmsCellVoltDataDicbyIds) && !bmsCellVoltDataDicbyIds.isEmpty()) {
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
        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is(DEVICE_NAME, deviceName)
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
        Iterable<DBObject> cellTempIte = bmsCellTempDataDicDao.aggregate().match(bcuDataDicBCUDao.query().is(DEVICE_NAME, deviceName)
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

        Iterable<DBObject> cellVolIte = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId))
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
        Iterable<DBObject> iterable = buguPageDao.aggregate().match(buguPageDao.query().is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)).sort("{generationDataTime:-1}").limit(1).results();
        if (iterable != null) {
            Object dbObject = null;
            for (DBObject object : iterable) {
                dbObject = MapperUtil.fromDBObject(buguPageDao.getEntityClass(), object);
            }
            return dbObject;
        }
        return null;
    }

    public List<BamsDataDicBA> getDataBams(String deviceName, Date startTime, Date endTime) {
        return bamsDataDicBADao.query().is(DEVICE_NAME, deviceName)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    public List<AirConditionExcelBo> getAirConditionData(String deviceName, Date startTime, Date endTime) {
        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is(DEVICE_NAME, deviceName)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        return airConditionDataDics.stream().map(airConditionDataDic -> {
            AirConditionExcelBo airConditionExcelBo = new AirConditionExcelBo();
            BeanUtils.copyProperties(airConditionDataDic, airConditionExcelBo);
            //TODO 直接这样类型转换
            airConditionExcelBo.setSensorFaultMsg(airConditionDataDic.getSensorFault() == 1 ? "故障" : "正常");
            airConditionExcelBo.setArefactionOnhumidity(airConditionDataDic.getArefactionOnhumidity());
            airConditionExcelBo.setArefactionOffhumidity(airConditionDataDic.getArefactionOffhumidity());
            return airConditionExcelBo;
        }).collect(Collectors.toList());
    }

    public List<List<String>> getTempHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellTempIte = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is(DEVICE_NAME, deviceName)
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
                is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempDataDics) && !bmsCellTempDataDics.isEmpty()) {
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

    /**

     * @param deviceName
     * @param time
     * @param type
     * @param outputStream
     * @throws ParseException
     */
    public void exportStationOfBattery(String deviceName, String time, Integer type, OutputStream outputStream) throws ParseException {
        if (StringUtils.isEmpty(deviceName)) {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
        Date startTime = null;
        Date endTime = null;
        if (StringUtils.isEmpty(time)) {
            Date date = new Date();
            startTime = DateUtils.getStartTime(date);
            endTime = DateUtils.getEndTime(date);
        } else {
            //根据传入的数值获取
            if (!Const.DATE_PATTERN.matcher(time).matches()) {
                throw new ExcelExportWithoutDataException("日期格式错误");
            }
            startTime = DateUtils.dateToStrWithHHmm(time);
            endTime = DateUtils.getEndTime(startTime);
        }
        if (type == 0) {
            exportPcsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 1) {
            exportBamsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 2) {
            exportAmmeterData(outputStream, deviceName, startTime, endTime);
        } else if (type == 3) {
            exportUpsAirFireData(outputStream, deviceName, startTime, endTime);
        } else {
            throw new ExcelExportWithoutDataException("暂无该类型数据");
        }
    }

    private void exportUpsAirFireData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).in("equipmentId", FIRE_AIR_UPS_EQUIPMENT_IDS)
                .results();
        if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
            ExcelWriter writer = null;
            try {
                for (int i = 0; i < equipmentList.size(); i++) {
                    if (equipmentList.get(i).getEquipmentId() == 1) {
                        List<FireControlDataDic> fireControlDataDics = fireControlDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(fireControlDataDics) && !fireControlDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(FireControlDataDic.class).build();
                            writer.write(fireControlDataDics, writeSheet);
                        }
                    } else if (equipmentList.get(i).getEquipmentId() == 12) {
                        List<UpsPowerDataDic> upsPowerDataDics = upsPowerDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(upsPowerDataDics) && !upsPowerDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(UpsPowerDataDic.class).build();
                            writer.write(upsPowerDataDics, writeSheet);
                        }
                    } else if (equipmentList.get(i).getEquipmentId() == 13) {
                        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(airConditionDataDics) && !airConditionDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AirConditionDataDic.class).build();
                            writer.write(airConditionDataDics, writeSheet);
                        }
                    }
                }
                if (writer == null) {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }

        }
    }

    private void exportAmmeterData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).in("equipmentId", AMMETER_EQUIPMENT_IDS).results();
        if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
            ExcelWriter writer = null;
            try {
                for (int i = 0; i < equipmentList.size(); i++) {
                    List ammeterData = getAmmeData(deviceName, equipmentList.get(i).getEquipmentId(), startTime, endTime);
                    if (!CollectionUtils.isEmpty(ammeterData) && !ammeterData.isEmpty()) {
                        if (writer == null) {
                            writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                        }
                        WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AmmeterDataDic.class).build();
                        writer.write(ammeterData, writeSheet);
                    } else {
                        throw new ExcelExportWithoutDataException("该日期暂无数据");
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
    }

    private List getAmmeData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return ammeterDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    private void exportPcsData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        ExcelWriter writer = null;
        List pcsCabinData = getPcsCabinData(deviceName, startTime, endTime);
        try {
            if (!CollectionUtils.isEmpty(pcsCabinData) && !pcsCabinData.isEmpty()) {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, PCS_DATA_SHEET_NAME).head(PcsCabinetDic.class).build();
                writer.write(pcsCabinData, writeSheet);
                List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).greaterThan("equipmentId", 15).lessThan("equipmentId", 32).results();
                if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
                    for (int i = 1, z = 0; i < equipmentList.size() + 1; i++, z++) {
                        List channelData = getPcsChannelData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(channelData) && !channelData.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(i, equipmentList.get(z).getName()).head(PcsChannelDic.class).build();
                            writer.write(channelData, writeSheet2);
                        }
                    }
                }
            } else {
                throw new ExcelExportWithoutDataException("该日期暂无数据");
            }
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private List getPcsChannelData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return pcsChannelDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    private List getPcsCabinData(String deviceName, Date startTime, Date endTime) {
        return pcsCabinetDicDao.query().is(DEVICE_NAME, deviceName)
                .is("equipmentId", 15)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    private void exportBamsData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List bamsData = getDataBams(deviceName, startTime, endTime);
        if (!CollectionUtils.isEmpty(bamsData) && !bamsData.isEmpty()) {
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, BAMS_DATA_SHEET_NAME).head(BamsDataDicBA.class).build();
                writer.write(bamsData, writeSheet);
                List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).greaterThan("equipmentId", 35).lessThan("equipmentId", 54).results();
                if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
                    int z = 0;
                    int j;
                    for (j = 1; j <= equipmentList.size(); j++, z++) {
                        List data = getBcuData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        WriteSheet writeSheet1 = EasyExcelFactory.writerSheet(j, equipmentList.get(z).getName()).head(BcuDataDicBCU.class).build();
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            writer.write(data, writeSheet1);
                        }
                    }
                    int x;
                    for (x = j + 1, z = 0; x <= equipmentList.size() + j; x++, z++) {
                        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                        WriteFont headWriteFont = new WriteFont();
                        headWriteFont.setFontHeightInPoints((short) 12);
                        headWriteCellStyle.setWriteFont(headWriteFont);
                        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                                new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                        List data = getTempData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(x, equipmentList.get(z).getName() + "电芯温度")
                                    .registerWriteHandler(horizontalCellStyleStrategy).head(getTempHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                            writer.write(data, writeSheet2);
                        }
                    }
                    int y;
                    for (y = x + 1, z = 0; y <= equipmentList.size() + x; y++, z++) {
                        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                        WriteFont headWriteFont = new WriteFont();
                        headWriteFont.setFontHeightInPoints((short) 12);
                        headWriteCellStyle.setWriteFont(headWriteFont);
                        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                                new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                        List data = getVolData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            WriteSheet writeSheet3 = EasyExcelFactory.writerSheet(y, equipmentList.get(z).getName() + "电芯电压")
                                    .registerWriteHandler(horizontalCellStyleStrategy).head(getVolHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                            writer.write(data, writeSheet3);
                        }
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        } else {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
    }

    private List<List<String>> getVolHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellVolIte = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId))
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
                is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellVoltDataDics) && !bmsCellVoltDataDics.isEmpty()) {
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
        return bcuDataDicBCUDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    /**
     * 创建个excel导出定时任务 当从数据库拿时候数据较慢时 可进行启动
     */
    @Scheduled(cron = "${excel.scheld.export.excel}")
    public void scheldExportExcel() {
        if (!propertiesConfig.isStartExportExcelSwitch()) {
            return;
        }
        //当前分页查询导出200个设备 可根据线上情况 实时查看导出情况在决定该设备数量
        long count = deviceDao.count();
        int pageSize = propertiesConfig.getExcelExportPageSize();
        Date date = new Date();
        Date beforeDate = DateUtils.addDateDays(date, -1);
        String time = DateUtils.format(beforeDate);
        log.info("定时执行excel导出任务:设备总数量{},导出时间:{}", count, time);
        for (int i = 0; i < (int) Math.ceil((double) count / (double) pageSize); i++) {
            List<Device> deviceList = deviceDao.query().pageSize(pageSize).pageNumber(i + 1).results();
            for (Device device : deviceList) {
                for (int type : EXPORT_DATA_TYPE) {
                    threadPoolExecutor.execute(() -> {
                        String filePath = propertiesConfig.getRealTimeDataExcelTempDir() + File.separator + device.getDeviceName() + device.getDeviceName() + "_" + time + "_" + type + ".xlsx";
                        File file = new File(filePath);
                        OutputStream outputStream = null;
                        ExportRecord exportRecord = new ExportRecord();
                        try {
                            makeSureFileExist(file);
                            outputStream = new FileOutputStream(file);
                            exportRecord.setRecordTime(new Date());
                            String filename = device.getDeviceName() + "_" + time + "_" + type;
                            exportRecord.setFilename(filename);
                            exportStationOfBattery(device.getDeviceName(), time, type, outputStream);
                            exportRecord.setExporting(true);
                            outputStream.close();
                            if(!exportRecordDao.query().is("filename",filename).exists()){
                                Uploader uploader = new Uploader(file, exportRecord.getFilename(), false);
                                uploader.save();
                                exportRecordDao.insert(exportRecord);
                            }
                        } catch (Exception e) {
                            log.error("定时执行导出excel失败:{},导出excel数据时间为:{},设备名称:{},导出设备类型:{}", e.getMessage(), time, device.getDeviceName(), getMsgByType(type));
                            exportRecord.setExporting(false);
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            file.delete();
                        }
                    });
                }
            }
        }


    }

    private void makeSureFileExist(File file) throws IOException {
        if (file.exists()) {
            return;
        }
        synchronized (Thread.currentThread()) {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
    }

    public String getMsgByType(Integer type) {
        switch (type) {
            case 0:
                return "PCS";
            case 1:
                return "电池堆";
            case 2:
                return "电表";
            case 3:
                return "附属件";
            default:
                return "未知数据";
        }
    }
}
