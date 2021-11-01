package com.bugull.hithiumfarmweb.http.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticBo {

    private String date;
    private Double count = 0D;


}
