package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BcuDataDicBCU;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BcuDataDicBCUDao extends BuguDao<BcuDataDicBCU> {
    public BcuDataDicBCUDao() {
        super(BcuDataDicBCU.class);
    }
}
