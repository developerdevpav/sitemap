package ru.devpav.sitemap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.devpav.domain.Link;
import ru.devpav.domain.Resource;
import ru.devpav.repository.ResourceRepository;

import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class SitemapService {

    private final ResourceInformant resourceInformant;
    private final SitemapParser sitemapParser;
    private final ResourceRepository resourceRepository;

    public SitemapService(ResourceInformant resourceInformant,
                          SitemapParser sitemapParser,
                          ResourceRepository resourceRepository) {
        this.resourceInformant = resourceInformant;
        this.sitemapParser = sitemapParser;
        this.resourceRepository = resourceRepository;
    }


    @Getter
    @Setter
    public static class AnalyticResource {
        private String resource;
        private Long sitemap;
        private Long totalLink;
    }


    public Set<Resource> getLinks(Set<String> resourceUrls) {
        return resourceUrls.stream()
                .parallel()
                .map(this::getLinks)
                .collect(Collectors.toSet());
    }

    public Set<String> getStringLink(String url) {
        final Resource resource = getLinks(url);

        return resource.getLinks()
                .stream()
                .filter(link -> !link.getSitemap())
                .map(Link::getLink)
                .collect(Collectors.toSet());
    }

    @Transactional
    public AnalyticResource getAnalyticResource(String url) {
        final Resource resource = getLinks(url);

        AnalyticResource analyticResource = new AnalyticResource();

        analyticResource.setResource(url);

        analyticResource.setSitemap(
                resource.getLinks().stream()
                .filter(Link::getSitemap)
                .count()
        );

        analyticResource.setTotalLink((long) resource.getLinks().size());

        return analyticResource;
    }

    public Resource getLinks(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        log.info("Start parse url: {}", url);

        Resource resource = new Resource();
        resource.setTime(new Date().getTime());
        resource.setLink(url);

        final Set<String> sitemapXmlLinks = resourceInformant.getSitemapXmlLinks(url);

        final Set<Link> mapSitemap = sitemapXmlLinks.stream()
                .map(it -> {
                    Link link = new Link();
                    link.setLink(it);
                    link.setTime(new Date().getTime());
                    link.setHash(0);
                    link.setSitemap(true);
                    return link;
                }).collect(Collectors.toSet());

        final Set<Link> sets = mapSitemap
                .parallelStream()
                .map(sitemapParser::parse)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        log.info("Resource {} has {} LINKS", url, sets.size());


        final Resource cachedResource = resourceRepository.findByLink(resource.getLink());

        if (Objects.nonNull(cachedResource)) {
            log.info("Resource exists in cache {}", url);
            final Set<Link> oldLinks = cachedResource.getLinks();
            final Set<Link> links = diffLinks(sets, oldLinks);
            log.info("Resource {} has [SIZE: {}] DIFF LINKS", url, links.size());
            cachedResource.getLinks().addAll(links);
            return resourceRepository.saveAndFlush(cachedResource);
        } else {
            log.info("Resource doesn't exists in cache {}", url);
            resource.setLinks(sets);
        }

        return resourceRepository.saveAndFlush(resource);
    }

    public Set<Link> diffLinks(Set<Link> newLinks, Set<Link> oldLinks) {
        final Map<String, Link> oldMap = oldLinks.stream()
                .collect(Collectors.toMap(Link::getLink, link -> link));

        return newLinks.stream()
                .filter(link -> oldMap.containsKey(link.getLink()))
                .collect(Collectors.toSet());
    }

}
