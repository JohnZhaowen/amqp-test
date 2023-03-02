package com.john.framework.amqp.testcase;


import java.text.DecimalFormat;

/**
 * 测试后的统计数据
 */
public class TestStatistics {

    private int testCaseId;

    private double avgUs;
    private double maxUs;
    private double minUs;
    private double stdDev;
    private double latency90Us;
    private double latency95Us;
    private double latency99Us;
    private double latency99_9Us;

    private int longLactencyCount;

    public String[] toStringArr(){

        DecimalFormat df = new DecimalFormat("0.00");

        String[] s = new String[10];
        s[0] = String.valueOf(this.testCaseId);
        s[1] = df.format(this.avgUs);
        s[2] = df.format(this.maxUs);
        s[3] = df.format(this.minUs);
        s[4] = df.format(this.stdDev);
        s[5] = df.format(this.latency90Us);
        s[6] = df.format(this.latency95Us);
        s[7] = df.format(this.latency99Us);
        s[8] = df.format(this.latency99_9Us);
        s[9] = String.valueOf(this.longLactencyCount);
        return s;
    }

    @Override
    public String toString(){

        DecimalFormat df = new DecimalFormat("0.00");

        StringBuilder sb = new StringBuilder();
        return sb.append("testCaseId: ").append(this.testCaseId).append("\n")
                .append(", avgUs: ").append(df.format(this.avgUs)).append("\n")
                .append(", maxUs: ").append(df.format(maxUs)).append("\n")
                .append(", minUs: ").append(df.format(this.minUs)).append("\n")
                .append(", stdDev: ").append(df.format(stdDev)).append("\n")
                .append(", latency90Us: ").append(df.format(this.latency90Us)).append("\n")
                .append(", latency95Us: ").append(df.format(latency95Us)).append("\n")
                .append(", latency99Us: ").append(df.format(latency99Us)).append("\n")
                .append(", latency99_9Us: ").append(df.format(latency99_9Us)).append("\n")
                .append(", longLactencyCount: ").append(this.longLactencyCount).append("\n")
                .toString();
    }


    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public double getAvgUs() {
        return avgUs;
    }

    public void setAvgUs(double avgUs) {
        this.avgUs = avgUs;
    }

    public double getMaxUs() {
        return maxUs;
    }

    public void setMaxUs(double maxUs) {
        this.maxUs = maxUs;
    }

    public double getMinUs() {
        return minUs;
    }

    public void setMinUs(double minUs) {
        this.minUs = minUs;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getLatency90Us() {
        return latency90Us;
    }

    public void setLatency90Us(double latency90Us) {
        this.latency90Us = latency90Us;
    }

    public double getLatency95Us() {
        return latency95Us;
    }

    public void setLatency95Us(double latency95Us) {
        this.latency95Us = latency95Us;
    }

    public double getLatency99Us() {
        return latency99Us;
    }

    public void setLatency99Us(double latency99Us) {
        this.latency99Us = latency99Us;
    }

    public double getLatency99_9Us() {
        return latency99_9Us;
    }

    public void setLatency99_9Us(double latency99_9Us) {
        this.latency99_9Us = latency99_9Us;
    }

    public int getLongLactencyCount() {
        return longLactencyCount;
    }

    public void setLongLactencyCount(int longLactencyCount) {
        this.longLactencyCount = longLactencyCount;
    }
}
