/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.bugull.hithiumfarmweb.common.exception;

import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 */
@RestControllerAdvice(basePackages = {"com.bugull.hithiumfarmweb.http.controller"})
public class ValidatorExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(ParamsValidateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResHelper<Void> handleRRException(ParamsValidateException e){
		logger.error(e.getMessage());
		return ResHelper.error( "参数错误");
	}
//    /**
//     * 处理无此Bean异常
//     */
//	@ExceptionHandler(NoSuchBeanDefinitionException.class)
//    public R hanlderNoSuchBeanDefinitionException(Exception e){
//	    logger.error(e.getMessage());
//	    return R.error();
//    }
//
//	@ExceptionHandler(NoHandlerFoundException.class)
//	public R handlerNoFoundException(Exception e) {
//		logger.error(e.getMessage(), e);
//		return R.error(404, "路径不存在，请检查路径是否正确");
//	}
//
//	@ExceptionHandler(DuplicateKeyException.class)
//	public R handleDuplicateKeyException(DuplicateKeyException e){
//		logger.error(e.getMessage(), e);
//		return R.error("数据库中已存在该记录");
//	}
//
	@ExceptionHandler({UnauthorizedException.class,AuthorizationException.class})
	public ResHelper<Void> handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage(), e);
		return ResHelper.error(HttpStatus.FORBIDDEN.value(),"没有权限，请联系管理员授权");
	}
//
	@ExceptionHandler(Exception.class)
	public ResHelper<Void> handleException(Exception e){
		logger.error(e.getMessage(),e);
		return ResHelper.error("系统繁忙");
	}
}
