package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    @Override
    public boolean pub(AmqpMessage msg, String exch, int persist) {
        return false;
    }

    private KSKingMQ ksKingMQ;

    private volatile boolean init = false;

    @Override
    public void init() {
        if (init) {
            logger.info("KSKingMQ is init");
            return;
        }
        init = true;
        //创建 KSMQ client实例
        ksKingMQ = KSKingMQ.CreateKingMQ("./config_sub.ini");

    }

    @Override
    public boolean sub(String bindingkey, String queue, boolean durable, IMsgListener listener) {
        KSKingMQSPI ksKingMQSPI = (KSKingMQSPI) listener;
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQSPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            return false;
        }
        while (true) {
            if (listener.connect()) {
                //pub client 订阅回复消息
                ReqSubscribeField reqSubscribeField = new ReqSubscribeField();
                reqSubscribeField.setCnt(1);
                //创建订阅topic
                //声明queue
                QueueType queueType = new QueueType();
                if (durable) {
                    queueType.setDurable(1);
                } else {
                    queueType.setDurable(0);
                }
                queueType.setBindingKey(bindingkey);
                queueType.setOffset(0);
                queueType.setQueue(queue);
                reqSubscribeField.setElems(queueType);
                APIResult subResult = ksKingMQ.ReqSubscribe(reqSubscribeField);
                if (subResult.swigValue() != APIResult.SUCCESS.swigValue()) {
                    logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                            queueType.getQueue(), queueType.getBindingKey(), subResult.swigValue(), subResult.toString());
                    return false;
                }
                boolean subscribe = false;
                while (true) {
                    if (listener.subscribe()) {
                        subscribe = true;
                        break;
                    }
                    try {
                        logger.info("req Subscribing! Subscribe queue name:{},bindKey:{}",
                                queueType.getQueue(), queueType.getBindingKey());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (subscribe) {
                    break;
                }
            }
            try {
                logger.info("connecting server! wait a moment!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
