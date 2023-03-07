package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.BindingKeyGenerator;
import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.messaging.api.QueueType;
import com.kingstar.messaging.api.ReqSubscribeField;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PubSubStatistics implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    private KSKingMQ ksKingMQ;

    private volatile boolean init = false;

    private StatisticsConsumerMsgListener ksKingMQSPI;

    public PubSubStatistics(TestCaseEnum testCaseEnum) {
        ksKingMQSPI = new StatisticsConsumerMsgListener(testCaseEnum);
    }

    @Override
    public void init() {
        if (init) {
            logger.info("KSKingMQ is init");
            return;
        }
        init = true;
        //注册一个回调 不订订阅即可
        ksKingMQ = KSKingMQ.CreateKingMQ("./config_pub.ini");
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQSPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            throw new RuntimeException("connect server failed!error msg:" + apiResult.toString());
        }
        while (true) {
            if (ksKingMQSPI.connect()) {
                init = true;
                break;
            }
            try {
                logger.info("connecting server! wait a moment!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean pub(AmqpMessage msg, String routingKey, int persist) {
        try {
            byte[] send = JavaStruct.pack(msg);
            ksKingMQ.publish(routingKey, send, persist);
            return true;
        } catch (StructException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sub(String bindingkey, String queue, boolean durable, IMsgListener listener) {
        ReqSubscribeField reqSubscribeField = new ReqSubscribeField();

        //创建订阅topic
        //声明queue
        String[] bindingKeys = BindingKeyGenerator.generateAll();
        reqSubscribeField.setCnt(bindingKeys.length);
        for (String key : bindingKeys) {
            QueueType queueType = new QueueType();
            queueType.setDurable(durable ? 1 : 0);
            queueType.setBindingKey(key);
            queueType.setOffset(0);
            queueType.setQueue(queue);
            reqSubscribeField.setElems(queueType);
        }
        APIResult subResult = ksKingMQ.ReqSubscribe(reqSubscribeField);
        if (subResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                    queue, Arrays.toString(bindingKeys), subResult.swigValue(), subResult.toString());
            return false;
        }


        while (true) {
            if (ksKingMQSPI.subscribe()) {
                break;
            }
            try {
                logger.info("req Subscribing! wait a moment! Subscribe queue name:{},bindKey:{}",
                        queue, Arrays.toString(bindingKeys));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
