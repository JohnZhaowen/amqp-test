package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.testcase.TestRawData;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.CsvUtils;
import com.john.framework.amqp.utils.MathUils;
import com.john.framework.amqp.utils.MathUtils;
import com.john.framework.amqp.utils.StatisticsUtils;
import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReConnectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 该监听器用于统计延时信息
 */
public class StatisticsConsumerShortMsgListener extends KSKingMQSPI implements IMsgListener{

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsConsumerShortMsgListener.class);

    private int totalCount;

    private volatile int recvCount = 0;

    private int warmupCount;

    private volatile int[] latencyInUs;

    private int latencyInUsLength = 0;

    private volatile boolean connect = false;

    private volatile boolean subscribe = false;

    //初始化标识
    private volatile boolean init = false;

    volatile int stop_flag = 0;

    private TestCaseEnum testCaseEnum = null;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);

    //延迟打印时间线程
    private Thread latencyThread=null;

    volatile long[] rtt;
    volatile int rtt_count = 0;


    private void init(String threadName){
        if(init){
            LOG.info("init LatencyDaemonThread repeat...");
            return ;
        }
        this.startLatencyDaemonThread(threadName);
        init = true;
        LOG.info("LatencyDaemonThread init end...");
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
                        report();
                        //所有消息已经接收完毕，则开始进行统计
                        int[] recvLatencies = new int[recvCount- warmupCount];
                        System.arraycopy(latencyInUs, warmupCount, recvLatencies, 0, recvLatencies.length);
                        latencyInUs = null;
                        //统计数据，一个测试用例生产一个统计数据
                        TestStatistics statistics = StatisticsUtils.cal(recvLatencies, testCaseEnum.testCaseId, testCaseEnum.msgSendRate);
                        CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_STATISTICS_FILENAME, statistics.toStringArr());
                        int[] rawLatencies = MathUils.split(recvLatencies, TestContents.LATENCY_RAW_BATCHES);
                        for (int rawLatency : rawLatencies) {
                            CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_RAW_FILENAME,
                                    new TestRawData(testCaseEnum.testCaseId, testCaseEnum.msgSendRate, rawLatency).toStringArr());
                        }
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

    @Override
    public void OnConnected() {
        LOG.info("OnConnected callback, sub client connected to broker!");
        connect = true;
    }

    @Override
    public void OnDisconnected(ReConnectStatus reConnectStatus, ErrorInfo pErrorInfo) {
        LOG.warn("OnDisconnected callback, sub client disconnected to broker! error code:" + pErrorInfo.getErrorId() +
                ",error msg:" + pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnRtnSubscribe(String pQueue, ErrorInfo pErrorInfo) {
        LOG.info("OnRtnSubscribe callback, sub client Subscribed success ,queue name:" + pQueue);
        if (pErrorInfo.getErrorId() == 0) {
            subscribe = true;
        }
    }

    @Override
    public void OnMessage(String routingKey, byte[] pMsgbuf) {
        long end = System.nanoTime();
        if (recvCount >= latencyInUsLength) return;
        //byte[] unzip = Snappy.uncompress(pMsgbuf);
        //long end1 = System.nanoTime();
        //rtt[rtt_count++] = (end1-end)/1000;
        byteBuffer.put(pMsgbuf,0,8);
        byteBuffer.flip();
        long start = byteBuffer.getLong();
        byteBuffer.clear();
        int  latency = (int)((end - start) / 1000);
        latencyInUs[recvCount++] = latency;
        if (recvCount== latencyInUsLength-1 ) stop_flag = 1;
        long end1 = System.nanoTime();
        rtt[rtt_count++] = (end1-end)/1000;
        //unzip = null;
        pMsgbuf = null;

    }

    //根据平均速度计算
    private void report() {

        double avg=0.0;
        //copy 数组
        Arrays.sort(rtt, 0, rtt.length);

        int longLatencyCount = 0;
        int subLength = rtt.length;
        for(int i = 0; i < subLength; i++) {
            avg += rtt[i];
            if(rtt[i]>1000){
                longLatencyCount++;
            }
        }

        avg /= (double)subLength;

        double stdDev = MathUtils.calStdDev(avg,rtt);

        System.out.printf("java OnMsg:min = %d, max=%d avg=%.0f%n", rtt[0],rtt[subLength-1], avg);
        System.out.printf("999pcnt = %d%n", rtt[(int) (subLength * 0.999)]);
        System.out.printf("99pcnt = %d%n", rtt[(int) (subLength * 0.99)]);
        System.out.printf("95pcnt = %d%n", rtt[(int) (subLength * 0.95)]);
        System.out.printf("90pcnt = %d%n", rtt[(int) (subLength * 0.90)]);
        System.out.printf("50pcnt = %d%n", rtt[(int) (subLength * 0.50)]);

        System.out.println("stdDev ="+stdDev);
        System.out.println("longLatencyCount="+longLatencyCount);
    }

    public StatisticsConsumerShortMsgListener(TestCaseEnum testCaseEnum, Environment environment) {
        int testTime = Integer.parseInt(environment.getProperty("testTime", String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));
        int warmupTime = Integer.parseInt(environment.getProperty("warmupTime", String.valueOf(TestContents.WARMUP_TIME_IN_SECONDS)));


        this.testCaseEnum = testCaseEnum;
        init("LatencyDaemonThread");
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

    public void onMsg(AmqpMessage msg) {

    }

    public boolean connect() {
        return connect;
    }

    public boolean subscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }
}
