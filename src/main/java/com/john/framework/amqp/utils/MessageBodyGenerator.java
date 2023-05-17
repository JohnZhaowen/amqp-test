package com.john.framework.amqp.utils;

import java.util.Random;

public class MessageBodyGenerator {

    public static byte[] generate(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static void generate(byte[] src) {
        new Random().nextBytes(src);
    }

    public static void main(String[] args) {
        byte[] bytes = generate(20);

        for (int i = 0; i < bytes.length; i++) {
            System.out.println(i + ": " + bytes[i]);
        }

    }
}
