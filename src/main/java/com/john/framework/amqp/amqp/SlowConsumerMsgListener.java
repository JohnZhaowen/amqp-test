package com.john.framework.amqp.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SlowConsumerMsgListener implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(SlowConsumerMsgListener.class);

    private volatile int count = 0;


    public int getCount() {
        return count;
    }

    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        count++;
        if(count%1000==0) {
            logger.info(String.format("Im slow,current receive total: %d", count));
        }
        try {
            Thread.sleep(new Random().nextInt(500)+500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
