package com.bugull.hithium.core.util;

import org.apache.commons.lang3.ArrayUtils;

public class PlusArrayUtils extends ArrayUtils {

    public static byte[] combine( byte[] bs, byte[]... bytes ){
        if( bs == null ){
            bs = new byte[]{};
        }
        if( bytes != null && bytes.length > 0 ){
            for( byte[] _bs : bytes ){
                bs = addAll(bs,_bs);
            }
        }
        return bs;
    }
}
