package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.testcase.TestCaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * @Description 用例9
 * @Author zqg
 * @Date 2023/5/17
 */
public class TestCase9ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(TestCase9ConsumerMsgListener.class);

    public TestCase9ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    @Override
    protected void onMsgExt(AmqpMessage message) {
        long sendTotal = message.getTotal();
        //判断收到的数量是否与发送的数量相等
        if(sendTotal != total){
            logger.error("send total:{},receive total count: [{}],不相等", sendTotal,total);
        }else{
            logger.info("send total:{},receive total count: [{}]，相等", sendTotal,total);
        }
    }
}
