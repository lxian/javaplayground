package com.playground.hashstore.server.client;

import com.playground.hashstore.server.proto.response.Response;

public class ResultHolder {
    private Response response;

    private ResultHandler resultHandler;

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

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
}
