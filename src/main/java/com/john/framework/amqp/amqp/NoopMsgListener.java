package com.john.framework.amqp.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopMsgListener implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(NoopMsgListener.class);


    private volatile int count = 0;


    public int getCount() {
        return count;
    }


    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        count++;
        if(count%1000==0) {
            logger.info(String.format("Im noop,current receive total: %d", count));
        }
    }
}
