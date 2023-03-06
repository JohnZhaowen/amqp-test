package com.john.framework.amqp.testcase;

import java.text.DecimalFormat;

public class TestRawData {

    private int testCaseId;

    private double latency;

    public TestRawData(int testCaseId, double latency) {
        this.testCaseId = testCaseId;
        this.latency = latency;
    }

    public String[] toStringArr(){

        DecimalFormat df = new DecimalFormat("0.00");

        String[] s = new String[2];
        s[0] = String.valueOf(this.testCaseId);
        s[1] = df.format(this.latency);
        return s;
    }

    @Override
    public String toString(){

        DecimalFormat df = new DecimalFormat("0.00");

        StringBuilder sb = new StringBuilder();
        return sb.append("testCaseId: ").append(this.testCaseId).append(", \n")
                .append("latency: ").append(df.format(this.latency))
                .toString();
    }

    public static void main(String[] args) {
        TestRawData d = new TestRawData(10, 19387.108763);
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

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }


}
