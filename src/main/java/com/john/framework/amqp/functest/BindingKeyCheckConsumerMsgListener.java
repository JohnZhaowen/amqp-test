package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.utils.MD5Utils;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BindingKeyCheckConsumerMsgListener implements IMsgListener {

    private static final Logger logger = LoggerFactory.getLogger(BindingKeyCheckConsumerMsgListener.class);

    private int recvCount = 0;
    private long lastSeq = 0;

    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf,long seq_no) {

        AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
        try {
            JavaStruct.unpack(packet, pMsgbuf);

            long seq = packet.getSeq();

            String md5 = packet.getMd5();

            byte[] body = packet.getBody();


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
        } catch (StructException e) {
            e.printStackTrace();
        }


    }

}
