package com.john.framework.amqp.amqp;

import com.john.framework.amqp.functest.PreSettingBindingKeys;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import com.john.framework.amqp.utils.MessageMatherUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimplePub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    private KSKingMQ ksKingMQ;

    private volatile boolean init = false;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);

    private TestCaseEnum testCaseEnum;
    private Environment environment;

    private KSKingMQServerAPI ksKingMQServerAPI;

    //为案例9 测试
    private String[] preSettingBindingKeys = PreSettingBindingKeys.getPreSettingBindingKeys();
    //key binding key value匹配的数量
    private Map<String, Integer> consumerMsgCountMap = new HashMap<>();

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
        ksKingMQServerAPI = new KSKingMQServerAPI(new NoopMsgListener());
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
    public void pub(AmqpMessage msg, String routingKey, int persist) {
        try {
            while (true){
                if(ksKingMQServerAPI.connect()){
                    //需要统计binding key的匹配数量
                    byte[] send = JavaStruct.pack(msg);
                    if(testCaseEnum.testCaseId==10){
                        boolean isSend = countStatistics(routingKey,msg);
                        if(isSend||msg.getEndMark()==1){
                            ksKingMQ.publish(routingKey, send, persist);
                        }
                        if(msg.getEndMark()==1){
                            logger.info("testCaseId:{},routingKey match bindingKey detail:{}",
                                    testCaseEnum.testCaseId,consumerMsgCountMap);
                        }
                    }else{
                        ksKingMQ.publish(routingKey, send, persist);
                    }
                    //退出循环
                    break;
                }else{
                    Thread.sleep(1000);
                    logger.error("api disconnect can not send msg!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean countStatistics(String routingKey,AmqpMessage msg) {
        boolean send = false;
        for(int i = 0; i < preSettingBindingKeys.length; i++){
            String bindingKey = preSettingBindingKeys[i];
            if(msg.getEndMark()!=1&&MessageMatherUtils.match(routingKey,bindingKey)){
                logger.debug("routing key:{},bindingKey:{}",routingKey,bindingKey);
                Integer count = consumerMsgCountMap.get(bindingKey);
                count =  count==null? 1:count+1;
                consumerMsgCountMap.put(preSettingBindingKeys[i], count);
                send = true;
                //binding key在设计在上互斥的 只要有一个匹配 后面的绝对匹配不上
                break;
            }
        }
        return send;
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
