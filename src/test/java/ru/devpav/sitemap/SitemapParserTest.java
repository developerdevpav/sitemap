package ru.devpav.sitemap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ru.devpav.model.VersionResource;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class SitemapParserTest {
    private final SitemapParser sitemapParser = new SitemapParser();
    private final ResourceInformant resourceInformant = new ResourceInformant(sitemapParser);
    private final LocalCacher localCacher = new LocalCacher(resourceInformant);

    private static final String TEST_RESOURCE = "https://fedpress.ru";
    @Test
    public void parseXML() throws MalformedURLException {
        localCacher.collect(Arrays.asList(TEST_RESOURCE, TEST_RESOURCE));
        final Map<Integer, SortedSet<VersionResource>> cache = localCacher.getCache();

        assertNotNull(cache);
        assertFalse(cache.isEmpty());
        assertEquals(cache.size(), 1);
        final SortedSet<VersionResource> versionResources = cache.get(Objects.hash(TEST_RESOURCE));
        assertNotNull(versionResources);
        assertEquals(versionResources.size(), 2);
        assertTrue(versionResources.first().compareTo(versionResources.last()) > 0);
    }
}