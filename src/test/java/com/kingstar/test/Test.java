package com.kingstar.test;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.checkerframework.checker.units.qual.A;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/3/7
 */
public class Test {

    public static void main(String[] args) throws StructException {
        AmqpMessage amqpMessage = new AmqpMessage(1500);
        amqpMessage.setTestCaseId(1);
        amqpMessage.setBody(MessageBodyGenerator.generate(amqpMessage.getBody().length));
        long start = System.nanoTime();
        for(int i=0;i<10000;i++){
            amqpMessage.setTimestampInNanos(System.nanoTime());
            byte[] bytes = JavaStruct.pack(amqpMessage);
            AmqpMessage amqpMessage1 = new AmqpMessage(bytes.length);
            JavaStruct.unpack(amqpMessage1,bytes);
        }
        System.out.println((System.nanoTime()-start)/10000000);
    }
}
