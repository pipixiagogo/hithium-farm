package com.bugull.hithiumfarmweb.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class CaEntity  extends SimpleEntity{
    private String date;

    private double sum;

    private List<String> list;


}
