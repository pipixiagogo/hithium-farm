package com.bugull.hithiumfarmweb.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.Date;

@Entity
@EnsureIndex("{expireTime:1,expireAfterSeconds:300}")
@Data
public class TestExpireIndex extends SimpleEntity {
    private Date expireTime;

    private String name;
}
