package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.Device;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceDao extends BuguPageDao<Device> {
    public DeviceDao() {
        super(Device.class);
    }
}
