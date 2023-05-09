package com.john.framework.amqp.amqp;

import com.john.framework.amqp.utils.MD5Utils;
import com.kingstar.struct.StructClass;

import java.io.Serializable;

@StructClass
public class AmqpMessage1 extends AmqpMessage implements Serializable {

    private byte sender;

    private int seq;

    private String md5;

    public AmqpMessage1(int packetSize) {
        super(packetSize);
    }

    public byte getSender() {
        return sender;
    }

    public void setSender(byte sender) {
        this.sender = sender;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setMd5() {
        this.md5 = MD5Utils.md5(getBody());
    }

    public String getMd5() {
        return this.md5;
    }

}
