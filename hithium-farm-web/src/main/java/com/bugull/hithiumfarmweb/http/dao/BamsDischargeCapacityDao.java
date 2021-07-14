package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BamsDischargeCapacity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;


@Repository
public class BamsDischargeCapacityDao extends BuguDao<BamsDischargeCapacity> {
    public BamsDischargeCapacityDao() {
        super(BamsDischargeCapacity.class);
    }
}
