package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.dao.EquipmentDao;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class EquipmentService {

    @Resource
    private EquipmentDao equipmentDao;

    public ResHelper<BuguPageQuery.Page<Equipment>> queryEquipment(Map<String, Object> params) {
        BuguPageQuery<Equipment> query =  equipmentDao.pageQuery();
        String deviceName=(String) params.get(Const.DEVICE_NAME);
        if(!StringUtils.isEmpty(deviceName)){
            query.is(Const.DEVICE_NAME,deviceName);
        }
        query.is("enabled",true);
        if(!PagetLimitUtil.pageLimit(query, params)){
            return ResHelper.pamIll();
        }
        BuguPageQuery.Page<Equipment> results = query.resultsWithPage();
        return ResHelper.success("", results);
    }


}
