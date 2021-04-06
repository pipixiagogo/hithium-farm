package com.bugull.hithiumfarmweb.utils;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {
    private static final char[] chars;
    static {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i <= 9; i++ ){
            sb.append(i);
        }
        for( char i = 'a'; i <= 'z'; i++){
            sb.append(i);
        }
        for( char i = 'A'; i <= 'Z'; i++){
            sb.append(i);
        }
        chars = sb.toString().toCharArray();
    }

    public static String get32Str(){
        return UUID.randomUUID().toString().replace("-","");
    }
    public static String getNStr(int n){
        if( n < 0 ){
            throw new RuntimeException();
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int l = chars.length;
        for( int i = 0; i < n; i++ ){
            int index = random.nextInt(l);
            sb.append(chars[index]);
        }
        return sb.toString();
    }
}
