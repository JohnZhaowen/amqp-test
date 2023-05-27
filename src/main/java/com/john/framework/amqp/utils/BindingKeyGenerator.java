package com.john.framework.amqp.utils;


import com.john.framework.amqp.testcase.TestContents;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;
import java.util.Random;

public class BindingKeyGenerator {

    private static final String BINDING_KEY = "pilot.default.JET.SZ.*.*.*.{}.*.*";

    private static final String[] RANDOM_BINGING_KEYS={
            "pilot.default.JET.SZ.FUT.*.*.*.*.*",
            "pilot.default.JET.SZ.*.IRS.*.*.*.*",
            "pilot.default.JET.SZ.CMDTY.*.pilot.ENDMARK.*.*",
            "pilot.default.JET.SZ.*.IRS.*.dimple.*.*",
            "pilot.default.JET.SZ.*.*.*.dimple.100031.*",
            "pilot.default.JET.SZ.FUT.IRS.qb.*.*.*",
            "pilot.default.JET.SZ.FUT.IRS.qb.dimple.100034.200089",
            "pilot.default.JET.SZ.FUT.*.*.*.*.200030",
            "pilot.default.JET.SZ.*.IRS.*.dimple.*.200089",
            "pilot.default.JET.SZ.FUT.*.*.*.100035.*"

    };


    public static String generate() {
        return RANDOM_BINGING_KEYS[Math.abs(new Random().nextInt()) % 10];
    }

    public static String[] generateAllInOne() {
        String[] allInOne = {"pilot.default.JET.SZ.*.*.*.*.*.*"};
        return allInOne;
    }

    public static String[] generateAll() {
        String[] res = new String[10];
        for (int i = 0; i < 10; i++) {
            String exchange = Contents.EXCHANGES[i];
            String[] arsg = new String[1];
            arsg[0] = exchange;
            res[i] = MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
        }
        return res;
    }

    public static String generateEndMark() {

        String[] arsg = new String[1];
        arsg[0] = Contents.EXCHANGES[9];
        return MessageFormatter.arrayFormat(BINDING_KEY, arsg).getMessage();
    }

    public static void main(String[] args) {
        String[] arr = generateAll();
        System.out.println(Arrays.toString(arr));
    }


}
