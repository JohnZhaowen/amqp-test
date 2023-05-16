package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.QueueType;
import com.kingstar.messaging.api.ReqSubscribeField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class SimpleSub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    @Override
    public boolean pub(AmqpMessage msg, String exch, int persist) {
        return false;
    }

    @Override
    public void pub(byte[] msg, String routingKey, int persist) {

    }

    private KSKingMQ ksKingMQ;

    private volatile boolean init = false;

    private TestCaseEnum testCaseEnum;

    private Environment environment;

    public SimpleSub(TestCaseEnum testCaseEnum, Environment environment) {
        this.testCaseEnum = testCaseEnum;
        this.environment = environment;
    }

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
    public boolean sub(String[] bindingKeys, String queue, boolean durable, IMsgListener listener) {
        KSKingMQSPI ksKingMQSPI = (KSKingMQSPI) listener;
        String apiId =environment.getProperty("apiId");
        if(StringUtils.isNotBlank(apiId)){
            ksKingMQ.OverrideParameter("ApiId",apiId);
        }
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQSPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            return false;
        }
        while (true) {
            if (listener.connect()) {
                ReqSubscribeField reqSubscribeField = new ReqSubscribeField();
                //创建订阅topic
                //声明queue
                for (int i=0;i<bindingKeys.length;i++) {
                    reqSubscribeField.setCnt(1);
                    QueueType queueType = new QueueType();
                    queueType.setDurable(durable ? 1 : 0);
                    queueType.setBindingKey(bindingKeys[i]);
                    queueType.setOffset(-1);
                    queueType.setQueue(queue);
                    reqSubscribeField.setElems(queueType);
                    APIResult subResult = ksKingMQ.ReqSubscribe(reqSubscribeField);
                    if (subResult.swigValue() != APIResult.SUCCESS.swigValue()) {
                        logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                                queue, bindingKeys[i], subResult.swigValue(), subResult.toString());
                        return false;
                    }
                    while (true) {
                        if (listener.subscribe()) {
                            if(i!=9){
                                //重置
                                listener.setSubscribe(false);
                            }
                            break;
                        }
                        try {
                            logger.info("req Subscribing! wait a moment! Subscribe queue name:{},bindKey:{}",
                                    queue, bindingKeys[i]);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
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
