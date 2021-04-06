package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.entity.BamsDataDicBA;
import org.springframework.stereotype.Repository;

@Repository
public class BamsDataDicBADao extends BuguPageDao<BamsDataDicBA> {
    public BamsDataDicBADao() {
        super(BamsDataDicBA.class);
    }
}
