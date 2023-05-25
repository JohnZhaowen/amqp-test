package com.john.framework.amqp.amqp;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.utils.FileUtils;
import com.kingstar.messaging.api.APIResult;
import com.kingstar.messaging.api.ErrorInfo;
import com.kingstar.messaging.api.KSKingMQ;
import com.kingstar.messaging.api.KSKingMQSPI;
import com.kingstar.messaging.api.QueueType;
import com.kingstar.messaging.api.ReConnectStatus;
import com.kingstar.messaging.api.ReqSubscribeField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/5/9
 */
public class KSKingMQServerAPI extends KSKingMQSPI implements KSKingMQServerState {

    private static final Logger logger = LoggerFactory.getLogger(KSKingMQServerAPI.class);

    private volatile boolean connect = false;

    //整体订阅成功能与失败
    private volatile boolean subscribe = false;

    private IMsgListener msgListener;

    //案例
    private TestCaseEnum testCaseEnum;
    //绑定的queue
    private String queue;
    //绑定的key
    private String[] bindingKeys;

    private final ConcurrentMap<String,Boolean> allSubscribeMap = new ConcurrentHashMap<>();
    //mq client实例
    private KSKingMQ ksKingMQ;

    public KSKingMQServerAPI(IMsgListener msgListener) {
        Objects.requireNonNull(msgListener);
        this.msgListener = msgListener;
    }

    //适用断线重连的绑定
    public KSKingMQServerAPI(IMsgListener msgListener,
                             TestCaseEnum testCaseEnum,
                             String queue,
                             String[] bindingKeys,
                             KSKingMQ ksKingMQ) {
        this.msgListener = msgListener;
        this.testCaseEnum = testCaseEnum;
        this.queue = queue;
        this.bindingKeys = bindingKeys;
        this.ksKingMQ = ksKingMQ;
    }

    @Override
    public void OnConnected() {
        logger.info("OnConnected callback, sub client connected to broker!");
        connect = true;
        try {
            //重置
            allSubscribeMap.clear();
            if(testCaseEnum!=null&&bindingKeys!=null&&bindingKeys.length!=0&&ksKingMQ!=null){
                Arrays.stream(this.bindingKeys).forEach(s->{
                    allSubscribeMap.put(s,false);
                });
                reqSubscribe();
            }
        }catch (Exception e){
            logger.error("",e);
        }

    }

    //发布订阅
    private void reqSubscribe(){
        //满足一定情况下才会触发订阅
        if(testCaseEnum!=null&&bindingKeys!=null&&bindingKeys.length!=0&&ksKingMQ!=null){
            ReqSubscribeField reqSubscribeField = new ReqSubscribeField();
            //创建订阅topic
            //声明queue
            int offset = -1;
            //关于offset的设置问题 只有功能性测试+持久化的前提下 才会有读offset的问题
            //功能性测试以大于9 为约定
            //offset值说明 -1代表连接上后从连接处消费，0代表从头开始消费，其他大于0的值代表 从断点处消费
            //排除掉 结束标认的
            if(testCaseEnum.testCaseId>=9&&testCaseEnum.durable){
                String fileName = queue+".txt";
                long seq_no = FileUtils.readSeqNo(fileName);
                logger.info("read csv name:{},queue name:{},seq_no:{}",fileName,queue,seq_no);
                offset = (int)seq_no;
            }
            for (int i = 0; i < bindingKeys.length; i++) {
                reqSubscribeField.setCnt(1);
                QueueType queueType = new QueueType();
                queueType.setDurable(testCaseEnum.durable ? 1 : 0);
                queueType.setBindingKey(bindingKeys[i]);
                //最后一个设置为 从文件读取的 seq_no
                if(i==bindingKeys.length-1&&testCaseEnum.durable&&testCaseEnum.testCaseId>=9){
                    queueType.setOffset(offset);
                }else{
                    queueType.setOffset(-1);
                }
                queueType.setQueue(queue);
                reqSubscribeField.setElems(queueType);

                APIResult subResult = ksKingMQ.ReqSubscribe(reqSubscribeField);
                if (subResult.swigValue() != APIResult.SUCCESS.swigValue()) {
                    logger.error("req Subscribe failed! Subscribe queue name:{},bindKey:{},error code:{},error msg:{}",
                            queue, bindingKeys[i], subResult.swigValue(), subResult.toString());
                    break;
                }else{
                    logger.info("req Subscribing! wait a moment! Subscribe queue name:{},bindKey:{}",
                            queue, bindingKeys[i]);
                }
            }
        }
    }

    @Override
    public void OnDisconnected(ReConnectStatus reConnectStatus, ErrorInfo pErrorInfo) {
        logger.warn("OnDisconnected callback, sub client disconnected to broker! error code:{},errMsg:{}",
                pErrorInfo.getErrorId(),pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnRtnSubscribe(String pQueue, ErrorInfo pErrorInfo) {
        if(pErrorInfo.getErrorId()==0){
            setSubscribe(true);
            logger.info("subscribe success! subscribe queue name:{},bindKey:{}",
                    pQueue, pErrorInfo.getErrorMessage());
            if(this.allSubscribeMap.get(pErrorInfo.getErrorMessage())!=null){
                allSubscribeMap.put(pErrorInfo.getErrorMessage(),true);
            }else{
                logger.warn("subscribe exception! subscribe bindKey:{} not exist!",
                        pErrorInfo.getErrorMessage());
            }
        }else
            logger.error("OnRtnSubscribe callback, sub client Subscribed failed ,queue name:{},error id:{},errMsg:{}",
                    pQueue,pErrorInfo.getErrorId(),pErrorInfo.getErrorMessage());
    }

    @Override
    public void OnMessage(String routingKey, byte[] pMsgbuf,long seq_no) {
        msgListener.onMsg(routingKey,pMsgbuf,seq_no);
    }

    @Override
    public boolean connect() {
        return connect;
    }

    @Override
    public boolean subscribe() {
        return subscribe;
    }

    public boolean isAllSubscribeOk(){
        return !allSubscribeMap.containsValue(false);
    }
}
