package ru.devpav.sitemap;

import org.springframework.stereotype.Component;
import ru.devpav.model.Link;
import ru.devpav.model.Resource;
import ru.devpav.model.VersionResource;
import ru.devpav.sitemap.thread_tasks.ChunkThreadJoin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

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

                boolean isFirstElement = !resources.containsKey(hash);

                if (isFirstElement) {
                    resources.put(hash, new TreeSet<>());
                }

                final SortedSet<VersionResource> versionResources = resources.get(hash);

                if (!isFirstElement) {
                    final Set<Resource> resources = versionResources.stream()
                            .filter(Objects::nonNull)
                            .map(VersionResource::getResource)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    resource.setLinks(diffResourceLinks(resource, resources));
                }

                final VersionResource versionResource = new VersionResource(resource, new Date().getTime());

                versionResources.add(versionResource);
            };
            urls.forEach(stringConsumer);
        };

        ChunkThreadJoin.execute(1, resourceUrls, consumer);
    }

    public Map<Integer, SortedSet<VersionResource>> getCache() {
        return resources;
    }


    public Set<Link> getLastLinks(String url){
        final SortedSet<VersionResource> versionResources = resources.get(Objects.hash(url));

        if (isNull(versionResources)) {
            return Collections.emptySet();
        }

        final VersionResource first = versionResources.first();

        if (isNull(first)) {
            return Collections.emptySet();
        }

        final Resource resource = first.getResource();

        if (isNull(resource)) {
            return Collections.emptySet();
        }

        if (isNull(resource.getLinks())) {
            return Collections.emptySet();
        }

        return resource.getLinks();
    }

    public Set<Link> getLinksBetween(String url, Long fromTime, Long toTime){
        final SortedSet<VersionResource> versionResources = resources.get(Objects.hash(url));

        if (isNull(versionResources)) {
            return Collections.emptySet();
        }

        final Predicate<VersionResource> timeBetween = (versionResource) -> {
            final Long time = versionResource.getTime();
            return fromTime < time && time < toTime;
        };

        return versionResources.stream()
                .filter(Objects::nonNull)
                .filter(timeBetween)
                .map(VersionResource::getResource)
                .map(Resource::getLinks)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Link> diffResourceLinks(Resource newResource, Set<Resource> oldResource) {
        final Set<Link> newLinks = newResource.getLinks();
        final Set<Link> oldLinks = oldResource.stream()
                .filter(Objects::nonNull)
                .map(Resource::getLinks)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return diffLinks(newLinks, oldLinks);
    }

    private Set<Link> diffLinks(Set<Link> newLink, Set<Link> oldLink) {
        Set<Link> copySet = new HashSet<>(newLink);
        copySet.removeAll(oldLink);
        return copySet;
    }

}
