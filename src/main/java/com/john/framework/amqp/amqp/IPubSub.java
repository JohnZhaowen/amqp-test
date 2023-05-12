package com.john.framework.amqp.amqp;

public interface IPubSub {

    //往exch中发布msg消息，msg是否持久化参考persist
    //exchange 固定不需要传
    void pub(AmqpMessage msg, String routingKey, int persist);

    //往exch中发布msg消息，msg是否持久化参考persist
    //exchange 固定不需要传
    void pub(byte[] msg, String routingKey, int persist);

    //将queue与exch绑定，key是bindingKey，queue是否持久化参考durable，监听器未listener
    boolean sub(String[] bindingKeys, String queue, boolean durable, IMsgListener listener);

    void init();
}
