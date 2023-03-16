package com.john.framework.amqp.utils;

public class MathUtils {

    private MathUtils(){}

    public static double calStdDev(double mean,long[] delays){
        double stdDev = 0.0;
        for(long num:delays){
            stdDev += Math.pow(num-mean,2);
        }
        return Math.sqrt(stdDev/delays.length);
    }
}
