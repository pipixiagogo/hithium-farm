package com.bugull.hithiumfarmweb;


import com.bugull.hithiumfarmweb.utils.UUIDUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;

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
    }

}
