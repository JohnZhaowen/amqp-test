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
    private String md5;

    @StructField(order = 3)
    private byte[] body;

    public AmqpMessage(int packetSize) {
        this.body = new byte[packetSize];
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
