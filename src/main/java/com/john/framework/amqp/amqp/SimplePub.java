package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SimplePub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    private KSKingMQ ksKingMQ;

    private volatile boolean init = false;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);

    private TestCaseEnum testCaseEnum;
    private Environment environment;

    //包多大要压缩
    private int compressLen = 2*1024*1024;
    private int packetSize;

    public SimplePub(TestCaseEnum testCaseEnum, Environment environment) {
        this.testCaseEnum = testCaseEnum;
        this.environment = environment;
    }

    @Override
    public void init() {
        if (init) {
            logger.info("KSKingMQ is init");
            return;
        }
        init = true;
        //注册一个回调 不订订阅即可
        compressLen = Integer.parseInt(environment.getProperty("compressLen", TestContents.MSG_SIZE_OF_2M+""));
        ksKingMQ = KSKingMQ.CreateKingMQ("./config_pub.ini");
        packetSize = testCaseEnum.msgSize;

        KSKingMQServerAPI ksKingMQServerAPI = new KSKingMQServerAPI(new NoopMsgListener());
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQServerAPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            throw new RuntimeException("connect server failed!error msg:" + apiResult.toString());
        }
        while (true) {
            if (ksKingMQServerAPI.connect()) {
                break;
            }
            try {
                logger.info("connecting server! wait a moment!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean pub(AmqpMessage msg, String routingKey, int persist) {
        try {
            byte[] send = JavaStruct.pack(msg);
            ksKingMQ.publish(routingKey, send, persist);
            return true;
        } catch (StructException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void pub(byte[] msg, String routingKey, int persist) {
        byteBuffer.putLong(System.nanoTime());
        int end = byteBuffer.limit();
        for (int i = 0; i < end; i++) msg[i] = byteBuffer.get(i);
        byteBuffer.clear();
        //测试案例2压缩
        if(packetSize>=compressLen){
            try {
                byte[] bytes = Snappy.compress(msg);
                ksKingMQ.publish(routingKey, bytes, persist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            ksKingMQ.publish(routingKey, msg, persist);
        }

    }

    @Override
    public boolean sub(String[] bindingKeys, String queue, boolean durable, IMsgListener listener) {
        return false;
    }

}
