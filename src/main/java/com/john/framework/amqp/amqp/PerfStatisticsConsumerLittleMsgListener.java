package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.testcase.TestRawData;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.CsvUtils;
import com.john.framework.amqp.utils.EnvironmentUtils;
import com.john.framework.amqp.utils.MathUils;
import com.john.framework.amqp.utils.MathUtils;
import com.john.framework.amqp.utils.StatisticsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 该监听器用于统计延时信息
 */
public class PerfStatisticsConsumerLittleMsgListener implements IMsgListener{

    private static final Logger LOG = LoggerFactory.getLogger(PerfStatisticsConsumerLittleMsgListener.class);

    private int totalCount;

    private volatile int recvCount = 0;

    private int warmupCount;

    private volatile int[] latencyInUs;

    private int latencyInUsLength = 0;

    //初始化标识
    private volatile boolean init = false;

    volatile int stop_flag = 0;

    private TestCaseEnum testCaseEnum = null;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);

    //延迟打印时间线程
    private Thread latencyThread=null;

    volatile long[] rtt;

    private void init(String threadName){
        if(init){
            LOG.info("init LatencyDaemonThread repeat...");
            return ;
        }
        this.startLatencyDaemonThread(threadName);
        init = true;
        LOG.info("PerfLittleThread init end...");
    }

    private void startLatencyDaemonThread(String threadName){
        if(init){
            return ;
        }
        try {
            this.latencyThread = new Thread(){
                @Override
                public void run() {
                    //计算时延
                    try {
                        Thread.sleep(1000);
                        while (stop_flag == 0){
                            if(recvCount==0){
                                LOG.info("current Latency [{}]us,current finish: [{}/{}]",latencyInUs[recvCount], recvCount, totalCount);
                            }else{
                                LOG.info("current Latency [{}]us,current finish: [{}/{}]",latencyInUs[recvCount-1], recvCount, totalCount);
                            }
                            Thread.sleep(1000);
                        }
                        LOG.info("receive finished, receive total:[{}], send count:[{}],start Statistics", recvCount, totalCount);
                        //report();
                        //所有消息已经接收完毕，则开始进行统计
                        int[] recvLatencies = new int[recvCount- warmupCount];
                        System.arraycopy(latencyInUs, warmupCount, recvLatencies, 0, recvLatencies.length);
                        latencyInUs = null;
                        //统计数据，一个测试用例生产一个统计数据
                        int[] rawLatencies = MathUils.split(recvLatencies, TestContents.LATENCY_RAW_BATCHES);
                        for (int rawLatency : rawLatencies) {
                            CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_RAW_FILENAME,
                                    new TestRawData(testCaseEnum.testCaseId, testCaseEnum.msgSendRate, rawLatency).toStringArr());
                        }
                        TestStatistics statistics = StatisticsUtils.cal(recvLatencies, testCaseEnum.testCaseId, testCaseEnum.msgSendRate);
                        CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_STATISTICS_FILENAME, statistics.toStringArr());
                        LOG.info("testCase [{}] run finished, result: [{}]", testCaseEnum.testCaseId, statistics);
                    }catch (Exception e){
                        LOG.error("Statistics exception", e);
                    }
                }
            };
            this.latencyThread.setName(threadName);
            this.latencyThread.setDaemon(true);
            this.latencyThread.start();
        }catch (Exception e){
            LOG.error("", e);
        }
    }


    public PerfStatisticsConsumerLittleMsgListener(TestCaseEnum testCaseEnum) {
        int testTime = EnvironmentUtils.getTestTime();
        int warmupTime = EnvironmentUtils.getWarmupTime();
        this.testCaseEnum = testCaseEnum;
        init("PerfLittleThread");
        LOG.info("测试用例详情:{}",testCaseEnum.toString());
        //最多接收到这么多消息，但是如果有过滤，就会少于这个量
        totalCount = testCaseEnum.msgSendRate * testTime;
        warmupCount = testCaseEnum.msgSendRate * warmupTime;
        latencyInUs = new int[totalCount];
        rtt = new long[totalCount];
        latencyInUsLength = latencyInUs.length;
        LOG.info("listener build for testCase: [{}], should send [{}] packets.",
                testCaseEnum.testCaseId, totalCount);
    }

    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        long end = System.nanoTime();
        recvCount++;
        if (recvCount > latencyInUsLength) return;
        byteBuffer.put(pMsgbuf,0,8);
        byteBuffer.flip();
        long start = byteBuffer.getLong();
        byteBuffer.clear();
        int  latency = (int)((end - start) / 1000);
        latencyInUs[recvCount-1] = latency;
        if (recvCount== latencyInUsLength) stop_flag = 1;
    }
}
