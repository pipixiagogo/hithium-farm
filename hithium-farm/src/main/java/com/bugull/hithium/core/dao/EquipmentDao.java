package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Equipment;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class EquipmentDao extends BuguDao<Equipment> {
    public EquipmentDao() {
        super(Equipment.class);
    }
}
