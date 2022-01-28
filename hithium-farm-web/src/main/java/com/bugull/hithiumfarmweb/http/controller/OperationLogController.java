package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.http.service.OperationLogService;
import com.bugull.hithiumfarmweb.http.entity.OperationLogEntity;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 操作日志
 */
@RestController
@RequestMapping(value = "/operationLog")
public class OperationLogController extends AbstractController {

    @Resource
    private OperationLogService operationLogService;

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/query")
    public ResHelper<Page<OperationLogEntity>> query(@RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                     @RequestParam(value = "username", required = false) String username,
                                                     @RequestParam(value = "operation",required = false)String operation) {
        if(page == null || pageSize == null){
            return ResHelper.pamIll();
        }
        return operationLogService.query(page,pageSize,username,operation);
    }
}
