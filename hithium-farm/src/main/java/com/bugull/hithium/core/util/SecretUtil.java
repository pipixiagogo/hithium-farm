package com.bugull.hithium.core.util;

import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 加解密工具
 * */
public class SecretUtil {

    private static int offset = 16;
    private static String transformation = "AES/CBC/PKCS5Padding";
    private static String algorithm = "AES";
    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param key
     *            加密时的密码
     * @return 明文
     */
    public static byte[] decryptWithoutBase64(byte[] content, byte[] key) {
        try {
            if( key.length != 16 ){
                throw new RuntimeException();
            }
            SecretKeySpec skey = new SecretKeySpec(key, algorithm);
            IvParameterSpec iv = new IvParameterSpec(key);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static byte[] encryptWithoutBase64(String content, byte[] password) {
        try {
            if( password == null || password.length != 16 )
                throw new RuntimeException("password error");

            SecretKeySpec key = new SecretKeySpec(password, algorithm);
            IvParameterSpec iv = new IvParameterSpec(password);
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key,iv);
            byte[] result = cipher.doFinal(byteContent);// 加密
            return result;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptWithBase64(String content,String password){
        byte[] bs = encryptWithoutBase64(content,ByteUtil.parseHexStringToArray( password ));
        if( bs != null && bs.length > 0 ){
            return Base64Utils.encodeToString( bs );
        }
        return null;
    }

    public static String decryptWithBase64(String content,String password){
        byte[] bs = Base64Utils.decodeFromString( content );
        byte[] ubs = decryptWithoutBase64(bs,ByteUtil.parseHexStringToArray( password ));
        if( ubs != null && ubs.length > 0 ){
            try {
                return new String(ubs,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
