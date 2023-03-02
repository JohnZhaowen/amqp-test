package com.john.framework.amqp.utils;

import java.util.List;

public class MathUils {

    public static double calStdDev(double mean, List<Integer> latencyInUs) {

        double stdDev = 0.0;

        for(double latency : latencyInUs){
            stdDev += Math.pow(latency - mean, 2);
        }
        return Math.sqrt(stdDev / latencyInUs.size());
    }
}
