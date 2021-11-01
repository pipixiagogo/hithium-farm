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
    public ResHelper<BuguPageQuery.Page<Equipment>> queryEquipment(Integer page, Integer pageSize, String deviceName) {
        BuguPageQuery<Equipment> query =  equipmentDao.pageQuery();
        if(!StringUtils.isEmpty(deviceName)){
            query.is(Const.DEVICE_NAME,deviceName);
        }
        query.is("enabled",true);
        query.pageSize(pageSize).pageNumber(page);
        BuguPageQuery.Page<Equipment> results = query.resultsWithPage();
        return ResHelper.success("", results);
    }
}
