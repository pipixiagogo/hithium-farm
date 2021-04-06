package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Device;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceDao extends BuguDao<Device> {

    public DeviceDao() {
        super(Device.class);
    }
}
