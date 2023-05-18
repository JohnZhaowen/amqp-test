package com.john.framework.amqp.runner;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.NoopMsgListener;
import com.john.framework.amqp.amqp.SlowConsumerMsgListener;
import com.john.framework.amqp.functest.PreSettingBindingKeys;
import com.john.framework.amqp.functest.TestCase10ConsumerMsgListener;
import com.john.framework.amqp.functest.TestCase11ConsumerMsgListener;
import com.john.framework.amqp.functest.TestCase12ConsumerMsgListener;
import com.john.framework.amqp.functest.TestCase9ConsumerMsgListener;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.BindingKeyGenerator;
import com.john.framework.amqp.utils.EnvironmentUtils;
import com.john.framework.amqp.utils.MD5Utils;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.john.framework.amqp.utils.RoutingKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

        int testCaseId = EnvironmentUtils.getTestCaseId();
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
                doFuncKsPub(testCase,(byte)EnvironmentUtils.getSender());
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
        boolean sub = pubSub.sub(bindingKeys, EnvironmentUtils.getQueueName(testCase), testCase.durable, null);
        if (sub) {
              //如果是pub sub一起的就当作是性能测试，功能测试 pub sub分开统计
            doPerfKsPub(testCase);

        }
    }

    /**
     * 调用该方法的只会是只进行sub的节点
     *
     * @param testCase
     */
    private void doSub(TestCaseEnum testCase) {
        //只订阅2种 一种是根testCase选择
        String[] bindingKeys = null;
        IMsgListener msgListener = null;
        if(testCase.testCaseId<=8){
            bindingKeys = new String[2];
            bindingKeys[0] = BindingKeyGenerator.generateEndMark();
            bindingKeys[1] = BindingKeyGenerator.generate();
            msgListener = testCase.slowConsumer && uniqueId == 5 ? new SlowConsumerMsgListener() : new NoopMsgListener();
        }else if(testCase.testCaseId==9){
            //保证都能收到
            bindingKeys = BindingKeyGenerator.generateAll();
            msgListener = new TestCase9ConsumerMsgListener(testCase);
        }else if(testCase.testCaseId == 10){
            //只保证匹配到的都能收到
            bindingKeys = new String[2];
            bindingKeys[0] = BindingKeyGenerator.generateEndMark();
            //提前预设好的 binding key
            bindingKeys[1] = PreSettingBindingKeys.generate();
            msgListener = new TestCase10ConsumerMsgListener(testCase);
        }else if(testCase.testCaseId == 11){
            //保证都能收到
            bindingKeys = BindingKeyGenerator.generateAll();
            msgListener = new TestCase11ConsumerMsgListener(testCase);
        }else if(testCase.testCaseId ==12){
            //保证都能收到
            bindingKeys = BindingKeyGenerator.generateAll();
            msgListener = new TestCase12ConsumerMsgListener(testCase);
        }else if(testCase.testCaseId == 13){
            //只保证匹配到的都能收到
            bindingKeys = new String[2];
            bindingKeys[0] = BindingKeyGenerator.generateEndMark();
            bindingKeys[1] = BindingKeyGenerator.generate();
            msgListener = testCase.slowConsumer && uniqueId == 998 ? new SlowConsumerMsgListener() : new NoopMsgListener();
        }else throw new RuntimeException("不支持的 testCase:"+testCase.testCaseId);
        pubSub.sub(bindingKeys,
                EnvironmentUtils.getQueueName(testCase),
                testCase.durable,
                //只选择节点5进行慢消费测试
                msgListener
        );
    }



    //功能性测试发包 包体为 AmqpMessage
    private void doFuncKsPub(TestCaseEnum testCase,byte send) {
        int testTime = EnvironmentUtils.getTestTime();

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * testTime;

        AmqpMessage msg = new AmqpMessage(testCase.msgSize);
        msg.setSender(send);

        LOG.info("start Func pub packet by kingstar");
        int durable = testCase.durable ? 1 : 0;

        //每发送一条要等待多少yield （忽略发送的时间消耗）
        long yield_num_per_message;

        //理论计算在此速率下 每1000包发送需要的时间 us
        double desired_duration_per_k = 1000 * 1000000.0 / msgSendRate;

        long tv = System.nanoTime();

        for (int j = 0; j < 100000; j++) {
            //Thread.yield();
        }

        long tv1 = System.nanoTime();

        double yield_duration = (tv1 - tv) / 1000.0 / 100000.0;
        //计算发送一次需要多少 yield_num 控制速率
        yield_num_per_message = Math.round(1000000.0 / (msgSendRate * yield_duration * 1.05));
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
        while (haveSend < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.getRandomRoutingKey();
            msg.setSeq(haveSend+1);
            msg.setBody(MessageBodyGenerator.generate(msg.getBody().length));
            msg.setMd5(MD5Utils.md5ForByte(msg.getBody()));
            pubSub.pub(msg, routingKey, durable);
            haveSend++;
            /*
             *  wait for yield_num_per_second
             */

            for (int j = 0; j < yield_num_per_message; j++) {
                //Thread.yield();
            }

            /**
             * 实际每1000条耗时
             */
            if (haveSend % 1000 == 0) {
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
        msg.setEndMark((byte)1);
        msg.setTotal(haveSend);
        pubSub.pub(msg, RoutingKeyGenerator.generateEndMsgRoutingKey(), durable);
        long tv_end = System.nanoTime();
        //计算总耗时 us
        long usec = (tv_end - tv_start) / 1000;
        //计算总耗时 s
        double sec = usec / 1000000.0;
        //计算速率
        double avgPkt = totalSendMsgCount / sec;
        LOG.info(String.format("Send %d %dbytes packets in %d us (%.2f s), %.0f pkt/sec",
                totalSendMsgCount, testCase.msgSize, usec, sec, avgPkt));
    }


    private void doPerfKsPub(TestCaseEnum testCase) {
        int testTime = EnvironmentUtils.getTestTime();

        int msgSendRate = testCase.msgSendRate;
        int totalSendMsgCount = msgSendRate * testTime;

        int durable = testCase.durable ? 1 : 0;
        LOG.info("start performance pub packet by kingstar");
        //每发送一条要等待多少yield （忽略发送的时间消耗）
        long yield_num_per_message;

        //理论计算在此速率下 每1000包发送需要的时间 us
        double desired_duration_per_k = 1000 * 1000000.0 / msgSendRate;

        long tv = System.nanoTime();

        for (int j = 0; j < 100000; j++) {
            //Thread.yield();
        }

        long tv1 = System.nanoTime();

        double yield_duration = (tv1 - tv) / 1000.0 / 100000.0;
        //计算发送一次需要多少 yield_num 控制速率
        yield_num_per_message = Math.round(1000000.0 / (msgSendRate * yield_duration * 1.05));
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
        while (haveSend < totalSendMsgCount) {
            String routingKey = RoutingKeyGenerator.getRandomRoutingKey();
            pubSub.pub(bizPacket, routingKey, durable);
            haveSend++;
            /*
             *  wait for yield_num_per_second
             */

            for (int j = 0; j < yield_num_per_message; j++) {
                //Thread.yield();
            }

            /**
             * 实际每1000条耗时
             */
            if (haveSend % 1000 == 0) {
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
        long tv_end = System.nanoTime();
        //计算总耗时 us
        long usec = (tv_end - tv_start) / 1000;
        //计算总耗时 s
        double sec = usec / 1000000.0;
        //计算速率
        double avgPkt = totalSendMsgCount / sec;
        LOG.info(String.format("Send %d %dbytes packets in %d us (%.2f s), %.0f pkt/sec",
                totalSendMsgCount, testCase.msgSize, usec, sec, avgPkt));
    }
}
