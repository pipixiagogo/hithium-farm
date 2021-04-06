package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.BmsCellTempDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class BmsCellTempDataDicDao extends BuguPageDao<BmsCellTempDataDic> {
    public BmsCellTempDataDicDao() {
        super(BmsCellTempDataDic.class);
    }
}
