package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.IncomeEntity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class IncomeEntityDao extends BuguDao<IncomeEntity> {
    public IncomeEntityDao() {
        super(IncomeEntity.class);
    }
}
