package com.bugull.hithium.core.util;

public class MsgBuilder {
    private static final byte[] SOF = new byte[]{(byte) 0xbb, (byte) 0xcd};
    private static final int BID_LEN = 4;
    private static final int MAX_SERVICE_DATA_LEN = 0x0000ffff;
    public static final byte[] DEFAULT_BID = new byte[]{0x00,0x00,0x00,0x00};
    public static final byte DEFALUT_VERSION = 0x01;
    private byte[] bid;
    private byte[] version;
    private byte[] aid;
    private byte[] mid;
    private byte[] serviceDataLen;
    private byte[] data;

    public MsgBuilder setBid(byte[] bid) {
        if( bid.length != BID_LEN ){
            throw new RuntimeException("BID长度不符合要求。");
        }
        this.bid = bid;
        return this;
    }

    public MsgBuilder setVersion(byte version) {
        this.version = new byte[]{version};
        return this;
    }

    public MsgBuilder setAid(byte aid) {
        this.aid = new byte[]{(byte)aid};
        return this;
    }

    public MsgBuilder setMid(byte mid) {
        this.mid = new byte[]{(byte)mid};
        return this;
    }

    public MsgBuilder setServiceDataLen( int len ) {
        if( len > MAX_SERVICE_DATA_LEN || len < 0 ){
            throw new RuntimeException("serviceDataLen长度不能超过0x0000ffff。");
        }
        byte[] lenByte = ByteUtil.fromInt( len );
        byte[] dataLen = new byte[2];
        System.arraycopy(lenByte,2,dataLen,0,dataLen.length);
        this.serviceDataLen = dataLen;
        return this;
    }

    public MsgBuilder setData(byte[] data) {
        this.data = data;
        setServiceDataLen( data.length );
        return this;
    }

    public byte[] getBid() {
        return bid;
    }

    public byte getVersion() {
        return version[0];
    }

    public byte getAid() {
        return aid[0];
    }

    public byte getMid() {
        return mid[0];
    }

    public int getServiceDataLen() {
        return ByteUtil.toInt( new byte[]{0x00,0x00,serviceDataLen[0],serviceDataLen[1]} );
    }

    public byte[] getData() {
        return data;
    }

    public byte[] build(){
        byte[] source = new byte[]{};
        if( bid == null || version == null || aid == null || mid == null || serviceDataLen == null ){
            throw new RuntimeException("build error!");
        }
        return PlusArrayUtils.combine(SOF,bid,version,aid,mid,serviceDataLen,data);
    }
}
