package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.Cabin;
import org.springframework.stereotype.Repository;

@Repository
public class CabinDao extends BuguPageDao<Cabin> {
    public CabinDao() {
        super(Cabin.class);
    }
}
