package com.john.framework.amqp.utils;


import com.john.framework.amqp.testcase.TestContents;
import org.slf4j.helpers.MessageFormatter;

import java.util.Random;

public class BindingKeyGenerator {

    private static final String BINDING_KEY = "pilot.default.JET.SZ.*.*.*.{}.*.*";


    public static String generate() {

        String[] arsg = new String[1];
        arsg[0] = TestContents.EXCHANGES[Math.abs(new Random().nextInt()) % 10];
        return MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
    }

    public static String[] generateAll() {
        String[] res = new String[10];
        for (int i = 0; i < 10; i++) {
            String exchange = TestContents.EXCHANGES[i];
            String[] arsg = new String[1];
            arsg[0] = exchange;
            res[i] = MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
        }
        return res;
    }

    public static String generateEndMark() {

        String[] arsg = new String[1];
        arsg[0] = TestContents.EXCHANGES[0];
        return MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
    }

    public static void main(String[] args) {
        System.out.println(generate());
    }


}
