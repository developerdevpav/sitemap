package ru.devpav.sitemap;

import org.springframework.stereotype.Component;
import ru.devpav.model.Link;
import ru.devpav.model.Resource;
import ru.devpav.model.VersionResource;
import ru.devpav.sitemap.thread_tasks.ChunkThreadJoin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class LocalCacher {

    private static final Map<Integer, SortedSet<VersionResource>> resources = new ConcurrentHashMap<>();

    private final ResourceInformant resourceInformant;

    public LocalCacher(ResourceInformant resourceInformant) {
        this.resourceInformant = resourceInformant;
    }


    public void collect(Collection<String> resourceUrls) {
        Consumer<Collection<String>> consumer = (urls) -> {
            Consumer<String> stringConsumer = (url) -> {
                Resource resource = new Resource();
                resource.setDate(new Date());
                resource.setLink(url);

                final Set<String> sitemapXmlLinks = resourceInformant.getSitemapXmlLinks(url);
                resource.setSitemaps(sitemapXmlLinks);

                final Set<Link> sitemapLinks = resourceInformant.getSitemapLinks(sitemapXmlLinks);
                resource.setLinks(sitemapLinks);

                final int hash = Objects.hash(url);

                if (!resources.containsKey(hash)) {
                    resources.put(hash, new TreeSet<>());
                }

                final SortedSet<VersionResource> versionResources = resources.get(hash);
                versionResources.add(new VersionResource(resource));
            };
            urls.forEach(stringConsumer);
        };

        ChunkThreadJoin.execute(1, resourceUrls, consumer);
    }

    public Map<Integer, SortedSet<VersionResource>> getCache() {
        return resources;
    }

}
