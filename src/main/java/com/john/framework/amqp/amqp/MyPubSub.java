package com.john.framework.amqp.amqp;

import org.springframework.stereotype.Component;

@Component
public class MyPubSub implements IPubSub{

    @Override
    public boolean pub(AmqpMessage msg, String exch, boolean persist) {
        return false;
    }

    @Override
    public boolean sub(String bindingkey, String exch, String queue, boolean durable, IMsgListener listener) {
        return false;
    }

    @Override
    public boolean unsub(String bindingkey, String exch, String queue) {
        return false;
    }
}
