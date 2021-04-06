package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.PcsChannelDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class PcsChannelDicDao extends BuguDao<PcsChannelDic> {
    public PcsChannelDicDao() {
        super(PcsChannelDic.class);
    }
}
