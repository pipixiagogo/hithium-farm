package com.bugull.hithiumfarmweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaEntity {
    private String date;

    private double sum;

    private List<String> list;


}
