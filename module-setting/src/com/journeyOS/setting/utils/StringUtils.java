package com.journeyOS.setting.utils;

public class StringUtils {
    public static String hideId(String id) {
        StringBuilder stringBuilder = new StringBuilder(id);
        stringBuilder.replace(4, id.length() - 4, "****");
        return stringBuilder.toString().toUpperCase();
    }
}
