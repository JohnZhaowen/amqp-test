package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.testcase.TestCaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 用例9
 * @Author zqg
 * @Date 2023/5/17
 */
public class TestCase10ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(TestCase10ConsumerMsgListener.class);

    public TestCase10ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    @Override
    protected void onMsgEnd(AmqpMessage message,long seq_no) {
        long sendTotal = message.getTotal();
        byte sender = message.getSender();
        long seq  = message.getSeq();
        logger.info("收到结束标识包，发送端标识：{},发送端结束序号：{},broker end seq_no:{}", sender,seq,seq_no);
        if(sender ==1){
            //只是打印一下发送端的数据量和最终收到的数据量
            logger.info("producer send total1:{},through binding key match,receive count1: [{}]",
                    sendTotal,total1);
            //相关数据清零 方便sub端不需要重启
            total1 =0 ;
            lastSeqNo1 = 0;
        }
    }

    @Override
    protected void validationRoutingKeyAndBindKey(AmqpMessage message, String routingKey) {
        logger.info("msg routing key:{}", routingKey);
    }
}
