package com.john.framework.amqp.amqp;

public interface IPubSub {

    //往exch中发布msg消息，msg是否持久化参考persist
    boolean pub(AmqpMessage msg, String exch, boolean persist);

    //将queue与exch绑定，key是bindingKey，queue是否持久化参考durable，监听器未listener
    boolean sub(String bindingkey, String exch, String queue, boolean durable, IMsgListener listener);

    boolean unsub(String bindingkey, String exch, String queue);

}
