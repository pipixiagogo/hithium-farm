package com.bugull.hithiumfarmweb.utils;


import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.mongo.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import static com.bugull.hithiumfarmweb.common.Const.DESC;
import static com.bugull.hithiumfarmweb.common.Const.PARAM_ILL;

public class ResHelper<T> {
    private static final Logger log = LoggerFactory.getLogger(ResHelper.class);
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    //1:成功
    //0：失败
    @ApiModelProperty(value = "1:成功 0:失败")
    private int code;

    @ApiModelProperty(value = "异常信息")
    private String msg;

    @ApiModelProperty(value = "数据内容")
    private T data;

    private boolean success;

    public static ResHelper pamIll() {
        return ResHelper.error(PARAM_ILL);
    }


    public static boolean validatePhone(String phone){
        String regex = "^1([3-8]){1}\\d{9}$";
        if(!validateBlank(phone)){
            return Pattern.compile(regex).matcher(phone).matches();
        }else{
            return false;
        }
    }
    public static boolean validateBlank(String src){
        return (src == null) || src.trim().equals("") ;
    }

    public static void errorHTML(HttpServletResponse response, String errorCode) {
        response.setContentType("text/html; charset=UTF-8");
        Writer writer = null;
        try {
            writer = response.getWriter();
            writer.write(errorCode);
        } catch (Exception e) {
            log.error("响应数据写出异常", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error("关闭响应输出流异常", e);
                }
            }
        }
    }

    public boolean isSuccess() {
        return SUCCESS == code || data != null;
    }

    private ResHelper() {
    }

    public static <S> ResHelper<S> success(String msg, S s) {
        ResHelper<S> helper = new ResHelper();
        helper.setCode(SUCCESS);
        helper.setData(s);
        helper.setMsg(msg);
        return helper;
    }

    public static ResHelper success(String msg) {
        ResHelper helper = new ResHelper();
        helper.setCode(SUCCESS);
        helper.setMsg(msg);
        return helper;
    }

    public static ResHelper error(String msg) {
        ResHelper helper = new ResHelper();
        helper.setCode(ERROR);
        helper.setMsg(msg);
        return helper;
    }

    public static ResHelper error(Integer code,String msg){
        ResHelper helper = new ResHelper();
        helper.setCode(code);
        helper.setMsg(msg);
        return helper;
    }


    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static boolean checkOrder(Integer order, String orderField, String table) {
        if (StringUtil.isEmpty(orderField) && order == null) {
            return true;
        }
        if (StringUtil.isEmpty(orderField) && order != null) {
            return false;
        }
        if (!StringUtil.isEmpty(orderField) && order == null) {
            return false;
        }
        if (!DESC.equals(order) && !Const.ASC.equals(order)) {
            return false;
        }
        if (!Const.SORT_FIELDS.get(table).contains(orderField)) {
            return false;
        }
        return true;
    }

}
