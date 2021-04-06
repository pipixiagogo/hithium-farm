package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.PcsChannelDic;
import org.springframework.stereotype.Repository;

@Repository
public class PcsChannelDicDao extends BuguPageDao<PcsChannelDic> {
    public PcsChannelDicDao() {
        super(PcsChannelDic.class);
    }
}
