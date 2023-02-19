package com.thuan.logging.util;

public class StringUtils {
    public static String truncateMessage(String message, int limit) {
        if (message.length() < limit) {
            return message;
        }
        return message.substring(0, limit-3) + "...";
    }
}
