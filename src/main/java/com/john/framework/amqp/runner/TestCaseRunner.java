package com.john.framework.amqp.runner;

import com.google.common.util.concurrent.RateLimiter;
import com.john.framework.amqp.amqp.*;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.BindingKeyGenerator;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
    @Qualifier("pub")
    private IPubSub pub;

    @Autowired
    @Qualifier("sub")
    private IPubSub sub;

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) {

        int testCaseId = Integer.parseInt(Objects.requireNonNull(environment.getProperty("testCaseId")));

        LOG.info("run env: uniqueId[{}], appType[{}], testCaseId[{}]", uniqueId, appType, testCaseId);

        TestCaseEnum testCaseEnum = TestCaseEnum.getById(testCaseId);
        runTestCases(testCaseEnum);
    }

    public void runTestCases(TestCaseEnum testCase) {
        if (testCase == null) {
            LOG.warn("no test case to run");
            return;
        }

        switch (appType) {
            case "pubsub":
                doPubsub(testCase);
                break;
            case "pub":
                doPub(testCase);
                break;
            case "sub":
                doSub(testCase);
                break;
            default:
                throw new IllegalArgumentException("appType: " + appType + " is not valid.");

        }

    }

    private void doPubsub(TestCaseEnum testCase) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //TODO 执行sub，一定要绑定endMark，也就是至少要绑定两个key
        executorService.execute(() -> {
            sub.sub(BindingKeyGenerator.generate(),
                    TestContents.EXCHAGE,
                    testCase.durable ? TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                    testCase.durable,
                    new StatisticsConsumerMsgListener(testCase)
            );
        });

        //执行pub
        executorService.execute(() -> doPub(testCase));
    }

    /**
     * 调用该方法的只会是只进行sub的节点
     *
     * @param testCase
     */
    private void doSub(TestCaseEnum testCase) {
        sub.sub(BindingKeyGenerator.generate(),
                TestContents.EXCHAGE,
                testCase.durable ? TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                testCase.durable,
                //只选择节点5进行满消费测试
                testCase.slowConsumer && uniqueId == 5 ? new SlowConsumerMsgListener() : new NoopMsgListener()
        );
    }


    private void doPub(TestCaseEnum testCase) {

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * TestContents.TEST_TIME_IN_SECONDS;
        RateLimiter rateLimiter = RateLimiter.create(msgSendRate);

        AmqpMessage msg = new AmqpMessage();
        msg.setTestCaseId(testCase.testCaseId);
        msg.setBody(MessageBodyGenerator.generate(testCase.msgSize));

        LOG.info("start pub msgs.");
        int sendedCount = 0;

        while (sendedCount < totalSendMsgCount) {
            msg.setRoutingKey(RoutingKeyGenerator.generate());
            msg.setTimestampInNanos(System.nanoTime());

            rateLimiter.acquire();
            pub.pub(msg, TestContents.EXCHAGE, testCase.durable);
            sendedCount++;
        }
        //发送endMark消息
        msg.setEndMark(false);
        msg.setRoutingKey(RoutingKeyGenerator.generateEndMsgRoutingKey());
        pub.pub(msg, TestContents.EXCHAGE, testCase.durable);

        LOG.info("end pub msgs...");
    }
}
