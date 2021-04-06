package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.PcsCabinetDic;
import org.springframework.stereotype.Repository;

@Repository
public class PcsCabinetDicDao extends BuguPageDao<PcsCabinetDic> {
    public PcsCabinetDicDao() {
        super(PcsCabinetDic.class);
    }
}
