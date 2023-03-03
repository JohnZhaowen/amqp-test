package com.john.framework.amqp.collectors;

import com.john.framework.amqp.amqp.*;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TestCaseRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    @Value("${appType}")
    private String appType;

    @Value("${uniqueId}")
    private int uniqueId;

    @Autowired
    private TestResultCollector collector;

    @Autowired
    private IPubSub pubSub;

    @Autowired
    private Environment environment;


    public void runTestCases(List<TestCaseEnum> cases) {
        if (cases == null || cases.size() == 0) {
            LOG.warn("no test case to run");
            return;
        }

        switch (appType) {
            case "pubsub":
                doPubsub(cases);
                break;
            case "pub":
                doPub(cases);
                break;
            case "sub":
                doSub(cases);
                break;
            default:
                throw new IllegalArgumentException("appType: " + appType + " is not valid.");

        }

    }

    private void doPubsub(List<TestCaseEnum> cases) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //执行sub
        executorService.execute(() -> {
            cases.forEach(testCase -> {
                pubSub.sub(TestContents.BINDING_KEY,
                        TestContents.EXCHAGE,
                        TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                        testCase.durable,
                        new StatisticsConsumerMsgListener(collector, testCase)
                );
            });

        });

        //执行pub
        executorService.execute(() -> doPub(cases));
    }

    private void doSub(List<TestCaseEnum> cases) {

        cases.forEach(testCase -> {
            pubSub.sub(TestContents.BINDING_KEY,
                    TestContents.EXCHAGE,
                    TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                    testCase.durable,
                    testCase.slowConsumer && uniqueId == 5 ? new SlowConsumerMsgListener() : new NoopMsgListener()
            );
        });
    }


    private void doPub(List<TestCaseEnum> cases) {
        cases.forEach(this::doSendMsg);
    }

    private void doSendMsg(TestCaseEnum testCase) {

        int msgSendRate = testCase.msgSendRate;

        int totalSendMsgCount = msgSendRate * TestContents.TEST_TIME_IN_SECONDS;

        int sendedCount = 0;

        //RateLimiter
        AmqpMessage msg = new AmqpMessage();
        msg.setTestCaseId(testCase.testCaseId);
        msg.setBody(MessageBodyGenerator.generate(testCase.msgSize));

        while (sendedCount < totalSendMsgCount) {
            msg.setRoutingKey(RoutingKeyGenerator.generate());
            msg.setTimestampInNanos(System.nanoTime());
            //rateLimiter
            pubSub.pub(msg, TestContents.EXCHAGE, testCase.durable);
            sendedCount++;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        int pubsubCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty("pubsubCount")));
        List<TestCaseEnum> cases = TestCaseEnum.getCasesByPubsubCount(pubsubCount);
        runTestCases(cases);
    }
}
