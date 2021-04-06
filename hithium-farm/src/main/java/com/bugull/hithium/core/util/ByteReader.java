package com.bugull.hithium.core.util;

import java.util.Arrays;

public class ByteReader {
    private byte[] bytes;
    private int index;
    private int num;
    public ByteReader(byte[] bytes) {
        this.bytes = bytes;
        this.index = 0;
    }

    public byte[] read( int num ){
        if(  this.hasNext(num) ){
            return this.next();
        }else {
            throw new RuntimeException("字节不足");
        }
    }

    public byte read(){
        byte[] bs = this.read(1);
        return bs[0];
    }

    public boolean hasNext(int num){
        if( num <= 0 ){
            throw new RuntimeException("num 必须大于0");
        }
        if(  index + num <= bytes.length ){
            this.num = num;
            return true;
        }else{
            this.num = 0;
            return false;
        }
    }

    public byte[] next(){
       if( this.num == 0 ){
           throw new RuntimeException("必须先调用hasNext方法。");
       }
       byte[] bs = null;
       bs = Arrays.copyOfRange(this.bytes,this.index,index + num);
       this.index += num;
       return bs;
    }
    public byte[] readRemain(){
        int n = bytes.length - this.index;
        if( this.hasNext(n) ){
            return this.next();
        }else {
            throw new RuntimeException("字节不足");
        }
    }
}
