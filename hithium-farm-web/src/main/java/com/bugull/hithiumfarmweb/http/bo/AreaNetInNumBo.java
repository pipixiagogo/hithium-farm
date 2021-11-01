package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

@Data
public class AreaNetInNumBo {
    private Integer year;
    private String type;
    private String country="china";

    private String province;

    private String city;

}
