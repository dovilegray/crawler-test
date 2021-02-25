package com.matchesfashion.test.crawler.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SiteContentLoader implements ContentLoader {
    private final Logger logger = Logger.getLogger(SiteContentLoader.class.getName());

    private final HttpClient client;
    private final String userAgent;
    private final Semaphore requestRateController;

    public SiteContentLoader(String userAgent, Integer requestConcurrency) {
        this.userAgent = userAgent;
        this.client = HttpClient.newBuilder().build();
        this.requestRateController = new Semaphore(requestConcurrency);
    }

    @Override
    public Response getContent(String url) {
        try {
            requestRateController.acquire();

            URI uri = URI.create(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .header("User-Agent", userAgent)
                    .build();

            HttpResponse<InputStream> responseStream = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (responseStream.statusCode() == 200) {
                return Response.ok(responseStream.body());
            }
            if (responseStream.statusCode() == 302) {
                return responseStream.headers().firstValue("Location")
                        .map(redirectPath -> getAbsoluteRedirectLink(uri, redirectPath))
                        .map(Response::redirect)
                        .orElseGet(Response::fail);
            }
        } catch (InterruptedException | IOException e) {
            logger.log(Level.WARNING, "Failed to load link content", e);
        } finally {
            requestRateController.release();
        }
        return Response.fail();
    }

    String getAbsoluteRedirectLink(URI uri, String redirectPath) {
        return uri.toString().replace(uri.getPath(), redirectPath);
    }
}
