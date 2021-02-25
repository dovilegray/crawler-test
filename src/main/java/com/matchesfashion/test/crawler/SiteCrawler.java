package com.matchesfashion.test.crawler;

import com.matchesfashion.test.crawler.loader.ContentLoader;
import com.matchesfashion.test.crawler.loader.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class SiteCrawler {
    private final Logger logger = Logger.getLogger(SiteCrawler.class.getName());

    private final ContentLoader siteContentLoader;
    private final int maxDepth;

    public SiteCrawler(int maxDepth, ContentLoader siteContentLoader) {
        this.maxDepth = maxDepth;
        this.siteContentLoader = siteContentLoader;
    }

    public List<String> crawlSite(String baseUrl) {
        ConcurrentMap<String, Boolean> visited = new ConcurrentHashMap<>();
        visitLink(baseUrl, 1, visited, createLinkFilter(baseUrl));

        return excludeLinksThatFailedToLoad(visited);
    }

    private List<String> excludeLinksThatFailedToLoad(ConcurrentMap<String, Boolean> visited) {
        return visited.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private Predicate<String> createLinkFilter(String baseUrl) {
        return link -> !link.isEmpty() && link.startsWith(baseUrl);
    }

    private void visitLink(String url, int level, ConcurrentMap<String, Boolean> visited, Predicate<String> linkFilter) {
        if (visited.putIfAbsent(url, true) != null)
            return;
        logger.log(Level.INFO, "Visiting {0}", url);

        Response response = siteContentLoader.getContent(url);

        switch (response.getStatus()) {
            case OK -> {
                try {
                    Document document = Jsoup.parse(response.getBody(), "UTF-8", url);
                    if (level < maxDepth) {
                        findLinksInDocument(document, linkFilter)
                                .parallelStream()
                                .forEach(link -> visitLink(link, level + 1, visited, linkFilter));
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to parse link content", e);
                    visited.put(url, false);
                }
            }
            case REDIRECT -> {
                visited.put(url, false);
                String link = response.getRedirectPath();
                if (linkFilter.test(link)) {
                    visitLink(link, level, visited, linkFilter);
                }
            }
            case FAIL -> visited.put(url, false);
        }
    }

    private Set<String> findLinksInDocument(Element document, Predicate<String> linkFilter) {
        return document.getElementsByTag("a")
                .stream()
                .map(this::getLinkUrl)
                .filter(linkFilter)
                .collect(toSet());
    }

    private String getLinkUrl(Element link) {
        String url = link.absUrl("href");
        return trimTrailingSlash(
                trimQueryParams(url));
    }

    private String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String trimQueryParams(String url) {
        int queryParamIndex = url.indexOf("?");
        return queryParamIndex > 0 ? url.substring(0, queryParamIndex) : url;
    }
}
