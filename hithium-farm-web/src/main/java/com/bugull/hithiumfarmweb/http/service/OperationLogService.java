package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.dao.OperationLogDao;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class OperationLogService {

    @Resource
    private OperationLogDao operationLogDao;

    public void save(OperationLog operationLog) {
        operationLogDao.insert(operationLog);
    }


    public ResHelper<BuguPageQuery.Page<OperationLog>> query(Map<String, Object> params) {
        BuguPageQuery<OperationLog> query = (BuguPageQuery<OperationLog>) operationLogDao.pageQuery();
        String username = (String) params.get("username");
        if (!StringUtils.isBlank(username)) {
            query.regexCaseInsensitive("username", username);
        }
        String operation=(String)params.get("operation");
        if(!StringUtils.isBlank(operation)){
            query.regexCaseInsensitive("operation",operation);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<OperationLog> operationLogPage = query.resultsWithPage();
        return ResHelper.success("", operationLogPage);
    }
}
