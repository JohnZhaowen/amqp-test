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

    //总的数量
    protected long total;

    private long lastSeqNo1;

    private long lastSeqNo2;

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
                FileUtils.writeSeqNo(EnvironmentUtils.getSeqNoFileName(testCaseEnum),seq_no);
            }
            byte sender =packet.getSender();
            long seq = packet.getSeq();
            byte[] md5 = packet.getMd5();
            byte[] body = packet.getBody();
            byte endMark = packet.getEndMark();
            //不管是一个发送端还是2个发送端 约定只发送一次endMark 并且 sender=0 代表不是任何一个producer
            if(endMark == 1){
                //暂时每一条都发送
                onMsgExt(packet);
            }else{
                total++;
            }
            //生产者1发送
            if (sender == 1) {
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
            //生产者2发送
            if (sender == 2) {
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
        } catch (StructException e) {
            e.printStackTrace();
        }
    }

    //子类需要扩展的 关于结束标识的
    protected abstract void onMsgExt(AmqpMessage message);

    //验证发送顺序与接收顺序 严格一致  只适用于 全部收到的情况
    protected void val(AmqpMessage message){};
}
