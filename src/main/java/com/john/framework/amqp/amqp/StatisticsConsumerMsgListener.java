package com.john.framework.amqp.amqp;

import com.john.framework.amqp.runner.TestCaseRunner;
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

/**
 * 该监听器用于统计延时信息
 */
public class StatisticsConsumerMsgListener extends KSKingMQSPI implements IMsgListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    private int totalCount;

    private int recvCount = 0;
    private int warmUpCount;

    private int[] latencyInUs;

    private volatile boolean connect = false;

    private volatile boolean subscribe = false;

    @Override
    public void OnConnected() {
        LOG.info("sub client connected to broker!");
        connect = true;
    }

    @Override
    public void OnDisconnected(ReConnectStatus reConnectStatus, ErrorInfo pErrorInfo) {
        LOG.warn("sub client disconnected to broker! error code:"+pErrorInfo.getErrorId()+
                ",error msg:"+pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnRtnSubscribe(String pQueue, ErrorInfo pErrorInfo) {
        LOG.info("sub client Subscribed success ,queue name:"+pQueue);
        if(pErrorInfo.getErrorId()==0){
            subscribe = true;
        }
    }

    @Override
    public void OnMessage(String routingKey, byte[] pMsgbuf, ErrorInfo pErrorInfo) {
        //什么都不做
        try {
            AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
            JavaStruct.unpack(packet, pMsgbuf);
            onMsg(packet);
        } catch (StructException e) {
            e.printStackTrace();
        }
    }

    public StatisticsConsumerMsgListener(TestCaseEnum testCaseEnum) {
        //最多接收到这么多消息，但是如果有过滤，就会少于这个量
        totalCount = testCaseEnum.msgSendRate * TestContents.TEST_TIME_IN_SECONDS;

        warmUpCount = testCaseEnum.msgSendRate * TestContents.WARNUP_TIME_IN_SECONDS;

        latencyInUs = new int[totalCount - warmUpCount];
        LOG.info("listener build for testCase: [{}], should send [{}] msgs.", testCaseEnum.testCaseId, totalCount);
    }

    public void onMsg(AmqpMessage msg) {

        if (msg.getEndMark()==1) {
            //所有消息已经接收完毕，则开始进行统计
            int[] recvLatencies = new int[recvCount];
            System.arraycopy(latencyInUs, 0, recvLatencies, 0, recvCount);
            latencyInUs = null;

            //统计数据，一个测试用例生产一个统计数据
            TestStatistics statistics = StatisticsUtils.cal(recvLatencies, msg.getTestCaseId());
            CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_STATISTICS_FILENAME, statistics.toStringArr());

            int[] rawLatencies = MathUils.split(recvLatencies, TestContents.LATENCY_RAW_BATCHES);
            for (int rawLatency : rawLatencies) {
                CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_RAW_FILENAME, new TestRawData(msg.getTestCaseId(), rawLatency).toStringArr());
            }

            LOG.info("testCase [{}] run finished, result: [{}]", msg.getTestCaseId(), statistics);
        } else if ((++recvCount) > warmUpCount) {
            long recvNanos = System.nanoTime();
            long sendNano = msg.getTimestampInNanos();

            int letencyUs = (int) ((recvNanos - sendNano) / 1000);
            latencyInUs[recvCount - 1] = letencyUs;
        }
    }

    @Override
    public boolean connect() {
        return connect;
    }

    @Override
    public boolean subscribe() {
        return subscribe;
    }
}
