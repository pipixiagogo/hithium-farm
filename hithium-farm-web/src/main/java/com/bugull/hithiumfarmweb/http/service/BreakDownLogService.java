package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class BreakDownLogService {

    @Resource
    private BreakDownLogDao breakDownLogDao;


    public ResHelper<BuguPageQuery.Page<BreakDownLog>> query(Map<String, Object> params) {
        BuguPageQuery<BreakDownLog> query = (BuguPageQuery<BreakDownLog>) breakDownLogDao.pageQuery();
        String deviceName = (String) params.get("deviceName");
        if (deviceName == null)
            return ResHelper.pamIll();
        query.is("deviceName", deviceName);
        if (!PagetLimitUtil.pageLimit(query, params)) return ResHelper.pamIll();
        if(!PagetLimitUtil.orderField(query,params)) return ResHelper.pamIll();
        BuguPageQuery.Page<BreakDownLog> downLogPage = query.resultsWithPage();
        return ResHelper.success("", downLogPage);
    }
}
