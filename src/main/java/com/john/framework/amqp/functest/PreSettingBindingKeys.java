package com.john.framework.amqp.functest;

public class PreSettingBindingKeys {

    private static final String[] PRESETTING_BINGING_KEYS = {
            "pilot.default.JET.SZ.FUT.*.*.*.*.*",
            "pilot.default.JET.SZ.*.IRS.*.*.*.*",
            "pilot.default.JET.SZ.CMDTY.*.pilot.ENDMARK.*.*",
            "pilot.default.JET.SZ.*.IRS.*.dimple.*.*",
            "pilot.default.JET.SZ.*.*.*.dimple.100031.*"

    };

    public static String[] getPreSettingBindingKeys() {
        return PRESETTING_BINGING_KEYS;
    }

    public static String getPreSettingBindingKeyById(int id) {
        return PRESETTING_BINGING_KEYS[id];
    }

}
