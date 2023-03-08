package com.john.framework.amqp.utils;


import com.john.framework.amqp.testcase.TestContents;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;
import java.util.Random;

public class RoutingKeyGenerator {

    //    private static final String template = "pilot.default.JET.SZ.{instrument_class}.{source_system_id}.{instrument_subclass}.{exch}.{product_id}.{exch_product_id}";
    private static final String TEMPLATE = "pilot.default.JET.SZ.{}.{}.{}.{}.{}.{}";

    public static String generateEndMsgRoutingKey() {

        String[] args = new String[6];
        for (int i = 0; i < 6; i++) {
            if (i == 3) {
                args[i] = TestContents.EXCHANGES[0];
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
                String exch = TestContents.EXCHANGES[Math.abs(new Random().nextInt()) % 10];
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
        for(int i=0;i<10000000;i++){
            String[] arr = generate().split("\\.");
            if(arr.length!=10){
                System.out.println(Arrays.toString(arr));
                break;
            }
        }

    }


}
