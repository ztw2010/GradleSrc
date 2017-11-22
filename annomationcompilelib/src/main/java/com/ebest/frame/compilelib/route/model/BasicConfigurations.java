package com.ebest.frame.compilelib.route.model;


import com.ebest.frame.annomationlib.router.annomation.RouteConfig;
import com.ebest.frame.compilelib.route.util.Utils;

public class BasicConfigurations {
    String baseUrl;
    String pack;

    public BasicConfigurations(RouteConfig config) {
        if (config == null) return;

        this.pack = config.pack();
        this.baseUrl = parseBaseUrl(config);
    }

    private String parseBaseUrl(RouteConfig config) {
        if (!Utils.isEmpty(config.baseUrl())) {
            return config.baseUrl();
        }
        return "";
    }
}
