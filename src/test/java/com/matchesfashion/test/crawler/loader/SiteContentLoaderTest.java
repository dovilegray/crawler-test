package com.matchesfashion.test.crawler.loader;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class SiteContentLoaderTest {

    @Test
    void buildsRedirectLinkCorrectly() throws URISyntaxException {
        SiteContentLoader loader = new SiteContentLoader("", 1);

        URI originalUri = new URI("http://example.com/link/to/resource");
        String redirectPath = "/redirect/location";

        String redirectLink = loader.getAbsoluteRedirectLink(originalUri, redirectPath);
        assertEquals("http://example.com/redirect/location", redirectLink);
    }
}