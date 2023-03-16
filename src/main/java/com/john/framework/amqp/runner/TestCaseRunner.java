package com.john.framework.amqp.runner;

import com.google.common.util.concurrent.RateLimiter;
import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.NoopMsgListener;
import com.john.framework.amqp.amqp.SlowConsumerMsgListener;
import com.john.framework.amqp.amqp.StatisticsConsumerShortMsgListener;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.BindingKeyGenerator;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
                doKsPubByShortMsg(testCase);
                break;
            case "sub":
                doSub(testCase);
                break;
            case "sub10":
                doSub10(testCase);
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
            if(StringUtils.isBlank(environment.getProperty("sendType"))||"ks1".equalsIgnoreCase(environment.getProperty("sendType"))){
                doKsPubByShortMsg(testCase);
            }else if("ks2".equalsIgnoreCase(environment.getProperty("sendType"))){
                doKsPub(testCase);
            }else{
                doPub(testCase);
            }

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
        bindingKeys[1] = BindingKeyGenerator.generate();

        pubSub.sub(bindingKeys,
                testCase.durable ? TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                testCase.durable,
                //只选择节点5进行满消费测试
                testCase.slowConsumer && uniqueId == 5 ? new SlowConsumerMsgListener() : new NoopMsgListener()
        );
    }


    /**
     * 调用该方法的只会是sub10 就是统计分开
     *
     * @param testCase
     */
    private void doSub10(TestCaseEnum testCase) {
        //统计sub10 固定传统计
        pubSub.sub(BindingKeyGenerator.generateAll(),
                testCase.durable ? TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId,
                testCase.durable,
                //只选择节点5进行满消费测试
                new StatisticsConsumerShortMsgListener(testCase,environment)
        );
    }


    private void doPub(TestCaseEnum testCase) {

        int testTime = Integer.parseInt(environment.getProperty("testTime", String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * testTime;
        RateLimiter rateLimiter = RateLimiter.create(msgSendRate);

        AmqpMessage msg = new AmqpMessage(testCase.msgSize);
        msg.setTestCaseId(testCase.testCaseId);
        msg.setBody(MessageBodyGenerator.generate(msg.getBody().length));

        LOG.info("start pub packet by RateLimiter");
        int durable = testCase.durable ? 1 : 0;
        int sendedCount = 0;
        long tv_start = System.nanoTime();
        while (sendedCount < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.getRandomRoutingKey();
            rateLimiter.acquire();
            msg.setTimestampInNanos(System.nanoTime());
            pubSub.pub(msg,routingKey, durable);
            sendedCount++;
        }
        //发送endMark消息
        //msg.setTimestampInNanos(System.nanoTime());
        //msg.setEndMark((short) 1);
        //pubSub.pub(msg, RoutingKeyGenerator.generateEndMsgRoutingKey(), durable);
        long tv_end = System.nanoTime();
        //计算总耗时 us
        long usec = (tv_end - tv_start) / 1000;
        //计算总耗时 s
        double sec = usec / 1000000.0;
        //计算速率
        double avgPkt = totalSendMsgCount/sec;
        LOG.info(String.format("Send %d %d bytes packets in %d us (%.2f s), %.0f pkt/sec",
                totalSendMsgCount, testCase.msgSize, usec, sec, avgPkt));
    }

    private void doKsPub(TestCaseEnum testCase){
        int testTime = Integer.parseInt(environment.getProperty("testTime", String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * testTime;

        AmqpMessage msg = new AmqpMessage(testCase.msgSize);
        msg.setTestCaseId(testCase.testCaseId);
        msg.setBody(MessageBodyGenerator.generate(msg.getBody().length));

        LOG.info("start pub packet by kingstar");
        int durable = testCase.durable ? 1 : 0;

        //每发送一条要等待多少yield （忽略发送的时间消耗）
        long yield_num_per_message;

        //理论计算在此速率下 每1000包发送需要的时间 us
        double desired_duration_per_k = 1000 * 1000000.0 / msgSendRate;

        long tv = System.nanoTime();

        for(int j=0;j<100000;j++){
            //Thread.yield();
        }

        long tv1 = System.nanoTime();

        double yield_duration = (tv1 - tv) / 1000.0 / 100000.0;
        //计算发送一次需要多少 yield_num 控制速率
        yield_num_per_message = Math.round(1000000.0 /(msgSendRate * yield_duration * 1.05));
        //最低是一个
        if (yield_num_per_message < 1) yield_num_per_message = 1;

        LOG.info(String.format("yield_duration = %f  yield_num_per_message = %d", yield_duration, yield_num_per_message));
        //起始时间
        long tv_start = System.nanoTime();
        /**
         *  每1000条花费时间 起始等于 tv_start
         */
        long tv_k = tv_start;

        int haveSend = 0;
        while(haveSend < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.getRandomRoutingKey();
            msg.setTimestampInNanos(System.nanoTime());
            pubSub.pub(msg,routingKey,durable);
            haveSend++;
            /*
             *  wait for yield_num_per_second
             */

            for(int j=0;j<yield_num_per_message;j++){
                //Thread.yield();
            }

            /**
             * 实际每1000条耗时
             */
            if (haveSend % 1000 == 0){
                //实际每1000秒耗时
                long tv_k_real = System.nanoTime();

                double real_duration_per_k = (tv_k_real - tv_k) / 1000;  /* the real sending time in usec for current 1000 packets */

                double _yield_num_per_message = 0.985 * yield_num_per_message * desired_duration_per_k / real_duration_per_k;
                yield_num_per_message = Double.valueOf(_yield_num_per_message).longValue();

                if (yield_num_per_message < 1)
                    yield_num_per_message = 1;

                tv_k = tv_k_real;
            }
        }
        //发送endMark消息
        //msg.setEndMark((short) 1);
        //pubSub.pub(msg, RoutingKeyGenerator.generateEndMsgRoutingKey(), durable);
        long tv_end = System.nanoTime();
        //计算总耗时 us
        long usec = (tv_end - tv_start) / 1000;
        //计算总耗时 s
        double sec = usec / 1000000.0;
        //计算速率
        double avgPkt = totalSendMsgCount/sec;
        LOG.info(String.format("Send %d %dbytes packets in %d us (%.2f s), %.0f pkt/sec",
                totalSendMsgCount, testCase.msgSize, usec, sec, avgPkt));
    }


    private void doKsPubByShortMsg(TestCaseEnum testCase){
        int testTime = Integer.parseInt(environment.getProperty("testTime", String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * testTime;


        int durable = testCase.durable ? 1 : 0;
        LOG.info("start pub packet by kingstar");
        //每发送一条要等待多少yield （忽略发送的时间消耗）
        long yield_num_per_message;

        //理论计算在此速率下 每1000包发送需要的时间 us
        double desired_duration_per_k = 1000 * 1000000.0 / msgSendRate;

        long tv = System.nanoTime();

        for(int j=0;j<100000;j++){
            //Thread.yield();
        }

        long tv1 = System.nanoTime();

        double yield_duration = (tv1 - tv) / 1000.0 / 100000.0;
        //计算发送一次需要多少 yield_num 控制速率
        yield_num_per_message = Math.round(1000000.0 /(msgSendRate * yield_duration * 1.05));
        //最低是一个
        if (yield_num_per_message < 1) yield_num_per_message = 1;

        LOG.info(String.format("yield_duration = %f  yield_num_per_message = %d", yield_duration, yield_num_per_message));
        //起始时间
        long tv_start = System.nanoTime();
        /**
         *  每1000条花费时间 起始等于 tv_start
         */
        long tv_k = tv_start;

        int haveSend = 0;
        byte[] bizPacket = new byte[testCase.msgSize];
        while(haveSend < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.getRandomRoutingKey();
            pubSub.pub(bizPacket,routingKey,durable);
            haveSend++;
            /*
             *  wait for yield_num_per_second
             */

            for(int j=0;j<yield_num_per_message;j++){
                //Thread.yield();
            }

            /**
             * 实际每1000条耗时
             */
            if (haveSend % 1000 == 0){
                //实际每1000秒耗时
                long tv_k_real = System.nanoTime();

                double real_duration_per_k = (tv_k_real - tv_k) / 1000;  /* the real sending time in usec for current 1000 packets */

                double _yield_num_per_message = 0.985 * yield_num_per_message * desired_duration_per_k / real_duration_per_k;
                yield_num_per_message = Double.valueOf(_yield_num_per_message).longValue();

                if (yield_num_per_message < 1)
                    yield_num_per_message = 1;

                tv_k = tv_k_real;
            }
        }
        //发送endMark消息
        //msg.setEndMark((short) 1);
        //pubSub.pub(msg, RoutingKeyGenerator.generateEndMsgRoutingKey(), durable);
        long tv_end = System.nanoTime();
        //计算总耗时 us
        long usec = (tv_end - tv_start) / 1000;
        //计算总耗时 s
        double sec = usec / 1000000.0;
        //计算速率
        double avgPkt = totalSendMsgCount/sec;
        LOG.info(String.format("Send %d %dbytes packets in %d us (%.2f s), %.0f pkt/sec",
                totalSendMsgCount, testCase.msgSize, usec, sec, avgPkt));
        //pubSub.statistics();
    }
}
