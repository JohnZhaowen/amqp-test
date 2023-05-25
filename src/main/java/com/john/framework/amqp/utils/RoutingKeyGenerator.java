package com.john.framework.amqp.utils;


import com.john.framework.amqp.amqp.SimpleSub;
import com.john.framework.amqp.testcase.TestContents;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RoutingKeyGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SimpleSub.class);

    //    private static final String template = "pilot.default.JET.SZ.{instrument_class}.{source_system_id}.{instrument_subclass}.{exch}.{product_id}.{exch_product_id}";
    private static final String TEMPLATE = "pilot.default.JET.SZ.{}.{}.{}.{}.{}.{}";

    //(2, 2), 5, 10, (100, 100)

    private static String[] routingKeys = new String[5000];

    private static final Random random = new Random();

    static {
        for (int i = 0; i < 5000; i++) {
            String[] args = new String[6];
            args[0] = Contents.INSTRUMENT_CLASS[Math.abs(new Random().nextInt()) % 2];
            args[1] = Contents.SOURCE_SYSTEM_ID[Math.abs(new Random().nextInt()) % 2];
            args[2] = Contents.INSTRUMENT_SUBCLASS[Math.abs(new Random().nextInt()) % 5];
            args[3] = Contents.EXCHANGES[Math.abs(new Random().nextInt()) % 10];

            //交易所选择固定的几个值
            args[4] = Contents.PRODUCTID[Math.abs(new Random().nextInt()) % 100];
            args[5] = Contents.EXTRODUCTID[Math.abs(new Random().nextInt()) % 100];

            routingKeys[i] = generate(args);
        }

    }

    public static String getRandomRoutingKey() {
        //0 - 4999
        //int index = Math.abs(new Random().nextInt()) % 5000;
        int index = random.nextInt(5000);
        return routingKeys[index];
    }

    public static String getRandomRoutingKey1() {
        //0 - 4999
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        int index = threadLocalRandom.nextInt(5000);
        return routingKeys[index];
    }

    public static String generateEndMsgRoutingKey() {

        String[] args = new String[6];
        for (int i = 0; i < 6; i++) {
            if (i == 3) {
                args[i] = Contents.EXCHANGES[9];
            } else {
                args[i] = randomString();
            }
        }

        return generate(args);
    }

    public static String generate() {

        String[] args = new String[6];
        for (int i = 0; i < 6; i++) {
            if (i == 3) {
                String exch = Contents.EXCHANGES[Math.abs(new Random().nextInt()) % 10];
                args[i] = exch;
            } else {
                args[i] = randomString();
            }
        }

        return generate(args);
    }

    public static String generate(String... args) {
        if (args == null || args.length != 6) {
            throw new IllegalArgumentException("bindingKey variable length must be 6, current length is: " + (args == null ? 0 : args.length));
        }

        return MessageFormatter.arrayFormat(TEMPLATE, args).getMessage();
    }

    private static String randomString() {

        int length = Math.abs(new Random().nextInt()) % 10 + 1;
        char[] c = { //62
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
        };

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = Math.abs(new Random().nextInt()) % 62;
            s.append(c[index]);
        }

        return s.toString();
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            getRandomRoutingKey1();
        }
        System.out.println((System.nanoTime()-start)/1000);
    }
}
