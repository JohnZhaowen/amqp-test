package com.john.framework.amqp.amqp;

public interface IMsgListener {

    void onEvent(AmqpMessage msg);
}
