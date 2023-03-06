package com.john.framework.amqp.amqp;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/3/6
 */
public interface IPublish {

    //往exch中发布msg消息，msg是否持久化参考persist
    // 是否同时注册sub端 在统计pub端就注册一个sub端 其他情况下
    boolean pub(AmqpMessage msg,String routingKey, boolean persist, ISubscribe subscribe);
}
