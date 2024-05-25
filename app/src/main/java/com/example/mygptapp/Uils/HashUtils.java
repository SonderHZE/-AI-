package com.example.mygptapp.Uils;

import java.security.MessageDigest;

public class HashUtils {
    public static String MD5Hash(String str) {
        // 使用MD5算法对传入的字符串进行加密
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    result.append("0").append(hex);
                } else {
                    result.append(hex);
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
