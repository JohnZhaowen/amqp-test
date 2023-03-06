package com.john.framework.amqp.amqp;

public interface IMsgListener {

    void onMsg(AmqpMessage msg);

    default boolean connect(){
        return false;
    }
}
