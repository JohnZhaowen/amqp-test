package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.QueueType;
import com.kingstar.messaging.api.ReqSubscribeField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sub")
public class MySub implements IPubSub{

    private static final Logger logger = LoggerFactory.getLogger(MySub.class);

    @Override
    public boolean pub(AmqpMessage msg, String exch, int persist) {
        return false;
    }

    @Override
    public boolean sub(String bindingkey, String exch, String queue, boolean durable, IMsgListener listener) {
        //创建 pub client
        KSKingMQ pubClient = KSKingMQ.CreateKingMQ("./config_sub.ini");
        KSKingMQSPI ksKingMQSPI = (KSKingMQSPI)listener;
        //连接 broker
        APIResult apiResult = pubClient.ConnectServer(ksKingMQSPI);
        if(apiResult.swigValue() != APIResult.SUCCESS.swigValue()){
            logger.error("connect server failed! error code:{},,error msg:{}",apiResult.swigValue(),
                    apiResult.toString());
            return false;
        }
        while (true){
            if(listener.connect()){
                //pub client 订阅回复消息
                ReqSubscribeField reqSubscribeField = new ReqSubscribeField();
                reqSubscribeField.setCnt(1);
                //创建订阅topic
                //声明queue
                QueueType queueType = new QueueType();
                if(durable){
                    queueType.setDurable(1);
                }else{
                    queueType.setDurable(0);
                }
                queueType.setBindingKey(bindingkey);
                queueType.setOffset(0);
                queueType.setQueue(queue);
                reqSubscribeField.setElems(queueType);
                APIResult subResult = pubClient.ReqSubscribe(reqSubscribeField);
                if(subResult.swigValue() != APIResult.SUCCESS.swigValue()){
                    logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                            queueType.getQueue(),queueType.getBindingKey(),subResult.swigValue(),subResult.toString());
                    return false;
                }
                boolean subscribe = false;
                while (true){
                    if(listener.subscribe()){
                        subscribe = true;
                        break;
                    }
                    try {
                        logger.info("req Subscribing! Subscribe queue name:{},bindKey:{}",
                                queueType.getQueue(),queueType.getBindingKey());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(subscribe){
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

    @Override
    public boolean unsub(String bindingkey, String exch, String queue) {
        return false;
    }
}
