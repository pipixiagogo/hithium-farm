package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BamsDischargeCapacity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BamsDischargeCapacityDao extends BuguDao<BamsDischargeCapacity> {
    public BamsDischargeCapacityDao() {
        super(BamsDischargeCapacity.class);
    }
}
