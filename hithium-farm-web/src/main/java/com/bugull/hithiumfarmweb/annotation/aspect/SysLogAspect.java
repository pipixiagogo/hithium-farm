package com.bugull.hithiumfarmweb.annotation.aspect;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
//import com.bugull.hithiumfarmweb.http.service.ExcelExportService;
import com.bugull.hithiumfarmweb.http.service.OperationLogService;
import com.bugull.hithiumfarmweb.utils.HttpContextUtils;
import com.bugull.hithiumfarmweb.utils.IPUtils;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.DEVICE_NAME;
import static com.bugull.hithiumfarmweb.common.Const.EXCEL_METHOD;

@Aspect
@Component
public class SysLogAspect {
    @Resource
    private OperationLogService operationLogService;
    @Resource
    private DeviceDao deviceDao;
//    @Resource
//    private ExcelExportService excelExportService;

    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);

    @Pointcut("@annotation(com.bugull.hithiumfarmweb.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();

        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = new OperationLog();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            operationLog.setOperation(syslog.value());
        }
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        operationLog.setMethod(className + "." + methodName + "()");
        Object[] args = joinPoint.getArgs();
        String params = "";
        try {
            if (!StringUtils.isEmpty(operationLog.getOperation()) && operationLog.getOperation().contains("导出")) {
                List<Object> collect = Arrays.stream(args).skip(2).collect(Collectors.toList());
                if (operationLog.getOperation().equals(EXCEL_METHOD)) {
                    Device device =  deviceDao.findDevice(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(collect.get(0))));
//                    operationLog.setOperation("导出设备:" + device.getName() + ",时间" + collect.get(1) + "的" +excelExportService.getMsgByType(Integer.parseInt(collect.get(3).toString())) + "数据");
                }
                params = new Gson().toJson(collect);
            } else {
                params = new Gson().toJson(args);
            }
            operationLog.setParams(params);
        } catch (Exception e) {
            log.error("AOP转换字符串错误:{}", e.getMessage());
        }
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        operationLog.setIp(IPUtils.getIpAddr(request));
        //用户名
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        String username = sysUser.getUserName();
        operationLog.setUsername(username);
        operationLog.setTime(time);
        operationLog.setCreateDate(new Date());
        operationLogService.save(operationLog);
    }
}
