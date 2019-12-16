package ru.devpav.sitemap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SitemapParserTest {

    private final SitemapParser sitemapParser = new SitemapParser();

    private static final String TEST_RESOURCE = "http://cnews.ru";


    @Test
    public void testParseAndVersionsOfElement() {
        long startTime = System.nanoTime();

    }

}