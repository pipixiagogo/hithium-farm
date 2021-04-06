package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.mongo.annotations.Entity;
import org.springframework.stereotype.Repository;

@Repository
@Entity
public class EquipmentDao extends BuguPageDao<Equipment> {
    public EquipmentDao() {
        super(Equipment.class);
    }
}
