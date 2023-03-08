package com.john.framework.amqp.runner;

import com.google.common.util.concurrent.RateLimiter;
import com.john.framework.amqp.amqp.*;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.BindingKeyGenerator;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import com.kingstar.messaging.api.KSKingMQ;
import org.apache.commons.lang3.StringUtils;
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
    private IPubSub pubSub;

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
        //全部订阅
        String[] bindingKeys = BindingKeyGenerator.generateAll();
        boolean sub = pubSub.sub(bindingKeys,
                testCase.durable ? TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                testCase.durable, null);
        if (sub) {
            doPub(testCase);
        }
    }

    /**
     * 调用该方法的只会是只进行sub的节点
     *
     * @param testCase
     */
    private void doSub(TestCaseEnum testCase) {
        //只订阅2种 一种是根testCase选择
        String[] bindingKeys = new String[2];
        bindingKeys[0] = BindingKeyGenerator.generateEndMark();
        bindingKeys[01] =  BindingKeyGenerator.generate();

        pubSub.sub(bindingKeys,
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

        AmqpMessage msg = new AmqpMessage(testCase.msgSize);
        msg.setTestCaseId(testCase.testCaseId);
        msg.setBody(MessageBodyGenerator.generate(msg.getBody().length));

        LOG.info("start pub packet.");
        int durable = testCase.durable ? 1 : 0;
        int sendedCount = 0;
        while (sendedCount < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.generate();
            rateLimiter.acquire();
            msg.setTimestampInNanos(System.nanoTime());
            pubSub.pub(msg,routingKey, durable);
            sendedCount++;
        }
        //发送endMark消息
        msg.setTimestampInNanos(System.nanoTime());
        msg.setEndMark((short) 1);
        pubSub.pub(msg, RoutingKeyGenerator.generateEndMsgRoutingKey(), durable);

        LOG.info("end pub msgs...");
    }
}
