package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.service.OperationLogService;
import com.bugull.hithiumfarmweb.http.vo.entity.OperationLogEntity;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 操作日志
 */
@RestController
@RequestMapping(value = "/operationLog")
public class OperationLogController extends AbstractController{

    @Resource
    private OperationLogService operationLogService;

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/query")
    @ApiOperation(value = "分页查询操作日志",response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1",name = "page", value = "当前页码",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10",name = "pageSize", value = "每页记录数",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "username", required = false,paramType = "query",value = "角色名称",dataType = "String",dataTypeClass = String.class),
    @ApiImplicitParam(name = "operation",required = false,paramType = "query",value = "操作行为",dataType = "string",dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<OperationLogEntity>> query(@ApiIgnore @RequestParam Map<String, Object> params){
        return operationLogService.query(params);
    }
}
