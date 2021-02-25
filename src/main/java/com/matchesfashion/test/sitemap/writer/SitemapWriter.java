package com.matchesfashion.test.sitemap.writer;

import java.util.List;

public interface SitemapWriter {
    String write(List<String> visited);
}
