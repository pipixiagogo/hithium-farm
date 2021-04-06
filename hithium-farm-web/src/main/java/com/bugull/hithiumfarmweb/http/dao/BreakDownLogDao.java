package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import org.springframework.stereotype.Repository;

@Repository
public class BreakDownLogDao extends BuguPageDao<BreakDownLog> {
    public BreakDownLogDao() {
        super(BreakDownLog.class);
    }
}
