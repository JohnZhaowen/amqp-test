package com.john.framework.amqp.amqp;

/**
 * @Description MQ连接状态接口维护
 * @Author zqg
 * @Date 2023/5/9
 */
public interface KSKingMQServerState {

    default boolean connect(){
        return false;
    }

    default boolean subscribe(){
        return false;
    }

    default void setSubscribe(boolean subscribe){
    }
}
