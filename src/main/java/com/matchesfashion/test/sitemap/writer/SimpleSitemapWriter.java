package com.matchesfashion.test.sitemap.writer;

import java.util.List;

public class SimpleSitemapWriter implements SitemapWriter {

    @Override
    public String write(List<String> links) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        builder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">").append("\n");
        for (String link : links) {
            builder.append("\t<url><loc>").append(link).append("</loc></url>").append("\n");
        }
        builder.append("</urlset>");
        return builder.toString();
    }
}
