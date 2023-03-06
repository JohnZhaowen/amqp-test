package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReConnectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopMsgListener extends KSKingMQSPI implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(NoopMsgListener.class);

    private volatile boolean connect = false;

    private volatile boolean subscribe = false;

    private volatile int count = 0;

    @Override
    public void OnConnected() {
        logger.info("sub client connected to broker!");
        connect = true;
    }

    @Override
    public void OnDisconnected(ReConnectStatus reConnectStatus, ErrorInfo pErrorInfo) {
        logger.warn("sub client disconnected to broker! error code:"+pErrorInfo.getErrorId()+
                ",error msg:"+pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnRtnSubscribe(String pQueue, ErrorInfo pErrorInfo) {
        logger.info("sub client Subscribed success ,queue name:"+pQueue);
        if(pErrorInfo.getErrorId()==0){
            subscribe = true;
        }
    }

    @Override
    public void OnMessage(String routingKey, byte[] pMsgbuf, ErrorInfo pErrorInfo) {
        count++;
        //什么都不做
        onMsg(null);
    }

    public boolean isConnect() {
        return connect;
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
