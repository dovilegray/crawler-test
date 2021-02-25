package com.matchesfashion.test;

import com.matchesfashion.test.crawler.SiteCrawler;
import com.matchesfashion.test.crawler.loader.SiteContentLoader;
import com.matchesfashion.test.sitemap.filter.LinkLevelFilter;
import com.matchesfashion.test.sitemap.writer.SitemapWriter;
import com.matchesfashion.test.sitemap.writer.StAXSitemapWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://www.matchesfashion.com/womens";
        String userAgent = "dovile393fk1";
        String outputFile = "sitemap.xml";
        int maxDepth = 3;

        SiteCrawler crawler = new SiteCrawler(maxDepth, new SiteContentLoader(userAgent, 10));
        LinkLevelFilter levelCalculator = new LinkLevelFilter();
        SitemapWriter writer = new StAXSitemapWriter();

        List<String> linksFound = crawler.crawlSite(baseUrl);
        List<String> linksToInclude = levelCalculator.filterLinks(linksFound, maxDepth);
        String sitemap = writer.write(linksToInclude);

        System.out.println(sitemap);
        Path filePath = Files.writeString(Paths.get(outputFile), sitemap);
        System.out.println("Saved sitemap to file: " + filePath.toAbsolutePath().toString());
    }
}
