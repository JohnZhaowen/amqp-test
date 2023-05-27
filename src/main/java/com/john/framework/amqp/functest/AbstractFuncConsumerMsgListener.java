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

    private String queueFileName;

    //实际接收发送端1收到的总数量
    protected long total1;

    //实际接收发送端2收到的总数量
    protected long total2;

    //发送端1定序的序号
    protected long lastSeqNo1;

    //发送端2定序的序号 只有用例9场景下才有发送2 其他都只有一个发送端
    protected long lastSeqNo2;

    //broker定序序号
    protected volatile long seqNo;

    public AbstractFuncConsumerMsgListener(TestCaseEnum testCaseEnum){
        this.testCaseEnum = testCaseEnum;
        this.queueFileName = EnvironmentUtils.getSeqNoFileName(testCaseEnum);
    }

    //抽象类完成 顺序检验，body验证，
    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        if(testCaseEnum.durable&&seq_no != seqNo+1){
            logger.error("broker seq error, broker seq should be [{}] ,but is [{}]", seqNo+1, seq_no);
        }
        seqNo = seq_no;
        AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
        try {
            JavaStruct.unpack(packet, pMsgbuf);
            //只有持久化才会保存seq_no
            if(testCaseEnum.durable){
                FileUtils.writeSeqNo(queueFileName,seq_no);
            }
            byte sender =packet.getSender();
            long seq = packet.getSeq();
            byte[] md5 = packet.getMd5();
            byte[] body = packet.getBody();
            byte endMark = packet.getEndMark();
            //生产者1发送
            if (sender == 1) {
                //第1条消息消费的发送端序号 打印日志
                if(lastSeqNo1 == 0){
                    logger.info("开始消费发送端消息，发送端标识：{},发送端序号：{}, broker seq_no:{}", sender,seq,seq_no);
                }
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
                    //仅限于 9和10案例
                    if(testCaseEnum.testCaseId==9||testCaseEnum.testCaseId==10||
                            (testCaseEnum.durable&&testCaseEnum.testCaseId==12)){
                        if(seq <= lastSeqNo1 ){
                            logger.error("producer1 seq error,  seq[{}] should be > lastSeqNo1 [{}] ,but not", seq, lastSeqNo1);
                        }
                    }
                    //放置值
                    lastSeqNo1 = seq;
                }
            }
            //生产者2发送
            if (sender == 2) {
                //第1条消息消费的发送端序号 打印日志
                if(lastSeqNo2 == 0){
                    logger.info("开始消费发送端消息，发送端标识：{},发送端序号：{}, broker seq_no:{}", sender,seq,seq_no);
                }
                //发送端结束
                if(endMark == 1){
                    //结束标识
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
                    //仅限于 9和10案例
                    if(testCaseEnum.testCaseId==9||testCaseEnum.testCaseId==10||
                            (testCaseEnum.durable&&testCaseEnum.testCaseId==12)) {
                        if (seq <= lastSeqNo2) {
                            logger.error("producer2 seq error,  seq[{}] should be > lastSeqNo2 [{}] ,but not", seq, lastSeqNo2);
                        }
                    }
                    //放置值
                    lastSeqNo2 = seq;
                }
            }
        } catch (StructException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mark(long seqNo) {
        //只有在持久化 并且是 total1 == 0 的情况下 也就是
        // 重启情况下设置 如果断线重连不会设置 因为断线重连之后程序状态的值还是存在的
        // 此种情况下是当前还没有消费完上一次发送的
        if(testCaseEnum.durable&&total1==0){
            this.total1 = seqNo;
            this.seqNo = seqNo;
            logger.info("持久化情况下，边接成功能后设置 total1接收的总数：{}，se",total1);
        }
    }

    @Override
    public void onDisConnectMark() {
        logger.info("连接时当前内部消费消息情况，producer1 receive total1:{}, last sender seq1:{}",total1,lastSeqNo1);
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
