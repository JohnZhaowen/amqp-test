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
public class TestCase11ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(TestCase11ConsumerMsgListener.class);

    public TestCase11ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    @Override
    protected void onMsgEnd(AmqpMessage message,long seq_no) {
        long sendTotal = message.getTotal();
        byte sender = message.getSender();
        long seq  = message.getSeq();
        logger.info("收到结束标识包，发送端标识：{},发送端结束序号：{},broker end seq_no:{}", sender,seq,seq_no);
        if(sender ==1){
            //只是打印一下发送端的数据量和最终收到的数据量 ,丢失的数据量
            logger.info("结束消费发送端消息,发送端结束序号:{},producer send total1:{},receive count1: [{}],miss count:[{}],broker end seq_no:{}",
                    seq,sendTotal,total1,sendTotal-total1,seq_no);
            //相关数据清零 方便sub端不需要重启
            total1 =0 ;
            lastSeqNo1 = 0;
        }
    }
}
