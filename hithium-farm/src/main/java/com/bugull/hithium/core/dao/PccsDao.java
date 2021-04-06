package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Pccs;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class PccsDao extends BuguDao<Pccs> {
    public PccsDao() {
        super(Pccs.class);
    }
}
