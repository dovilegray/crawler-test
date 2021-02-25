package com.matchesfashion.test.crawler;

import com.matchesfashion.test.crawler.loader.ContentLoader;
import com.matchesfashion.test.crawler.loader.Response;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SiteCrawlerTest {

    @Test
    void excludesLinksThatFailToLoad() {
        ContentLoader contentLoader = url -> Response.fail();
        SiteCrawler crawler = new SiteCrawler(1, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");
        assertEquals(0, links.size());
    }

    @Test
    void visitsRedirectedLinkIfPathIsUnderBaseUrl() {
        ContentLoader contentLoader = url ->
                switch (url) {
                    case "http://example.com/levelone" -> Response.redirect("http://example.com/levelone/redirected");
                    default -> Response.ok(null);
                };
        SiteCrawler crawler = new SiteCrawler(1, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");

        assertEquals(1, links.size());
        assertIterableEquals(
                singletonList("http://example.com/levelone/redirected"),
                links
        );
    }

    @Test
    void excludesRedirectedLinkIfPathIsNotUnderBaseUrl() {
        ContentLoader contentLoader = url ->
                switch (url) {
                    case "http://example.com/levelone" -> Response.redirect("http://example.com/redirected");
                    default -> Response.ok(null);
                };
        SiteCrawler crawler = new SiteCrawler(1, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");

        assertEquals(0, links.size());
    }

    @Test
    void visitsLinksFoundInReturnedContent() {
        ContentLoader contentLoader = url ->
                switch (url) {
                    case "http://example.com/levelone" -> Response.ok(asStream("<html><a href=\"levelone/leveltwo\"/><a href=\"levelone/alsoleveltwo\"/></html>"));
                    default -> Response.ok(null);
                };
        SiteCrawler crawler = new SiteCrawler(2, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");
        Collections.sort(links);

        assertEquals(3, links.size());
        assertIterableEquals(
                asList(
                        "http://example.com/levelone",
                        "http://example.com/levelone/alsoleveltwo",
                        "http://example.com/levelone/leveltwo"
                ),
                links
        );
    }

    @Test
    void excludesLinksFoundInReturnedContentThatAreNotUnderBaseUrl() {
        ContentLoader contentLoader = url ->
                switch (url) {
                    case "http://example.com/levelone" -> Response.ok(asStream("<html><a href=\"levelone/leveltwo\"/><a href=\"otherlevelone\"/></html>"));
                    default -> Response.ok(null);
                };
        SiteCrawler crawler = new SiteCrawler(2, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");
        Collections.sort(links);

        assertEquals(2, links.size());
        assertIterableEquals(
                asList(
                        "http://example.com/levelone",
                        "http://example.com/levelone/leveltwo"
                ),
                links
        );
    }

    @Test
    void stopsTraversingAtConfiguredLinkLevel() {
        ContentLoader contentLoader = url ->
                switch (url) {
                    case "http://example.com/levelone" -> Response.ok(asStream("<html><a href=\"/levelone/leveltwo\"/></html>"));
                    case "http://example.com/levelone/leveltwo" -> Response.ok(asStream("<html><a href=\"/levelone/leveltwo/levelthree\"/></html>"));
                    case "http://example.com/levelone/leveltwo/levelthree" -> Response.ok(asStream("<html><a href=\"/levelone/leveltwo/levelthree/levelfour\"/></html>"));
                    default -> Response.ok(null);
                };
        SiteCrawler crawler = new SiteCrawler(3, contentLoader);

        List<String> links = crawler.crawlSite("http://example.com/levelone");
        Collections.sort(links);

        assertEquals(3, links.size());
        assertIterableEquals(
                asList(
                        "http://example.com/levelone",
                        "http://example.com/levelone/leveltwo",
                        "http://example.com/levelone/leveltwo/levelthree"
                ),
                links
        );
    }

    private InputStream asStream(String content) {
        return new ByteArrayInputStream(content.getBytes());
    }
}