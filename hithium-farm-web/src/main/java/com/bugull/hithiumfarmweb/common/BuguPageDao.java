package com.bugull.hithiumfarmweb.common;

import com.bugull.mongo.BuguDao;

public class BuguPageDao<T> extends BuguDao<T> {
    public BuguPageDao(Class<T> clazz) {
        super(clazz);
    }
    public BuguPageQuery<T> pageQuery() {
        return new BuguPageQuery<>(this);
    }

}
