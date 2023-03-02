package com.john.framework.amqp.collectors;

import com.john.framework.amqp.testcase.TestRawData;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.CsvUtils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TestResultCollector {

    private BlockingQueue<TestStatistics> statisticsQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<TestRawData> rawDataQueue = new LinkedBlockingQueue<>();

    public void addStatistics(TestStatistics statistics) {
        try {
            statisticsQueue.put(statistics);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStatisticsToCsv(String fileName) {
        List<String[]> datas = statisticsQueue.stream().map(e -> e.toStringArr()).collect(Collectors.toList());
        CsvUtils.writeCsvWithMultiLines(fileName, datas);

        statisticsQueue.clear();
    }

    public void addRawData(TestRawData rawData) {
        try {
            rawDataQueue.put(rawData);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeRawDataToCsv(String fileName) {
        List<String[]> datas = rawDataQueue.stream().map(e -> e.toStringArr()).collect(Collectors.toList());
        CsvUtils.writeCsvWithMultiLines(fileName, datas);

        rawDataQueue.clear();
    }
}
