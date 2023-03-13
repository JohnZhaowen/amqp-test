package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.testcase.TestRawData;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.CsvUtils;
import com.john.framework.amqp.utils.MathUils;
import com.john.framework.amqp.utils.StatisticsUtils;
import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReConnectStatus;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * 该监听器用于统计延时信息
 */
public class StatisticsConsumerMsgListener extends KSKingMQSPI implements IMsgListener {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsConsumerMsgListener.class);

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

    //延迟打印时间线程
    private Thread latencyThread = null;


    private void init(String threadName) {
        if (init) {
            LOG.info("init LatencyDaemonThread repeat...");
            return;
        }
        this.startLatencyDaemonThread(threadName);
        init = true;
        LOG.info("LatencyDaemonThread init end...");
    }

    private void startLatencyDaemonThread(String threadName) {
        if (init) {
            return;
        }
        try {
            this.latencyThread = new Thread() {
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
        } catch (Exception e) {
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
        try {
            if (recvCount >= latencyInUsLength) return;
            long end = System.nanoTime();
            AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
            JavaStruct.unpack(packet, pMsgbuf);
            long start = packet.getTimestampInNanos();
            latencyInUs[recvCount++] = (int) ((end - start) / 1000);
            if (recvCount == latencyInUsLength - 1) stop_flag = 1;
        } catch (StructException e) {
            LOG.error("回调消息处理异常", e);
        }
    }

    public StatisticsConsumerMsgListener(TestCaseEnum testCaseEnum, Environment environment) {
        int testTime = Integer.parseInt(environment.getProperty("testTime", String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));
        int warmupTime = Integer.parseInt(environment.getProperty("warmupTime", String.valueOf(TestContents.WARMUP_TIME_IN_SECONDS)));


        this.testCaseEnum = testCaseEnum;
        init("LatencyDaemonThread");
        //最多接收到这么多消息，但是如果有过滤，就会少于这个量
        totalCount = testCaseEnum.msgSendRate * testTime;
        warmupCount = testCaseEnum.msgSendRate * warmupTime;
        latencyInUs = new int[totalCount];
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
