package com.john.framework.amqp.amqp;

import com.kingstar.struct.StructClass;
import com.kingstar.struct.StructField;

import java.io.Serializable;

@StructClass
public class AmqpMessage implements Serializable {

    private static final long serialVersionUID = 8356611117456552686L;

    @StructField(order = 0)
    private int testCaseId;

    @StructField(order = 1)
    private long timestampInNanos;

    @StructField(order = 2)
    private short endMark;

    @StructField(order = 3)
    private byte[] body;

    public AmqpMessage(int packetSize) {
        this.body = new byte[packetSize - 14];
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public long getTimestampInNanos() {
        return timestampInNanos;
    }

    public void setTimestampInNanos(long timestampInNanos) {
        this.timestampInNanos = timestampInNanos;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public short getEndMark() {
        return endMark;
    }

    public void setEndMark(short endMark) {
        this.endMark = endMark;
    }
}
