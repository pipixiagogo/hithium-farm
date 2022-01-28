package com.bugull.hithium.core.util;

import java.util.Locale;

public class ClassUtil {
    public static String getClassLowerName(Class clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT);
    }
}
