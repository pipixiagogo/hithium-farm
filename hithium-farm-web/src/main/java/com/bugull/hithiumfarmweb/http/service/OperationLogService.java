package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.dao.OperationLogDao;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.vo.dao.OperationLogEntityDao;
import com.bugull.hithiumfarmweb.http.vo.entity.OperationLogEntity;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class OperationLogService {

    @Resource
    private OperationLogDao operationLogDao;
    @Resource
    private OperationLogEntityDao operationLogEntityDao;

    public void save(OperationLog operationLog) {
        operationLogDao.insert(operationLog);
    }


    public ResHelper<BuguPageQuery.Page<OperationLogEntity>> query(Integer page, Integer pageSize, String username, String operation) {
        BuguPageQuery<OperationLogEntity> query = operationLogEntityDao.pageQuery();
        if (!StringUtils.isEmpty(username)) {
            query.regexCaseInsensitive("username", username);
        }
        if (!StringUtils.isEmpty(operation)) {
            query.regexCaseInsensitive("operation", operation);
        }
        query.pageSize(pageSize).pageNumber(page);
        query.sortDesc("_id");
        BuguPageQuery.Page<OperationLogEntity> operationLogPage = query.resultsWithPage();
        return ResHelper.success("", operationLogPage);
    }
}
