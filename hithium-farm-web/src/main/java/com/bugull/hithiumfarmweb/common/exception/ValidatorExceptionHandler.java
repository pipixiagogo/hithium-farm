
package com.bugull.hithiumfarmweb.common.exception;

import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.CorpResponse;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
	@ResponseStatus(HttpStatus.OK)
	public ResHelper<Void> handleRRException(ParamsValidateException e){
		logger.error(e.getMessage());
		return ResHelper.error("参数错误");
	}
	@ExceptionHandler({UnauthorizedException.class,AuthorizationException.class})
	public ResHelper<Void> handleAuthorizationException(AuthorizationException e){
		logger.error("没有响应权限",e);
		return ResHelper.error(HttpStatus.FORBIDDEN.value(),"没有权限，请联系管理员授权");
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public CorpResponse handleValidatorException(HttpMessageNotReadableException e){
		logger.error("参数错误",e);
		return CorpResponse.error(0, "参数错误");
	}

	@ExceptionHandler(Exception.class)
	public ResHelper<Void> handleException(Exception e){
		logger.error(e.getMessage(),e);
		return ResHelper.error("系统繁忙");
	}
}
