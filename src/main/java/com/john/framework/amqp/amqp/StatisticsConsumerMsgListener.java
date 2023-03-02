package com.john.framework.amqp.amqp;

import com.john.framework.amqp.collectors.TestCaseRunner;
import com.john.framework.amqp.collectors.TestResultCollector;
import com.john.framework.amqp.testcase.TestStatistics;
import com.john.framework.amqp.utils.StatisticsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 该监听器用于统计延时信息
 */
public class StatisticsConsumerMsgListener implements IMsgListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    private TestResultCollector collector;

    private List<Integer> latencyInUs = new LinkedList<>();

    public StatisticsConsumerMsgListener(TestResultCollector collector) {
        this.collector = collector;
    }

    public void onMsg(AmqpMessage msg) {

        if (msg.isFinished()) {
            TestStatistics statistics = StatisticsUtils.cal(latencyInUs, msg.getTestCaseId());
            collector.addStatistics(statistics);
            latencyInUs.clear();
            LOG.info("testCase [{}] run resule: [{}]", msg.getTestCaseId(), statistics);
        }

        //计算延时
        long recvNanos = System.nanoTime();
        long sendNano = msg.getTimestampInNanos();

        int letencyUs = (int) ((recvNanos - sendNano) / 1000);
        latencyInUs.add(letencyUs);

    }
}
