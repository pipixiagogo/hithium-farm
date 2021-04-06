package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.Pccs;
import org.springframework.stereotype.Repository;

@Repository
public class PccsDao extends BuguPageDao<Pccs> {
    public PccsDao() {
        super(Pccs.class);
    }
}
