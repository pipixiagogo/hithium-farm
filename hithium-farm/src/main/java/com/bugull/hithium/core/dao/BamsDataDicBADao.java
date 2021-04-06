package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BamsDataDicBA;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BamsDataDicBADao extends BuguDao<BamsDataDicBA> {
    public BamsDataDicBADao() {
        super(BamsDataDicBA.class);
    }
}
