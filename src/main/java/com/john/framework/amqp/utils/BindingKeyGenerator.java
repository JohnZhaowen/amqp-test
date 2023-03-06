package com.john.framework.amqp.utils;


import org.slf4j.helpers.MessageFormatter;

import java.util.Random;

public class BindingKeyGenerator {

    private static final String BINDING_KEY = "pilot.default.JET.SZ.*.*.*.{}.*.*";

    private static final String[] EXCHANGES = {"ENDMARK", "SZEX", "SHEX", "CFETS", "gold", "bond", "xbond", "fx", "option", "dimple"};

    public static String generate() {

        String[] arsg = new String[1];
        arsg[0] = EXCHANGES[Math.abs(new Random().nextInt()) % 10];
        return MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
    }

    public static String generateEndMark() {

        String[] arsg = new String[1];
        arsg[0] = EXCHANGES[0];
        return MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
    }

    public static void main(String[] args) {
        System.out.println(generate());
    }


}
