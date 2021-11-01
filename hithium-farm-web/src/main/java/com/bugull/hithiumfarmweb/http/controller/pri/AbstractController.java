/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Controller公共组件
 *
 */
public abstract class AbstractController {


	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUser getUser() {
		return (SysUser) SecurityUtils.getSubject().getPrincipal();
	}

	protected String getUserId() {
		return getUser().getId();
	}

	protected String getUserName(){
	    return getUser().getUserName();
    }
}
