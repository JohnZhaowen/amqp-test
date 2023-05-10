package com.john.framework.amqp.amqp;

public interface IMsgListener {

    void onMsg(String routingKey, byte[] pMsgbuf,long seq_no);
}
