package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.EnvironmentUtils;
import com.john.framework.amqp.utils.FileUtils;
import com.john.framework.amqp.utils.MD5Utils;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/5/16
 */
public abstract class AbstractFuncConsumerMsgListener implements IMsgListener {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFuncConsumerMsgListener.class);

    protected TestCaseEnum testCaseEnum;

    //接收端1收到的总数量
    protected long total1;

    //接收端2收到的总数量
    protected long total2;

    protected long lastSeqNo1;

    protected long lastSeqNo2;

    public AbstractFuncConsumerMsgListener(TestCaseEnum testCaseEnum){
        this.testCaseEnum = testCaseEnum;
    }

    //抽象类完成 顺序检验，body验证，
    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
        try {
            JavaStruct.unpack(packet, pMsgbuf);
            //只有持久化才会保存seq_no
            if(testCaseEnum.durable){
                if(total1 ==0) logger.info("current consumer start sender seq_no：{},broker seq_no:{}",
                        packet.getSeq(),seq_no);
                FileUtils.writeSeqNo(EnvironmentUtils.getSeqNoFileName(testCaseEnum),seq_no);
            }
            byte sender =packet.getSender();
            long seq = packet.getSeq();
            byte[] md5 = packet.getMd5();
            byte[] body = packet.getBody();
            byte endMark = packet.getEndMark();
            //生产者1发送
            if (sender == 1) {
                //发送端结束
                if(endMark == 1){
                    //暂时每一条都发送
                    onMsgEnd(packet,seq_no);
                }else{
                    total1++;
                    //验证发送顺序与接收顺序 严格一致  只适用于 全部收到的情况
                    //只适用于功能测试场景9
                    if(testCaseEnum.testCaseId==9){
                        validationSendOrderAndReceiveOrder(packet);
                    }
                    if(testCaseEnum.testCaseId==10){
                        validationRoutingKeyAndBindKey(packet,routingKey);
                    }
                    if (!MD5Utils.md5(body).equals(MD5Utils.converBytes2HexStr(md5))) {
                        logger.error("producer1 body error, md5 should be[{}], but is [{}]", md5, MD5Utils.md5(body));
                    }
                    //保证顺序消费 其实这里不严格 严格来说  是以broker收到的顺序为准
                    if(seq <= lastSeqNo1 ){
                        logger.error("producer1 seq error,  seq[{}] should be > lastSeqNo1 [{}] ,but not", seq, lastSeqNo1);
                    }
                    //放置值
                    lastSeqNo1 = seq;
                }
            }
            //生产者2发送
            if (sender == 2) {

                //发送端结束
                if(endMark == 1){
                    //暂时每一条都发送
                    onMsgEnd(packet,seq_no);
                }else{
                    total2++;
                    //验证发送顺序与接收顺序 严格一致  只适用于 全部收到的情况
                    //只适用于功能测试场景9
                    if(testCaseEnum.testCaseId==9){
                        validationSendOrderAndReceiveOrder(packet);
                    }

                    if (!MD5Utils.md5(body).equals(MD5Utils.converBytes2HexStr(md5))) {
                        logger.error("producer2 body error, md5 should be[{}], but is [{}]", md5, MD5Utils.md5(body));
                    }
                    //保证顺序消费 其实这里不严格 严格来说  是以broker收到的顺序为准
                    if(seq <= lastSeqNo2){
                        logger.error("producer2 seq error,  seq[{}] should be > lastSeqNo2 [{}] ,but not", seq, lastSeqNo2);
                    }
                    //放置值
                    lastSeqNo2 = seq;
                }
            }
        } catch (StructException e) {
            e.printStackTrace();
        }
    }

    //子类需要扩展的 关于结束标识的
    protected abstract void onMsgEnd(AmqpMessage message,long seq_no);

    //验证发送顺序与接收顺序 严格一致  只适用于 全部收到的情况
    //只适用于功能测试场景9
    protected void validationSendOrderAndReceiveOrder(AmqpMessage message){}

    //观察收到的数量
    //只适用于功能测试场景10
    protected void validationRoutingKeyAndBindKey(AmqpMessage message,String routingKey){}
}
