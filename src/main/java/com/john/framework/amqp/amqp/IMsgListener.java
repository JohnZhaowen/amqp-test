package com.john.framework.amqp.amqp;

public interface IMsgListener {

    //queueName是用于 记录一些断点的
    void onMsg(String routingKey, byte[] pMsgbuf,long seq_no);
}
