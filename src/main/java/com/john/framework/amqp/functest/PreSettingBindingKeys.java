package com.john.framework.amqp.functest;

import java.util.Random;

public class PreSettingBindingKeys {

    private static final String[] PRESETTING_BINGING_KEYS = {
            "pilot.default.JET.SZ.*.*.*.SZEX.*.*",
            "pilot.default.JET.SZ.*.*.*.CFETS.*.*",
            "pilot.default.JET.SZ.*.*.*.bond.*.*",
            "pilot.default.JET.SZ.*.*.*.fx.*.*",
            "pilot.default.JET.SZ.*.*.*.dimple.*.*",
            "pilot.default.JET.SZ.*.*.*.ENDMARK.*.*",

    };

    public static String[] getPreSettingBindingKeys() {
        return PRESETTING_BINGING_KEYS;
    }

    public static String getPreSettingBindingKeyById(int id) {
        return PRESETTING_BINGING_KEYS[id];
    }

    public static String generate() {
        return PRESETTING_BINGING_KEYS[Math.abs(new Random().nextInt()) % 5];
    }

}
