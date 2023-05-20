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
public class TestCase12ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(TestCase12ConsumerMsgListener.class);

    public TestCase12ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    @Override
    protected void onMsgEnd(AmqpMessage message,long seq_no) {
        long sendTotal = message.getTotal();
        byte sender = message.getSender();
        if(sender ==1){
            //只是打印一下发送端的数据量和最终收到的数据量 ,丢失的数据量
            logger.info("producer send total1:{},receive count1: [{}],miss count:[{}],broker end seq_no:{}",
                    sendTotal,total1,sendTotal-total1,seq_no);
            //相关数据清零 方便sub端不需要重启
            total1 =0 ;
            lastSeqNo1 = 0;
        }
    }
}
