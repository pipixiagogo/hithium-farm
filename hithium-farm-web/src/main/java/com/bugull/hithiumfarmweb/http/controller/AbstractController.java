
package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.http.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller公共组件
 *
 */
public abstract class AbstractController {


	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUser getUser() {
		return (SysUser) SecurityUtils.getSubject().getPrincipal();
	}

	protected Long getUserId() {
		return getUser().getId();
	}

	protected String getUserName(){
	    return getUser().getUserName();
    }
}
