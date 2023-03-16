package com.kingstar.test;

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
        byte[] unzip = Snappy.uncompress(zip);
        byteBuffer1.put(unzip,0,8);
        byteBuffer1.flip();
        long start1 = byteBuffer1.getLong();
        byteBuffer1.clear();
        System.out.println(start1);
    }
}
