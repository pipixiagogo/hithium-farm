package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Cube;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class CubeDao extends BuguDao<Cube> {
    public CubeDao() {
        super(Cube.class);
    }
}
