package com.john.framework.amqp.utils;

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

        int[] result = new int[batches];

        double avg = 0.0;

        int countInOneBatch = (int)Math.ceil((double)latencyInUs.length / batches);

        for (int batch = 0; batch < batches; batch++) {
            for (int i = batch * countInOneBatch; i < latencyInUs.length; i++) {
                avg += latencyInUs[i];
            }

            if (batch == batches - 1) {
                //最后一个batch可能数量不全
                int count = latencyInUs.length - countInOneBatch * (batches - 1);
                result[batch] = (int) (avg / count);
            } else {
                result[batch] = (int) (avg / countInOneBatch);
            }
            avg = 0;
        }

        return result;
    }

}
