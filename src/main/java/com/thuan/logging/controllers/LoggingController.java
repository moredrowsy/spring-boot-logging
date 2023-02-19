package com.thuan.logging.controllers;

import com.thuan.logging.config.GlobalMap;
import com.thuan.logging.model.LogSettings;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {
    @PostMapping("/log-settings")
    public LogSettings setLogging(@RequestBody LogSettings logSettings) {
        GlobalMap.set("logging", logSettings.getLogging());
        return logSettings;
    }
}
