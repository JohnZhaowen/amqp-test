package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.SimpleSub;
import com.john.framework.amqp.utils.MessageMatherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BindingKeyCheckPub implements IPubSub {

    private static final Logger logger = LoggerFactory.getLogger(BindingKeyCheckPub.class);

    private String[] preSettingBindingKeys;

    private Map<Integer, Integer> consumerMsgCountMap = new HashMap<>();

    private int sendCount = 0;


    @Override
    public void pub(AmqpMessage msg, String routingKey, int persist) {
        countStatistics(routingKey);
    }

    private void countStatistics(String routingKey) {
        sendCount++;

        for(int i = 0; i < preSettingBindingKeys.length; i++){
            if(MessageMatherUtils.match(routingKey, preSettingBindingKeys[i])){
                int count = consumerMsgCountMap.get(i) + 1;
                consumerMsgCountMap.put(i, count);
            }
        }

        if(sendCount == 10000){
            logger.info("sendCount info: [{}]", consumerMsgCountMap);
        }
    }

    @Override
    public void pub(byte[] msg, String routingKey, int persist) {
        countStatistics(routingKey);
    }

    @Override
    public boolean sub(String[] bindingKeys, String queue, boolean durable, IMsgListener listener) {
        return false;
    }

    @Override
    public void init() {
        preSettingBindingKeys = PreSettingBindingKeys.getPreSettingBindingKeys();


    }
}
