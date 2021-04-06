package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.Cube;
import org.springframework.stereotype.Repository;

@Repository
public class CubeDao extends BuguPageDao<Cube> {
    public CubeDao() {
        super(Cube.class);
    }
}
