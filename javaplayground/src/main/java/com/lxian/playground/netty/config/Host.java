package com.lxian.playground.netty.config;

import java.util.Map;

public class Host implements Endpoint {

    private String host;

    private Map<String, Endpoint> endpoints;

    @Override
    public boolean match(String url) {
        return false;
    }
}
