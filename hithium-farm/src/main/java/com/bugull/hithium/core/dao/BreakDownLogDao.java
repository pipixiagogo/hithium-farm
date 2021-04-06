package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BreakDownLog;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BreakDownLogDao extends BuguDao<BreakDownLog> {
    public BreakDownLogDao() {
        super(BreakDownLog.class);
    }
}
