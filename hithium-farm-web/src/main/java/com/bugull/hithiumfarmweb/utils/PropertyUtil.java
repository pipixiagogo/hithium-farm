package com.bugull.hithiumfarmweb.utils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

public class PropertyUtil {
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    public static final String CONFIG = "application.properties";
    public static final String CONFIG_SPLIT_TOKEN = ",";
    private static final Properties PROPERTIES = new Properties();
    public  static String getApplicationScenariosItemMsg(Integer applicationScenariosItem) {
        switch (applicationScenariosItem) {
            case 1:
                return "防逆流控制";
            case 2:
                return "削峰填谷";
            case 8:
                return "需量控制";
            case 16:
                return "AGC联合调频";
            case 32:
                return "需求响应";
            case 64:
                return "动态扩容";
            case 128:
                return "微网监控管理";
            case 256:
                return "备用电源";
            default:
                return "未知应用场景下策略";
        }
    }

    public static String getApplicationScenariosMsg(Integer applicationScenarios) {
        switch (applicationScenarios) {
            case 0:
                return "调峰";
            case 1:
                return "调频";
            case 2:
                return "风光储充";
            case 3:
                return "微网";
            case 4:
                return "备用电源";
            case 5:
                return "台区";
            default:
                return "未知应用场景";
        }
    }
//    static {
//        String configFile =.getProperty("configFile");
//        configFile = StringUtils.isEmpty(configFile) ? CONFIG : configFile;
//        Resource resource = resourceLoader.getResource(configFile);
//        try (InputStream is = resource.getInputStream()) {
//            PROPERTIES.load(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void init( String configFilePath ){
        InputStream in = null;
        try {
            if( !StringUtils.isEmpty(configFilePath) ){
                in = new FileInputStream(configFilePath);
            }else {
                in = resourceLoader.getResource(CONFIG).getInputStream();
            }
            if( in != null ){
                PROPERTIES.load(in);
            }else {
                throw new RuntimeException("配置文件未找到。");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if( in != null ){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        if (PROPERTIES.containsKey(key)) {
            return PROPERTIES.getProperty(key);
        }
        return "";
    }

    public static Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    public static boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

    public static Long getLong(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Long.valueOf(value);
    }

    public static String getProperty(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

    public static String[] getList(String key, String regex) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value.split(regex);
    }

    public static String[] getList(String key) {
        return getList(key, CONFIG_SPLIT_TOKEN);
    }

    private PropertyUtil(){}

}
