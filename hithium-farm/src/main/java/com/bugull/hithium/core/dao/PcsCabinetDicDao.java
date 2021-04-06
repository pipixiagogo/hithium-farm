package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.PcsCabinetDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class PcsCabinetDicDao extends BuguDao<PcsCabinetDic> {
    public PcsCabinetDicDao() {
        super(PcsCabinetDic.class);
    }
}
