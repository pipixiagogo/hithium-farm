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

//    static {
//        String configFile = System.getProperty("configFile");
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
