package com.john.framework.amqp.amqp;

import java.util.concurrent.TimeUnit;

public class SlowConsumerMsgListener implements IMsgListener {

    public void onEvent(AmqpMessage msg) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            //skip
        }
    }
}
