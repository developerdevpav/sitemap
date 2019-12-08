package ru.devpav.sitemap;

import kong.unirest.Unirest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class SitemapParserTest {

    private final static String xmlConfiguration = "https://fedpress.ru/sitemap/sitemap.xml";
    private final SitemapParser sitemapParser = new SitemapParser();

    @Test
    public void parseXML() throws MalformedURLException {
        final long nanoTime = System.nanoTime();
        final Set<String> strings = sitemapParser.parse(new URL(xmlConfiguration));
        System.out.println((System.nanoTime() - nanoTime) / 1_000_000_000 + " ");
        assertNotNull(strings);
        assertFalse(strings.isEmpty());
    }
}