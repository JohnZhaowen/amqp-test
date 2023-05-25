package com.john.framework.amqp.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopMsgListener implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(NoopMsgListener.class);

    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {

    }
}
