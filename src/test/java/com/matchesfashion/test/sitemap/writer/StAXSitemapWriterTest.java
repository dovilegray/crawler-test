package com.matchesfashion.test.sitemap.writer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class StAXSitemapWriterTest {

    @Test
    void generatesCorrectXmlSitemapFromGivenLinks() {

        List<String> links = asList(
                "https://www.matchesfashion.com/womens",
                "https://www.matchesfashion.com/womens/designers"
        );

        StAXSitemapWriter writer = new StAXSitemapWriter();
        String sitemapContent = writer.write(links);

        String[] actualLines = sitemapContent.split("\n");
        assertLinesMatch(
                asList(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                        "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">",
                        "	<url><loc>https://www.matchesfashion.com/womens</loc></url>",
                        "	<url><loc>https://www.matchesfashion.com/womens/designers</loc></url>",
                        "</urlset>"
                ),
                asList(actualLines));
    }
}