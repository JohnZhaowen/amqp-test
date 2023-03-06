package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReqSubscribeField;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/3/6
 */
public interface ISubscribe {

    //将queue与exch绑定，key是bindingKey，queue是否持久化参考durable，注册监控回调  KSKingMQSPI
    boolean sub(ReqSubscribeField reqSubscribeField,  KSKingMQSPI ksKingMQSPI);
}
