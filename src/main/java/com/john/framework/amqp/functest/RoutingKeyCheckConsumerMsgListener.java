package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.AmqpMessage1;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.utils.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingKeyCheckConsumerMsgListener implements IMsgListener {

    private static final Logger logger = LoggerFactory.getLogger(RoutingKeyCheckConsumerMsgListener.class);

    private int recvCount = 0;
    private int lastSeq = 0;

    @Override
    public void onMsg(AmqpMessage msg) {

        if (!(msg instanceof AmqpMessage1)) {
            logger.error("msg format is error, should be instanceof [AmqpMessage1]");
            System.exit(1);
        }

        int seq = ((AmqpMessage1) msg).getSeq();

        String md5 = ((AmqpMessage1) msg).getMd5();

        byte[] body = msg.getBody();


        if (!(seq > lastSeq)) {
            logger.error("seq error");
            System.exit(1);
        }

        if (!MD5Utils.md5(body).equals(md5)) {
            logger.error("producer2 body error, md5 should be[{}], but is [{}]", md5, MD5Utils.md5(body));
            System.exit(1);
        }

        lastSeq = seq;
        recvCount++;

        logger.info("recv count: [{}]", recvCount);
    }

}
