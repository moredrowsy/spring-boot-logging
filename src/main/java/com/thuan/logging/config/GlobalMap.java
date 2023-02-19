package com.thuan.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("global")
@Component
public class GlobalMap {
    private static Map<String, String> map = new HashMap<>();

    public static String get(String key) {
        return map.get(key);
    }

    public static void set(String key, String value) {
        map.put(key, value);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        GlobalMap.map = map;
    }
}
