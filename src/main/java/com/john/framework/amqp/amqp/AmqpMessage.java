package com.john.framework.amqp.amqp;

import com.kingstar.struct.StructClass;
import com.kingstar.struct.StructField;

import java.io.Serializable;

@StructClass
public class AmqpMessage implements Serializable {

    private static final long serialVersionUID = 8356611117456552686L;

    @StructField(order = 0)
    private byte sender;

    @StructField(order = 1)
    private long seq;

    @StructField(order = 2)
    private byte[] md5 =new byte[16];

    //1代表结束
    @StructField(order = 3)
    private byte endMark = 0;

    //在发送结束报文时 带上发送的总数量
    @StructField(order = 4)
    private int total = 0;

    @StructField(order = 5)
    private byte[] body;

    public AmqpMessage(int packetSize) {
        //long 算8位，md5算16
        this.body = new byte[packetSize-30];
    }

    public byte getSender() {
        return sender;
    }

    public void setSender(byte sender) {
        this.sender = sender;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public byte[] getMd5() {
        return md5;
    }

    public void setMd5(byte[] md5) {
        this.md5 = md5;
    }

    public byte getEndMark() {
        return endMark;
    }

    public void setEndMark(byte endMark) {
        this.endMark = endMark;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
