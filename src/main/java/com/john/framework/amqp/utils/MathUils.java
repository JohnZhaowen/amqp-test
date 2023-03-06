package com.john.framework.amqp.utils;

import java.util.Arrays;

public class MathUils {

    public static double calStdDev(double mean, int[] latencyInUs) {

        double stdDev = 0.0;

        for (double latency : latencyInUs) {
            stdDev += Math.pow(latency - mean, 2);
        }
        return Math.sqrt(stdDev / latencyInUs.length);
    }

    /**
     * 将延时数据分成batches组，每组取均值
     *
     * @param latencyInUs
     * @param batches
     * @return
     */
    public static int[] split(int[] latencyInUs, int batches) {

        if (latencyInUs.length <= batches) {
            return latencyInUs;
        }

        int[] result = new int[batches];

        double avg = 0.0;

        int countInOneBatch = latencyInUs.length / batches;

        for (int batch = 0; batch < batches; batch++) {
            if (batch == batches - 1) {
                //最后一个batch数量可能会更多
                int count = latencyInUs.length - countInOneBatch * (batches - 1);
                for (int i = batch * countInOneBatch; i < latencyInUs.length; i++) {
                    avg += latencyInUs[i];
                }
                result[batch] = (int) (avg / count);

            } else {
                for (int i = batch * countInOneBatch; i < batch * countInOneBatch + countInOneBatch && i < latencyInUs.length; i++) {
                    avg += latencyInUs[i];
                }

                result[batch] = (int) (avg / countInOneBatch);
            }
            avg = 0;
        }

        return result;
    }

    public static void main(String[] args) {

//        int[] latencyInUs = {1, 2, 3, 4, 5};
//
//        int[] split = split(latencyInUs, 6);
//        System.out.println(Arrays.toString(split));

        int[] latencyInUs = {1, 2, 3, 4, 5, 6};

        int[] split = split(latencyInUs, 7);
        System.out.println(Arrays.toString(split));




    }

}
