package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.dao.EquipmentDao;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class EquipmentService {

    @Resource
    private EquipmentDao equipmentDao;

    @Resource
    private DeviceDao deviceDao;

    public ResHelper<BuguPageQuery.Page<Equipment>> queryEquipment(Map<String, Object> params) {

        BuguPageQuery<Equipment> query = (BuguPageQuery<Equipment>) equipmentDao.pageQuery();
        if(!PagetLimitUtil.pageLimit(query, params)){
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<Equipment> results = query.resultsWithPage();
        return ResHelper.success("", results);
    }


}
