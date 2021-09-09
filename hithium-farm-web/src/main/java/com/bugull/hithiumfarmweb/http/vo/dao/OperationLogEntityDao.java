package com.bugull.hithiumfarmweb.http.vo.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.vo.entity.OperationLogEntity;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogEntityDao extends BuguPageDao<OperationLogEntity> {
    public OperationLogEntityDao() {
        super(OperationLogEntity.class);
    }
}
