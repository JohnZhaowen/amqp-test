package com.john.framework.amqp.utils;

import com.john.framework.amqp.testcase.TestStatistics;

import java.util.Arrays;

public class StatisticsUtils {

    public static TestStatistics cal(int[] latencyInUs, int testCaseId) {
        TestStatistics statistics = new TestStatistics();
        statistics.setTestCaseId(testCaseId);

        Arrays.sort(latencyInUs);

        double avg = 0.0;
        int size = latencyInUs.length;
        int longLatencyCount = 0;

        for (Integer inUs : latencyInUs) {
            avg += inUs;
            if (inUs > 1000) {
                longLatencyCount++;
            }
        }

        avg /= size;
        double stdDev = MathUils.calStdDev(avg, latencyInUs);
        double min = latencyInUs[0];
        double max = latencyInUs[size - 1];
        double latency90Us = latencyInUs[(int) (size * 0.9)];
        double latency95Us = latencyInUs[(int) (size * 0.95)];
        double latency99Us = latencyInUs[(int) (size * 0.99)];
        double latency99_9Us = latencyInUs[(int) (size * 0.999)];

        statistics.setAvgUs(avg);
        statistics.setStdDev(stdDev);
        statistics.setLatency90Us(latency90Us);
        statistics.setLatency95Us(latency95Us);
        statistics.setLatency99Us(latency99Us);
        statistics.setLatency99_9Us(latency99_9Us);
        statistics.setMinUs(min);
        statistics.setMaxUs(max);
        statistics.setLongLactencyCount(longLatencyCount);

        return statistics;
    }
}
