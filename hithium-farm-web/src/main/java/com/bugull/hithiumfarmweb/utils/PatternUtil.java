package com.bugull.hithiumfarmweb.utils;

import java.util.regex.Pattern;

import static com.bugull.hithiumfarmweb.common.Const.REGEX_MOBILE;

public class PatternUtil {
    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static String checkType(String fileType) {

        switch (fileType) {
            case "FFD8FF": return "jpg";
            case "89504E": return "png";
            case "474946": return "jif";
            default: return "0000";
        }
    }
}
