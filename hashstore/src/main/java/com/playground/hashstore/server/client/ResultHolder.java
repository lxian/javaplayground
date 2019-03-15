package com.playground.hashstore.server.client;

import com.playground.hashstore.server.proto.response.Response;

public class ResultHolder {
    private Response response;

    public ResultHolder() {
    }

    public ResultHolder(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
