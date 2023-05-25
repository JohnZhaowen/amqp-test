package com.john.framework.amqp.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SlowConsumerMsgListener implements IMsgListener{

    private static final Logger logger = LoggerFactory.getLogger(SlowConsumerMsgListener.class);

    //初始化标识
    private volatile boolean init = false;

    //延迟打印时间线程
    private Thread latencyThread=null;

    private volatile int count = 0;

    public SlowConsumerMsgListener() {
        init("PerfSlowThread");
    }

    private void init(String threadName){
        if(init){
            logger.info("init LatencyDaemonThread repeat...");
            return ;
        }
        this.startLatencyDaemonThread(threadName);
        init = true;
        logger.info("PerfSlowThread init end...");
    }

    private void startLatencyDaemonThread(String threadName){
        if(init){
            return ;
        }
        try {
            this.latencyThread = new Thread(){
                @Override
                public void run() {
                    //计算时延
                    try {
                        Thread.sleep(1000);
                        while (true){
                            logger.info("current receive count: [{}]", count);
                            Thread.sleep(1000);
                        }
                    }catch (Exception e){
                        logger.error("", e);
                    }
                }
            };
            this.latencyThread.setName(threadName);
            this.latencyThread.setDaemon(true);
            this.latencyThread.start();
        }catch (Exception e){
            logger.error("", e);
        }
    }


    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf, long seq_no) {
        count++;
        try {
            Thread.sleep(new Random().nextInt(500)+500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
