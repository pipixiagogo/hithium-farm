package com.bugull.hithiumfarmweb;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.utils.UUIDUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;

import java.util.UUID;

public class HithiumFarmWebApplicationTests {

    @Test
    public void contextLoads() {
        String salt = RandomStringUtils.randomAlphanumeric(20);
        String s = new Sha256Hash("123456", salt).toHex();
        System.out.println(salt);
        System.out.println(s);
    }
    @Test
    public void createUUID(){
        String str = UUIDUtil.get32Str();
        System.out.println(str);

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("DATATYPE", "REAL_TIME_DATA");
//        jsonObject.put("PROJECTTYPE", "KC_ESS");
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject.put("DATA", jsonObject1);
//        String s = UUID.randomUUID().toString();
//        jsonObject1.put("pipixia", s);
//        System.out.println(jsonObject.toString());
    }

    @Test
    public void createDeviceInfo(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", DataType.DEVICE_LIST_DATA);
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        jsonObject1.put("pipixia2222222222999", s);

        System.out.println(jsonObject);
    }

    @Test
    public void testBreakDownLog(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", DataType.BREAKDOWNLOG_DATA);
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONArray jsonObject1 = new JSONArray();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        JSONObject jsonObject2 = new JSONObject();

        jsonObject1.add(jsonObject2);
        for(int i=10;i<20;i++){
            jsonObject2.put("pipi"+i,"xia"+i);
        }
        System.out.println(jsonObject);
    }

}
