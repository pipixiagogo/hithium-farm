package com.bugull.hithiumfarmweb.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.bugull.hithiumfarmweb.common.Const.COUNTRY;


@Component
@EnableConfigurationProperties
public class PropertiesConfig {

    public static final String MONGO_HOST = "mongo.host";
    public static final String MONGO_PORT = "mongo.port";

    public static final String MONGO_DB = "mongo.database";
    public static final String MONGO_USERNAME = "mongo.username";
    public static final String MONGO_PASSWORD = "mongo.password";

    public static final String CITYS = "city.json";
    private Map<String, String> citys = null;
    private Map<String, Set<String>> proToCitys = null;

    private Object lock=new Object();
    @Value("${mqtt.clientid.subscriber}")
    private String mqttClientIdSub;

    @Value("${mqtt.complete.timeout}")
    private int completeTimeout;

    @Value("${token.expire.time}")
    private int tokenExpireTime;

    @Value("${mqtt.username}")
    private String mqttUsername;

    @Value("${mqtt.password}")
    private String mqttPassword;

    @Value("${mqtt.server}")
    private String mqttServer;

    @Value("${mqtt.inflight}")
    private Integer inflight;

    @Value("${mqtt.clientid.publisher}")
    private String mqttClientIdPub;

    public Integer getCompleteTimeout() {
        return completeTimeout;
    }

    public int getTokenExpireTime() {
        return tokenExpireTime;
    }

    public String getMqttUsername() {
        return mqttUsername;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public String getMqttServer() {
        return mqttServer;
    }

    public Integer getInflight() {
        return inflight;
    }

    public String getMqttClientIdPub() {
        return mqttClientIdPub;
    }
    public Map<String, Set<String>> getProToCitys() {
        getCitys();
        return proToCitys;
    }
    public Map<String, String> getCitys() {
        if (citys == null) {
            synchronized (lock) {
                if (citys == null) {
                    citys = new HashMap<>();
                    proToCitys = new HashMap<>();
                    InputStream in = JSONObject.class.getClassLoader().getResourceAsStream(CITYS);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    StringBuilder sb = new StringBuilder();
                    reader.lines().forEach((s) -> {
                        sb.append(s);
                    });
                    JSONObject jsonObject = JSON.parseObject(sb.toString());
                    Set<Map.Entry<String, Object>> items = jsonObject.entrySet();
                    for (Map.Entry<String, Object> entry : items) {
                        JSONArray arr = (JSONArray) entry.getValue();
                        for (Object obj : arr) {
                            JSONObject o = (JSONObject) obj;
                            String cityName = o.getString("name");
                            String province = o.getString("province");
                            citys.put(cityName, province);
                            Set<String> cs = proToCitys.get(province);
                            if (cs == null) {
                                Set<String> set = new HashSet<>();
                                proToCitys.put(province, set);
                                set.add(cityName);
                            } else {
                                cs.add(cityName);
                            }
                        }
                    }
                }
            }
        }
        return citys;
    }

    public boolean verify(Map<String, Object> params) {
        /**
         * TODO 国家判断
         */
        String country = (String) params.get("country");
//        if (!StringUtils.isEmpty(country) && !country.equals(COUNTRY)) {
//            return false;
//        }
        String province = (String) params.get("province");
        String city = (String) params.get("city");
        if (StringUtils.isEmpty(province) && !StringUtils.isEmpty(city)) {
            return false;
        }
        if (!StringUtils.isEmpty(province) && !getProToCitys().containsKey(province)) {
            return false;
        }

        if (!StringUtils.isEmpty(city) && !getCitys().containsKey(city) && !getCitys().get(city).equals(province)) {
            return false;
        }
        if(!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city)){
            if(!getProToCitys().get(province).contains(city) || !getCitys().get(city).equals(province)){
                return false;
            }
        }
        return true;
    }
}
