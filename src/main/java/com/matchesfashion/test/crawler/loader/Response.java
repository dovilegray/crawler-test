package com.matchesfashion.test.crawler.loader;

import java.io.InputStream;

public class Response {
    private final ResponseStatus status;
    private final String redirectPath;
    private final InputStream body;

    public Response(ResponseStatus status, String redirectPath, InputStream body) {
        this.status = status;
        this.redirectPath = redirectPath;
        this.body = body;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public InputStream getBody() {
        return body;
    }

    public static Response ok(InputStream body) {
        return new Response(ResponseStatus.OK, null, body);
    }

    public static Response redirect(String redirectPath) {
        return new Response(ResponseStatus.REDIRECT, redirectPath, null);
    }

    public static Response fail() {
        return new Response(ResponseStatus.FAIL, null, null);
    }
}
