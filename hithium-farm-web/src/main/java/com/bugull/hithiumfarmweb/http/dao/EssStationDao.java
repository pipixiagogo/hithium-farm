package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class EssStationDao extends BuguPageDao<EssStation> {
    public EssStationDao() {
        super(EssStation.class);
    }
}
