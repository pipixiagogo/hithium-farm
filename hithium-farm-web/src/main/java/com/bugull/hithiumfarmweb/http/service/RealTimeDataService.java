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

}
