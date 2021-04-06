package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.ConnetDevice;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class ConnetDeviceDao extends BuguDao<ConnetDevice> {
    public ConnetDeviceDao() {
        super(ConnetDevice.class);
    }
}
