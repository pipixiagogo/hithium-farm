package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.http.dao.OperationLogDao;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.entity.OperationLogEntity;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OperationLogService {

    @Resource
    private OperationLogDao operationLogDao;

    public void save(OperationLog operationLog) {
        operationLogDao.saveOperationLog(operationLog);
    }


    public ResHelper<Page<OperationLogEntity>> query(Integer page, Integer pageSize, String username, String operation) {
        Query batchQuery = new Query();
        if (!StringUtils.isEmpty(username)) {
            batchQuery.addCriteria(Criteria.where("username").regex(username));
        }
        if (!StringUtils.isEmpty(operation)) {
            batchQuery.addCriteria(Criteria.where("operation").regex(operation));
        }
        long totalRecord = operationLogDao.count(batchQuery);
        batchQuery.skip(((long) page - 1) * pageSize).limit(pageSize).with(Sort.by(Sort.Order.desc("_id")));
        List<OperationLogEntity> operationLogEntityList = operationLogDao.findBatch(batchQuery);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, operationLogEntityList));
    }
}
