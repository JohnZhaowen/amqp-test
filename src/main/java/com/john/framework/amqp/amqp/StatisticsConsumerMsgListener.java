package com.john.framework.amqp.amqp;

import com.john.framework.amqp.collectors.TestCaseRunner;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.testcase.TestRawData;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.CsvUtils;
import com.john.framework.amqp.utils.MathUils;
import com.john.framework.amqp.utils.StatisticsUtils;
import com.kingstar.messaging.api.KSKingMQSPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该监听器用于统计延时信息
 */
public class StatisticsConsumerMsgListener extends KSKingMQSPI implements IMsgListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    private int totalCount;

    private int recvCount = 0;
    private int warnUpCount;

    private int[] latencyInUs;

    public StatisticsConsumerMsgListener(TestCaseEnum testCaseEnum) {
        totalCount = testCaseEnum.msgSendRate * TestContents.TEST_TIME_IN_SECONDS;
        warnUpCount = testCaseEnum.msgSendRate * TestContents.WARNUP_TIME_IN_SECONDS;

        latencyInUs = new int[totalCount - warnUpCount];
        LOG.info("listener build for testCase: [{}], should send [{}] msgs.", testCaseEnum.testCaseId, totalCount);
    }

    public void onMsg(AmqpMessage msg) {

        //所有消息已经接收完毕，则开始进行统计
        if ((++recvCount) >= totalCount) {
            TestStatistics statistics = StatisticsUtils.cal(latencyInUs, msg.getTestCaseId());
            CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_STATISTICS_FILENAME, statistics.toStringArr());

            int[] rawLatencies = MathUils.calAvg(latencyInUs, TestContents.LATENCY_RAW_BATCHES);
            for (int rawLatency : rawLatencies) {
                CsvUtils.writeCsvWithOneLine(TestContents.LATENCY_RAW_FILENAME, new TestRawData(msg.getTestCaseId(), rawLatency).toStringArr());
            }

            LOG.info("testCase [{}] run finished, result: [{}]", msg.getTestCaseId(), statistics);
        } else if ((++recvCount) > warnUpCount) {
            long recvNanos = System.nanoTime();
            long sendNano = msg.getTimestampInNanos();

            int letencyUs = (int) ((recvNanos - sendNano) / 1000);
            latencyInUs[recvCount - 1] = letencyUs;
        }
    }
}
