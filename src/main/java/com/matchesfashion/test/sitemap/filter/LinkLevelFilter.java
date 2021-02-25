package com.matchesfashion.test.sitemap.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class LinkLevelFilter {

    public List<String> filterLinks(List<String> links, int maxDepth) {
        Map<String, Integer> linkToLevel = calculateLinkLevels(links);

        return excludeLinksBelowLevel(linkToLevel, maxDepth);
    }

    private Map<String, Integer> calculateLinkLevels(List<String> links) {
        Map<String, Integer> linkToLevel = new HashMap<>();

        Collections.sort(links);

        for (String link : links) {
            int linkLevel = calculateLinkLevel(link, links, linkToLevel);
            linkToLevel.put(link, linkLevel);
        }

        return linkToLevel;
    }

    private int calculateLinkLevel(String link, List<String> allLinks, Map<String, Integer> linkToLevel) {
        for (int i = allLinks.indexOf(link) - 1; i >= 0; i--) {

            String previousLink = allLinks.get(i);
            if (link.startsWith(previousLink)) {
                Integer parentLinkLevel = linkToLevel.get(previousLink);
                return parentLinkLevel + 1;
            }
        }
        return 1;
    }

    private List<String> excludeLinksBelowLevel(Map<String, Integer> linkToLevel, int maxDepth) {
        return linkToLevel.entrySet().parallelStream()
                .filter(entry -> entry.getValue() <= maxDepth)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(toList());
    }
}
