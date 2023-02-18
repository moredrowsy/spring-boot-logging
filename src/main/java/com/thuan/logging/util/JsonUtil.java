package com.thuan.logging.util;

import com.google.gson.Gson;

public class JsonUtil {
    public static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
