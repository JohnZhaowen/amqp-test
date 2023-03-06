package com.john.framework.amqp.amqp;

import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.ReqSubscribeField;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/3/6
 */
@Service(BeanIndex.NonSubscribe_key)
public class NonSubscribe implements ISubscribe{

    @Override
    public boolean sub(ReqSubscribeField reqSubscribeField, KSKingMQSPI ksKingMQSPI) {
        //创建 pub client
        KSKingMQ pubClient = KSKingMQ.CreateKingMQ("./config_pub.ini");
        //连接 broker
        APIResult apiResult = pubClient.ConnectServer(ksKingMQSPI);
        if(apiResult.swigValue() != APIResult.SUCCESS.swigValue()){
            //logger.log(Level.SEVERE,"connect server failed! error code:"+apiResult.swigValue()
            //        +",error msg:"+apiResult.toString());
            //System.exit(0);
        }
        return false;
    }
}
