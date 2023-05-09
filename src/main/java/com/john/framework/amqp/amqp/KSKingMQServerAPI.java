package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReConnectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/5/9
 */
public class KSKingMQServerAPI extends KSKingMQSPI implements KSKingMQServerState {

    private static final Logger logger = LoggerFactory.getLogger(KSKingMQServerAPI.class);

    private volatile boolean connect = false;

    private volatile boolean subscribe = false;

    private volatile int count = 0;

    private IMsgListener msgListener;

    public KSKingMQServerAPI(IMsgListener msgListener) {
        this.msgListener = msgListener;
    }

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

    }

    public int getCount() {
        return count;
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
