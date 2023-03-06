package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("pub")
public class MyPub implements IPubSub{

    private static final Logger logger = LoggerFactory.getLogger(MySub.class);

    @Override
    public boolean pub(AmqpMessage msg, String exch, boolean persist) {
        //创建 pub client
        KSKingMQ pubClient = KSKingMQ.CreateKingMQ("./config_pub.ini");
        NoopMsgListener ksKingMQSPI = new NoopMsgListener();
        //连接 broker
        APIResult apiResult = pubClient.ConnectServer(ksKingMQSPI);
        if(apiResult.swigValue() != APIResult.SUCCESS.swigValue()){
            logger.error("connect server failed! error code:{},,error msg:{}",apiResult.swigValue(),
                    apiResult.toString());
            return false;
        }
        while (true){
            if(ksKingMQSPI.connect()){
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

    @Override
    public boolean sub(String bindingkey, String exch, String queue, boolean durable, IMsgListener listener) {
        return false;
    }

    @Override
    public boolean unsub(String bindingkey, String exch, String queue) {
        return false;
    }
}
