package com.matchesfashion.test.sitemap.filter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class LinkLevelFilterTest {

    @Test
    void filtersFirstLevelLinkCorrectly() {
        List<String> links = asList(
                "https://www.matchesfashion.com/womens/designers",
                "https://www.matchesfashion.com/womens"
        );
        LinkLevelFilter generator = new LinkLevelFilter();
        List<String> includedLinks = generator.filterLinks(links, 1);

        assertEquals(1, includedLinks.size());
        assertIterableEquals(
                singletonList("https://www.matchesfashion.com/womens"),
                includedLinks);
    }

    @Test
    void filtersSecondLevelLinksCorrectly() {
        List<String> links = asList(
                "https://www.matchesfashion.com/womens/designers",
                "https://www.matchesfashion.com/womens/designers/cabana-magazine",
                "https://www.matchesfashion.com/womens/ski-studio",
                "https://www.matchesfashion.com/womens"
        );
        LinkLevelFilter generator = new LinkLevelFilter();
        List<String> includedLinks = generator.filterLinks(links, 2);

        assertEquals(3, includedLinks.size());
        assertIterableEquals(
                asList(
                        "https://www.matchesfashion.com/womens",
                        "https://www.matchesfashion.com/womens/designers",
                        "https://www.matchesfashion.com/womens/ski-studio"
                ),
                includedLinks);
    }

    @Test
    void identifiesSecondLevelLinkWithComplexPathCorrectly() {
        List<String> links = asList(
                "https://www.matchesfashion.com/womens/just-in/just-in-this-month",
                "https://www.matchesfashion.com/womens"
        );
        LinkLevelFilter generator = new LinkLevelFilter();
        List<String> includedLinks = generator.filterLinks(links, 2);

        assertEquals(2, includedLinks.size());
        assertIterableEquals(
                asList(
                        "https://www.matchesfashion.com/womens",
                        "https://www.matchesfashion.com/womens/just-in/just-in-this-month"
                ),
                includedLinks);
    }

    @Test
    void filtersThirdLevelLinksCorrectly() {
        List<String> links = asList(
                "https://www.matchesfashion.com/womens/shop/shoes",
                "https://www.matchesfashion.com/womens/shop/shoes/boots/ankle-boots",
                "https://www.matchesfashion.com/womens/shop/bags",
                "https://www.matchesfashion.com/womens/shop/shoes/boots",
                "https://www.matchesfashion.com/womens"
        );

        LinkLevelFilter generator = new LinkLevelFilter();
        List<String> includedLinks = generator.filterLinks(links, 3);

        assertEquals(4, includedLinks.size());
        assertIterableEquals(
                asList(
                        "https://www.matchesfashion.com/womens",
                        "https://www.matchesfashion.com/womens/shop/bags",
                        "https://www.matchesfashion.com/womens/shop/shoes",
                        "https://www.matchesfashion.com/womens/shop/shoes/boots"
                        ),
                includedLinks);
    }
}