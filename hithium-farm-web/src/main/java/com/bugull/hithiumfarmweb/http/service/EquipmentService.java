package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.http.dao.EquipmentDao;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.bugull.hithiumfarmweb.common.Const.DEVICE_NAME;


@Service
public class EquipmentService {

    @Resource
    private EquipmentDao equipmentDao;
    public ResHelper<Page<Equipment>> queryEquipment(Integer page, Integer pageSize, String deviceName) {
        Query findBatchQuery = new Query();
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(deviceName)){
            criteria.and(DEVICE_NAME).is(deviceName);
        }
        criteria.and("enabled").is(true);
        findBatchQuery.addCriteria(criteria);
        long totalRecord= equipmentDao.count(findBatchQuery);
        findBatchQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        List<Equipment> equipmentList =equipmentDao.findBatchEquipment(findBatchQuery);

        return ResHelper.success("", new Page<>(page,pageSize,totalRecord,equipmentList));
    }
}
