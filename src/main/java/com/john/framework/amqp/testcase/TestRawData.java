package com.john.framework.amqp.testcase;

import java.text.DecimalFormat;

public class TestRawData {

    private int testCaseId;

    private int msgSendRate;

    private double latency;

    public TestRawData(int testCaseId, int msgSendRate, double latency) {
        this.testCaseId = testCaseId;
        this.msgSendRate = msgSendRate;
        this.latency = latency;
    }

    public String[] toStringArr(){

        DecimalFormat df = new DecimalFormat("0.00");

        String[] s = new String[3];
        s[0] = String.valueOf(this.testCaseId);
        s[1] = String.valueOf(this.msgSendRate);
        s[2] = df.format(this.latency);
        return s;
    }

    @Override
    public String toString(){

        DecimalFormat df = new DecimalFormat("0.00");

        StringBuilder sb = new StringBuilder();
        return sb.append("testCaseId: ").append(this.testCaseId).append(", \n")
                .append("msgSendRate: ").append(df.format(this.msgSendRate))
                .append("latency: ").append(df.format(this.latency))
                .toString();
    }

    public static void main(String[] args) {
        TestRawData d = new TestRawData(10, 12000, 19387.108763);
//        System.out.println(d);

        for (String s : d.toStringArr()) {
            System.out.println(s);
        }
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getMsgSendRate() {
        return msgSendRate;
    }

    public void setMsgSendRate(int msgSendRate) {
        this.msgSendRate = msgSendRate;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }


}
