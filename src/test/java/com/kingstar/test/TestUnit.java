package com.kingstar.test;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.utils.MD5Utils;
import com.john.framework.amqp.utils.MessageBodyGenerator;
import com.kingstar.struct.JavaStruct;
import com.kingstar.struct.StructException;
import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/3/16
 */
public class TestUnit {

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
    private ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(8);

    @Test
    public void test2() throws IOException {
        byte[] data = new byte[2*1024*1024];
        long start = System.nanoTime();
        System.out.println(start);
        byteBuffer.putLong(start);
        int end = byteBuffer.limit();
        for (int i = 0; i < end; i++) data[i] = byteBuffer.get(i);
        byte[] zip = Snappy.compress(data);
        byteBuffer.clear();
        System.out.println(zip.length);
        System.out.println(Snappy.isValidCompressedBuffer(zip));
        System.out.println(Snappy.isValidCompressedBuffer(data));
        byte[] unzip = Snappy.uncompress(zip);
        byteBuffer1.put(unzip,0,8);
        byteBuffer1.flip();
        long start1 = byteBuffer1.getLong();
        byteBuffer1.clear();
        System.out.println(start1);
    }

    //@Test
    public void testSnappy() throws IOException {
        byte[] data = new byte[2*1024*1024];
        for(int j=0;j<10000;j++){
            long start = System.nanoTime();
            System.out.println(start);
            byteBuffer.putLong(start);
            int end = byteBuffer.limit();
            for (int i = 0; i < end; i++) data[i] = byteBuffer.get(i);
            byte[] zip = Snappy.compress(data);
            byteBuffer.clear();
            System.out.println(zip.length);
            System.out.println(Snappy.isValidCompressedBuffer(zip));
            System.out.println(Snappy.isValidCompressedBuffer(data));
            byte[] unzip = Snappy.uncompress(zip);
            byteBuffer1.put(unzip,0,8);
            byteBuffer1.flip();
            long start1 = byteBuffer1.getLong();
            byteBuffer1.clear();
            System.out.println(start1);
        }
    }

    @Test
    public void test3(){
        System.out.println(byteBuffer.getLong());
    }

    @Test
    public void test1(){
        int i=0;
        i++;
        System.out.println(i);
    }

    @Test
    public void test4(){
        AmqpMessage amqpMessage =new AmqpMessage(1500);

        amqpMessage.setSeq(1);
        amqpMessage.setSender((byte)1);
        amqpMessage.setBody(MessageBodyGenerator.generate(amqpMessage.getBody().length));
        amqpMessage.setMd5(MD5Utils.md5ForByte(amqpMessage.getBody()));
        byte[] pack = null;
        try {
            pack = JavaStruct.pack(amqpMessage);
        } catch (StructException e) {
            e.printStackTrace();
        }

        AmqpMessage amqpMessage1 =new AmqpMessage(pack.length);
        try {
            JavaStruct.unpack(amqpMessage1,pack);
            System.out.println(amqpMessage1.getSeq()==amqpMessage.getSeq());
        } catch (StructException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5(){
        byte[]  md1= new byte[1500-134];
        byte[] md2 = MessageBodyGenerator.generate(md1.length);
        System.out.println(md2.length);
        byte[] md3 = MD5Utils.md5ForByte(md2);
        System.out.println(md3.length);
    }
}
