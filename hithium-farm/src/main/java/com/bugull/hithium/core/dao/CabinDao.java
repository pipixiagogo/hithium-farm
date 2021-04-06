package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Cabin;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class CabinDao extends BuguDao<Cabin> {
    public CabinDao() {
        super(Cabin.class);
    }
}
