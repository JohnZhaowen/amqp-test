package com.john.framework.amqp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String md5(byte[] srcBytes) {

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] digestBytes = md5.digest(srcBytes);
            /**
             * 当srcBytes较大时：可以将srcBytes进行切分，然后循环update
             * md5.update(srcBytes);
             * md5.digest();
             */
            //md5 hex string: 4ff1dfc5c3f2f3ebc03a7747b6453597
            return converBytes2HexStr(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String converBytes2HexStr(byte[] bytes) {

        StringBuilder res = new StringBuilder();
        for (byte b : bytes) {
            //获取b的补码的后8位
            String hex = Integer.toHexString((int) b & 0xFF);
            if (hex.length() == 1) {
                res.append("0");
            }
            res.append(hex);
        }
        return res.toString();
    }

}
