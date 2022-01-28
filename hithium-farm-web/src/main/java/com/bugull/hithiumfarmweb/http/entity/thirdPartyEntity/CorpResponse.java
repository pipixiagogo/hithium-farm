package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import lombok.ToString;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ToString
public class CorpResponse extends HashMap<String,Object> {

    private static final long serialVersionUID = 1L;

    public CorpResponse setData(Object data) {
        put("data",data);
        return this;
    }

    public static CorpResponse ok(String msg) {
        CorpResponse r = new CorpResponse();
        r.put("msg", msg);
        return r;
    }

    public static CorpResponse ok(Map<String, Object> map) {
        CorpResponse r = new CorpResponse();
        r.putAll(map);
        return r;
    }

    public static CorpResponse error(int code, String msg) {
        CorpResponse r = new CorpResponse();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static CorpResponse error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }


    //利用fastjson进行反序列化
    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data");	//默认是map
        String jsonString = JSON.toJSONString(data);
        T t = JSON.parseObject(jsonString, typeReference);
        return t;
    }

    //利用fastjson进行反序列化
    public <T> T getData(String key,TypeReference<T> typeReference) {
        Object data = get(key);	//默认是map
        String jsonString = JSON.toJSONString(data);
        T t = JSON.parseObject(jsonString, typeReference);
        return t;
    }

    public CorpResponse() {
        put("code", 1);
        put("msg", "success");
    }
    public CorpResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode() {

        return (Integer) this.get("code");
    }



}
