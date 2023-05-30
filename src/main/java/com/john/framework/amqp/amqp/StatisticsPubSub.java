package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.EnvironmentUtils;
import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class StatisticsPubSub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    private KSKingMQ ksKingMQ;

    private KSKingMQServerAPI ksKingMQServerAPI;

    private volatile boolean init = false;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);

    private Environment environment;

    private TestCaseEnum testCaseEnum;

    //包多大要压缩
    private int compressLen = 2*1024*1024;
    private int packetSize;


    volatile long[] rtt;

    public StatisticsPubSub(TestCaseEnum testCaseEnum, Environment environment) {
        this.environment = environment;
        this.testCaseEnum = testCaseEnum;
        compressLen = EnvironmentUtils.getCompressLen();
        int testTime = EnvironmentUtils.getTestTime();
        rtt = new long[testCaseEnum.msgSendRate*testTime];
        packetSize = testCaseEnum.msgSize;
    }

    @Override
    public void init() {
        if (init) {
            logger.info("KSKingMQ is init");
            return;
        }
        init = true;
        //注册一个回调 不订订阅即可
        ksKingMQ = KSKingMQ.CreateKingMQ("./config_pub.ini");
    }

    @Override
    public void pub(AmqpMessage msg, String routingKey, int persist) {
        try {
            byte[] send = JavaStruct.pack(msg);
            ksKingMQ.publish(routingKey, send, persist);
        } catch (StructException e) {
            e.printStackTrace();
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
        //按测试用例来
        ksKingMQServerAPI = new KSKingMQServerAPI(listener,testCaseEnum,queue,bindingKeys,ksKingMQ);
        String apiId =environment.getProperty("apiId");
        if(StringUtils.isNotBlank(apiId)){
            ksKingMQ.OverrideParameter("ApiId",apiId);
        }
        String groupId = environment.getProperty("groupId");
        if(StringUtils.isNotBlank(groupId)){
            ksKingMQ.OverrideParameter("GroupId",groupId);
        }
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQServerAPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            throw new RuntimeException("connect server failed!error msg:" + apiResult.toString());
        }
        //判断是否连接和订阅成功
        while (true) {
            if (ksKingMQServerAPI.connect()&&ksKingMQServerAPI.isAllSubscribeOk()) {
                return true;
            }
            try {
                logger.info("connecting server or req subscribe!! wait a moment!");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
