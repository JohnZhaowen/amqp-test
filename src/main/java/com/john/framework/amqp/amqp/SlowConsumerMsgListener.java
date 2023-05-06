package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReConnectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SlowConsumerMsgListener extends KSKingMQSPI implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(SlowConsumerMsgListener.class);

    private volatile boolean connect = false;

    private volatile boolean subscribe = false;

    private volatile int count = 0;

    @Override
    public void OnConnected() {
        logger.info("OnConnected callback, sub client connected to broker!");
        connect = true;
    }

    @Override
    public void OnDisconnected(ReConnectStatus reConnectStatus, ErrorInfo pErrorInfo) {
        logger.warn("OnDisconnected callback, sub client disconnected to broker! error code:"+pErrorInfo.getErrorId()+
                ",error msg:"+pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnRtnSubscribe(String pQueue, ErrorInfo pErrorInfo) {
        logger.info("OnRtnSubscribe callback, sub client Subscribed success ,queue name:"+pQueue);
        if(pErrorInfo.getErrorId()==0){
            subscribe = true;
        }
    }

    @Override
    public void OnMessage(String routingKey, byte[] pMsgbuf,long seq_no) {
        count++;
        if(count%1000==0) {
            System.out.println(String.format("Im slow,current receive total: %d", count));
        }
        try {
            //兼容新的包结构 此处不再解包
            //AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
            //JavaStruct.unpack(packet, pMsgbuf);
            //onMsg(packet);
            Thread.sleep(new Random().nextInt(500)+500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return count;
    }

    @Override
    public void onMsg(AmqpMessage msg) {

    }

    @Override
    public boolean connect() {
        return connect;
    }

    @Override
    public boolean subscribe() {
        return subscribe;
    }
}
