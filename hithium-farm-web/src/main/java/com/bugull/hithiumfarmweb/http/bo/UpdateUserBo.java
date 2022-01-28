package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;


import java.util.List;

@Data
public class UpdateUserBo {
    private Long id;

    private String userName;
    private String email;
    private String mobile;

    private List<String> roleIds;
    private String userExpireTimeStr;
    private String remarks;
    private Integer status;

    private List<String> stationList;

    private Integer userType;


}
