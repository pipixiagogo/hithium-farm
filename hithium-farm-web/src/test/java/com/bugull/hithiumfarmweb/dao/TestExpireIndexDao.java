package com.bugull.hithiumfarmweb.dao;

import com.bugull.hithiumfarmweb.entity.TestExpireIndex;
import com.bugull.mongo.BuguDao;

public class TestExpireIndexDao extends BuguDao<TestExpireIndex> {
    public TestExpireIndexDao() {
        super(TestExpireIndex.class);
    }
}
