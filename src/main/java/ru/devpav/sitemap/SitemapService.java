package ru.devpav.sitemap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.devpav.domain.Link;
import ru.devpav.domain.Resource;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class SitemapService {

    private final ResourceInformant resourceInformant;
    private final SitemapParser sitemapParser;

    public SitemapService(ResourceInformant resourceInformant, SitemapParser sitemapParser) {
        this.resourceInformant = resourceInformant;
        this.sitemapParser = sitemapParser;
    }


    public Set<Resource> getLinks(Set<String> resourceUrls) {
        return resourceUrls.stream()
                .parallel()
                .map(this::getLinks)
                .collect(Collectors.toSet());
    }

    @Getter
    @Setter
    public class AnalyticResource {
        private String resource;
        private Long sitemap;
        private Long totalLink;
    }

    public Set<String> getStringLink(String url) {
        final Resource resource = getLinks(url);

        return resource.getLinks()
                .stream()
                .filter(link -> !link.getSitemap())
                .map(Link::getLink)
                .collect(Collectors.toSet());
    }

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

        final Set<Link> sets = mapSitemap.stream()
                .map(sitemapParser::parse)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        resource.setLinks(sets);

        return resource;
    }

}
