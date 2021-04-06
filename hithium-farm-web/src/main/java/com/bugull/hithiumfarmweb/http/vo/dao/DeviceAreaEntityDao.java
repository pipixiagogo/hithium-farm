package com.bugull.hithiumfarmweb.http.vo.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceAreaEntityDao extends BuguPageDao<DeviceAreaEntity> {
    public DeviceAreaEntityDao() {
        super(DeviceAreaEntity.class);
    }
}
