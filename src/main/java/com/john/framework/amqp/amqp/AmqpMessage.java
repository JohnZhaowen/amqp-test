package com.john.framework.amqp.amqp;

public class AmqpMessage {

    private int testCaseId;

    private long timestampInNanos;

    private String routingKey;

    private boolean finished;

    private byte[] body;

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

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
