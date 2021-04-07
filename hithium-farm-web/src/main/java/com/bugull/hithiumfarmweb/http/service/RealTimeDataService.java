package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.BamsLatestBo;
import com.bugull.hithiumfarmweb.http.bo.BcuDataBo;
import com.bugull.hithiumfarmweb.http.bo.BmsTempMsgBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguQuery;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.count;

@Service
public class RealTimeDataService {

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
        if (deviceName == null) return false;
        query.is("deviceName", deviceName);
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
        /**
         * 用这个看下 聚合快 还是查询快
         */
        List<Equipment> equipment = equipmentDao.query().is("deviceName", deviceName).results();
        List<BmsTempMsgBo> bos = new ArrayList<>();
        equipment.stream().forEach(euip -> {
            BuguQuery<TemperatureMeterDataDic> dicBuguQuery = temperatureMeterDataDicDao.query().is("deviceName", deviceName)
                    .is("equipmentId", euip.getEquipmentId());
            long count = dicBuguQuery.count();
            if (count > 0) {
                List<TemperatureMeterDataDic> temperatureMeterDataDic = dicBuguQuery
                        .pageSize(1)
                        .pageNumber(Integer.parseInt(String.valueOf(count)))
                        .results();
                if (!CollectionUtils.isEmpty(temperatureMeterDataDic) && temperatureMeterDataDic.size() > 0) {
                    TemperatureMeterDataDic temperatureMeterDataDic1 = temperatureMeterDataDic.get(0);
                    if (temperatureMeterDataDic1 != null) {
                        BmsTempMsgBo bmsTempMsgBo = new BmsTempMsgBo();
                        bmsTempMsgBo.setName(temperatureMeterDataDic1.getName());
                        bmsTempMsgBo.setTemperature(temperatureMeterDataDic1.getTemperature());
                        bmsTempMsgBo.setHumidity(temperatureMeterDataDic1.getHumidity());
                        bmsTempMsgBo.setCreateDate(temperatureMeterDataDic1.getGenerationDataTime());
                        bos.add(bmsTempMsgBo);
                    }
                }
            }
        });
        return ResHelper.success("", bos);
    }


    public ResHelper<BamsLatestBo> bamsLatestData(String deviceName) {
        Iterable<DBObject> iterable = bamsDataDicBADao.aggregate().match(bamsDataDicBADao.query().is("deviceName", deviceName))
                .group("{_id:'$name','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").results();
        String queryId = null;
        for (DBObject object : iterable) {
            ObjectId dataId = (ObjectId) object.get("dataId");
            queryId = dataId.toString();
        }
        if (!StringUtils.isEmpty(queryId)) {
            BamsDataDicBA bamsDataDicBA = bamsDataDicBADao.query().is("_id", queryId).result();
            BamsLatestBo bamsLatestBo = new BamsLatestBo();
            BeanUtils.copyProperties(bamsDataDicBA, bamsLatestBo);
            return ResHelper.success("", bamsLatestBo);
        }
        return ResHelper.success("");
    }

    public ResHelper<BcuDataBo> bcuDataqueryByName(String deviceName,String name) {

        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("name","集装箱电池簇1").is("deviceName",deviceName))
                .group("{_id:'$"+name+"','dataId':{$last:'$_id'}}")
                .sort("{generationDataTime:-1}").results();
        for (DBObject object : iterable) {
            ObjectId dataId = (ObjectId) object.get("dataId");
            String queryId = dataId.toString();
            if(!StringUtils.isEmpty(queryId)){
                BcuDataDicBCU bcuDataDicBCU = bcuDataDicBCUDao.query().is("_id", queryId).result();

            }
        }

        return null;
    }
}
