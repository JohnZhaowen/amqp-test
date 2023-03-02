package com.john.framework.amqp.collectors;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.StatisticsConsumerMsgListener;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    @Value("appType")
    private String appType;

    @Value("uniqueId")
    private String uniqueId;

    private TestResultCollector collector;

    private IPubSub pubSub;

    public TestCaseRunner(TestResultCollector collector, IPubSub pubSub) {
        this.collector = collector;
        this.pubSub = pubSub;
    }

    public void runTestCases(List<TestCaseEnum> cases) {
        if (cases == null || cases.size() == 0) {
            LOG.warn("no test case to run");
            return;
        }

        int caseCount = cases.size();
        int finished = 0;
        for (TestCaseEnum testCase : cases) {
            LOG.info("start run case: [{}]", testCase);

            doRun(testCase);
            //TODO 清理工作
            LOG.info("end run case: [{}], already run : [{}]", testCase, (++finished) + "/" + caseCount);
        }

    }

    private void doRun(TestCaseEnum testCase) {

        //PUB
        AmqpMessage msg = new AmqpMessage();
        msg.setTestCaseId(testCase.testCaseId);
        msg.setRoutingKey(RoutingKeyGenerator.generate());

        msg.setBody(MessageBodyGenerator.generate(testCase.msgSize));
        msg.setTimestampInNanos(System.nanoTime());
        msg.setFinished(true);
        pubSub.pub(msg, TestContents.EXCHAGE, testCase.durable);

        //SUB
        pubSub.sub(TestContents.BINDING_KEY,
                TestContents.EXCHAGE,
                TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                false,
                new StatisticsConsumerMsgListener(collector)
        );
    }


}
