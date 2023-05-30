package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.CsvUtils;
import com.john.framework.amqp.utils.FileUtils;
import com.kingstar.messaging.api.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class SimpleSub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    @Override
    public void pub(AmqpMessage msg, String exch, int persist) {

    }

    @Override
    public void pub(byte[] msg, String routingKey, int persist) {

    }

    private KSKingMQ ksKingMQ;

    private KSKingMQServerAPI ksKingMQServerAPI;

    private volatile boolean init = false;

    private TestCaseEnum testCaseEnum;

    private Environment environment;

    public SimpleSub(TestCaseEnum testCaseEnum, Environment environment) {
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
        //创建 KSMQ client实例
        ksKingMQ = KSKingMQ.CreateKingMQ("./config_sub.ini");
    }

    @Override
    public boolean sub(String[] bindingKeys, String queue, boolean durable, IMsgListener listener) {
        ksKingMQServerAPI = new KSKingMQServerAPI(listener,
                testCaseEnum,queue,bindingKeys,ksKingMQ);
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
            return false;
        }
        return true;
    }

}
