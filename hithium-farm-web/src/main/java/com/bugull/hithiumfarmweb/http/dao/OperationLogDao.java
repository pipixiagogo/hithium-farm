package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogDao extends BuguPageDao<OperationLog> {
    public OperationLogDao() {
        super(OperationLog.class);
    }
}
