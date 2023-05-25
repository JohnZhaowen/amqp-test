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
public class TestCase9ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(TestCase9ConsumerMsgListener.class);

    public TestCase9ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    private long expectSeq1;

    private long expectSeq2;

    @Override
    protected void onMsgEnd(AmqpMessage message,long seq_no) {
        long sendTotal = message.getTotal();
        byte sender = message.getSender();
        long seq = message.getSeq();
        logger.info("收到结束标识包，发送端标识：{},发送端结束序号：{},broker end seq_no:{}", sender,seq,seq_no);
        if(sender ==1){
            //判断收到的数量是否与发送的数量相等 只适用于全部收到的
            if(sendTotal != total1){
                logger.error("producer1 send total1:{},receive total count1: [{}],不相等", sendTotal,total1);
            }else{
                logger.info("producer1 send total1:{},receive total count1: [{}]，相等", sendTotal,total1);
            }
            //相关数据清零 方便sub端不需要重启
            expectSeq1 = 0;
            total1 =0 ;
            lastSeqNo1 = 0;
        }
        if(sender ==2){
            //判断收到的数量是否与发送的数量相等 只适用于全部收到的
            if(sendTotal != total2){
                logger.error("producer2 send total2:{},receive total count2: [{}],不相等", sendTotal,total2);
            }else{
                logger.info("producer2 send total2:{},receive total count12: [{}]，相等", sendTotal,total2);
            }
            expectSeq2 = 0;
            total2 =0 ;
            lastSeqNo2 = 0;
        }

    }

    @Override
    protected void validationSendOrderAndReceiveOrder(AmqpMessage message) {
        long seq = message.getSeq();
        byte sender = message.getSender();
        //保证顺序消费 其实这里不严格 严格来说  是以broker收到的顺序为准
        if(sender ==1){
            if(seq != expectSeq1+1 ){
                logger.error("producer1 seq error, seq should be [{}] ,but is [{}]", expectSeq1+1, seq);
            }
            expectSeq1++;
        }
        if(sender ==2){
            if(seq != expectSeq2+1 ){
                logger.error("producer2 seq error, seq should be [{}] ,but is [{}]", expectSeq2+1, seq);
            }
            expectSeq2++;
        }
    }
}
