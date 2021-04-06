package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class MenuEntityDao extends BuguDao<MenuEntity> {
    public MenuEntityDao() {
        super(MenuEntity.class);
    }
}
