package com.john.framework.amqp.utils;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MessageGenerator {

    public static byte[] generateMsgBody(int bytes) {
        return generateMsgBody(bytes, System.currentTimeMillis());
    }

    public static byte[] generateMsgBody(int bytes, long timeStamp) {

        byte[] msgBody = new byte[bytes];
        System.arraycopy(ByteBuffer.allocate(8).putLong(timeStamp).array(), 0, msgBody, 0, 8);

        return msgBody;
    }

    public static long getTimeStamp(byte[] msg) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(msg, 0, 8);
        buffer.flip();

        return buffer.getLong();
    }

    public static LocalDateTime getDateTime(byte[] msg) {
        Timestamp timestamp = new Timestamp(getTimeStamp(msg));
        return timestamp.toLocalDateTime();
    }

}
