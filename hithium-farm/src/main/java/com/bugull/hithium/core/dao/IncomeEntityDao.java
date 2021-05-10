package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.IncomeEntity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class IncomeEntityDao extends BuguDao<IncomeEntity> {
    public IncomeEntityDao() {
        super(IncomeEntity.class);
    }
}
