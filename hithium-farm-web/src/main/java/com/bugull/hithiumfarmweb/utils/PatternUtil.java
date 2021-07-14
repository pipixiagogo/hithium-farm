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
}
