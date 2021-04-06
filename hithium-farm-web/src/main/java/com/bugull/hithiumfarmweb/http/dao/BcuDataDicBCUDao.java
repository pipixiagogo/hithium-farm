package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.BcuDataDicBCU;
import org.springframework.stereotype.Repository;

@Repository
public class BcuDataDicBCUDao extends BuguPageDao<BcuDataDicBCU> {
    public BcuDataDicBCUDao() {
        super(BcuDataDicBCU.class);
    }
}
