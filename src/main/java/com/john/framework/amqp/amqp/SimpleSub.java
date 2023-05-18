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

        KSKingMQServerAPI ksKingMQServerAPI = new KSKingMQServerAPI(listener);
        String apiId =environment.getProperty("apiId");
        if(StringUtils.isNotBlank(apiId)){
            ksKingMQ.OverrideParameter("ApiId",apiId);
        }
        //连接 broker
        APIResult apiResult = ksKingMQ.ConnectServer(ksKingMQServerAPI);
        if (apiResult.swigValue() != APIResult.SUCCESS.swigValue()) {
            logger.error("connect server failed! error code:{},,error msg:{}", apiResult.swigValue(),
                    apiResult.toString());
            return false;
        }
        while (true) {
            if (ksKingMQServerAPI.connect()) {
                ReqSubscribeField reqSubscribeField = new ReqSubscribeField();
                //创建订阅topic
                //声明queue
                int offset = -1;
                //关于offset的设置问题 只有功能性测试+持久化的前提下 才会有读offset的问题
                //功能性测试以大于9 为约定
                //offset值说明 -1代表连接上后从连接处消费，0代表从头开始消费，其他大于0的值代表 从断点处消费
                //排除掉 结束标认的
                if(testCaseEnum.testCaseId>=9&&durable){
                    String fileName = queue+".txt";
                    long seq_no = FileUtils.readSeqNo(fileName);
                    logger.info("read csv name:{},queue name:{},seq_no:{}",fileName,queue,seq_no);
                    offset = (int)seq_no;
                }
                for (int i = 0; i < bindingKeys.length; i++) {
                    reqSubscribeField.setCnt(1);
                    QueueType queueType = new QueueType();
                    queueType.setDurable(durable ? 1 : 0);
                    queueType.setBindingKey(bindingKeys[i]);
                    queueType.setOffset(offset);
                    queueType.setQueue(queue);
                    reqSubscribeField.setElems(queueType);
                    APIResult subResult = ksKingMQ.ReqSubscribe(reqSubscribeField);
                    if (subResult.swigValue() != APIResult.SUCCESS.swigValue()) {
                        logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                                queue, bindingKeys[i], subResult.swigValue(), subResult.toString());
                        return false;
                    }
                    while (true) {
                        if (ksKingMQServerAPI.subscribe()) {
                            logger.warn("req Subscribing success! Subscribe queue name:{},bindKey:{}",
                                    queue, bindingKeys[i]);
                            if (i != bindingKeys.length-1) {
                                //重置
                                ksKingMQServerAPI.setSubscribe(false);
                            }
                            break;
                        }
                        try {
                            logger.info("req Subscribing! wait a moment! Subscribe queue name:{},bindKey:{}",
                                    queue, bindingKeys[i]);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            try {
                logger.info("connecting server! wait a moment!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
