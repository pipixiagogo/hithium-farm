package com.bugull.hithiumfarmweb.http.dao;


import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BuguPageDao<User> {
    public UserDao() {
        super(User.class);
    }
}
