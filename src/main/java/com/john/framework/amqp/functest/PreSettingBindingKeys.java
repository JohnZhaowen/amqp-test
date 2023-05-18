package com.john.framework.amqp.functest;

import java.util.Random;

public class PreSettingBindingKeys {

    private static final String[] PRESETTING_BINGING_KEYS = {
            "pilot.default.JET.SZ.FUT.*.*.*.*.*",
            "pilot.default.JET.SZ.*.IRS.*.*.*.*",
            "pilot.default.JET.SZ.FUT.IRS.qb.*.*.*",
            "pilot.default.JET.SZ.*.IRS.*.dimple.*.*",
            "pilot.default.JET.SZ.*.*.*.dimple.100031.*"

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
