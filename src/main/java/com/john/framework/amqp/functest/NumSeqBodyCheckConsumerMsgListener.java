package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.amqp.IMsgListener;
import com.john.framework.amqp.utils.MD5Utils;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumSeqBodyCheckConsumerMsgListener implements IMsgListener {

    private static final Logger logger = LoggerFactory.getLogger(NumSeqBodyCheckConsumerMsgListener.class);

    private int countForProducer1 = 0;

    private int countForProducer2 = 0;

    @Override
    public void onMsg(String routingKey, byte[] pMsgbuf,long seq_no) {

        AmqpMessage packet = new AmqpMessage(pMsgbuf.length);
        try {
            JavaStruct.unpack(packet, pMsgbuf);

            byte sender =packet.getSender();

            long seq = packet.getSeq();

            String md5 = packet.getMd5();

            byte[] body = packet.getBody();

            if (sender == 1) {
                if (seq != countForProducer1 + 1) {
                    logger.error("producer1 seq error, should be[{}], but is [{}]", (countForProducer1 + 1), seq);
                    System.exit(1);
                }

                if (!MD5Utils.md5(body).equals(md5)) {
                    logger.error("producer1 body error, md5 should be[{}], but is [{}]", md5, MD5Utils.md5(body));
                    System.exit(1);
                }

                countForProducer1++;
            }

            if (sender == 2) {
                if (seq != countForProducer2 + 1) {
                    logger.error("producer2 seq error, should be[{}], but is [{}]", (countForProducer2 + 1), seq);
                    System.exit(1);
                }

                if (!MD5Utils.md5(body).equals(md5)) {
                    logger.error("producer2 body error, md5 should be[{}], but is [{}]", md5, MD5Utils.md5(body));
                    System.exit(1);
                }

                countForProducer2++;
            }
            logger.info("producer1 count: [{}], producer2 count: [{}]", countForProducer1, countForProducer2);
        } catch (StructException e) {
            e.printStackTrace();
        }

    }

}
