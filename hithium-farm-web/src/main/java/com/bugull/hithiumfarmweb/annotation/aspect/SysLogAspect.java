package com.bugull.hithiumfarmweb.annotation.aspect;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.service.OperationLogService;
import com.bugull.hithiumfarmweb.utils.HttpContextUtils;
import com.bugull.hithiumfarmweb.utils.IPUtils;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class SysLogAspect {
    @Resource
    private OperationLogService operationLogService;

    @Pointcut("@annotation(com.bugull.hithiumfarmweb.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = new OperationLog();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            //注解上的描述
            operationLog.setOperation(syslog.value());
        }
//		//请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        operationLog.setMethod(className + "." + methodName + "()");
//		//请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = new Gson().toJson(args);
            operationLog.setParams(params);
        } catch (Exception e) {

        }
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        operationLog.setIp(IPUtils.getIpAddr(request));
        //用户名
        String username = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getUserName();
        operationLog.setUsername(username);
        operationLog.setTime(time);
        operationLog.setCreateDate(new Date());
        operationLogService.save(operationLog);
    }
}
