package com.john.framework.amqp.utils;

public class MessageMatherUtils {

    public static boolean match(String routingKey, String bindingKey) {

        String[] rks = routingKey.split("\\.");
        String[] bks = bindingKey.split("\\.");

        if (rks.length != bks.length) {
            return false;
        }

        for (int i = 0; i < rks.length; i++) {
            if (!bks[i].equals("*") && !bks[i].equals(rks[i])) {
                return false;
            }

        }
        return true;

    }

    public static void main(String[] args) {
        String bindingKey = "pilot.default.JET.SZ.FUT.*.*.c.*.e1";
        String routingKey = "pilot.default.JET.SZ.FUT.a.b.c.d.e";

        System.out.println(match(routingKey, bindingKey));
    }

}
