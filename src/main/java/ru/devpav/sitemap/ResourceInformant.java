package ru.devpav.sitemap;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.devpav.model.Link;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ResourceInformant {

    private static final Integer SOCKET_TIME_WAIT = 10000;

    private final SitemapParser sitemapParser;

    public ResourceInformant(SitemapParser sitemapParser) {
        this.sitemapParser = sitemapParser;
    }

    public Set<Link> getSitemapLinks(String url) {
        final Set<String> sitemapXmlLinks = getSitemapXmlLinks(url);

        final Function<String, URL> stringToURL = link -> {
            URL linkURL = null;
            try {
                linkURL = new URL(link);
            }catch (Exception ignored) {
                return linkURL;
            }
            return linkURL;
        };

        final BinaryOperator<Set<Link>> mergeSet = (origin, other) -> {
            origin.addAll(other);
            return origin;
        };

        return sitemapXmlLinks.stream()
                .map(stringToURL)
                .filter(Objects::nonNull)
                .map(sitemapParser::parse)
                .reduce(mergeSet)
                .orElse(new HashSet<>());
    }

    public Set<Link> getSitemapLinks(Set<String> xmls) {
        final Function<String, URL> stringToURL = link -> {
            URL linkURL = null;
            try {
                linkURL = new URL(link);
            }catch (Exception ignored) {
                return linkURL;
            }
            return linkURL;
        };

        final BinaryOperator<Set<Link>> mergeSet = (origin, other) -> {
            origin.addAll(other);
            return origin;
        };

        return xmls.stream()
                .map(stringToURL)
                .filter(Objects::nonNull)
                .map(sitemapParser::parse)
                .reduce(mergeSet)
                .orElse(new HashSet<>());
    }

    public Set<String> getSitemapXmlLinks(String url) {
        final String robotUrl = url + "/robots.txt";

        final Set<String> links = new HashSet<>();

        GetRequest getRequest;
        HttpResponse<String> stringHttpResponse;
        HttpStatus responseStatus;

        try {
            getRequest = Unirest.get(robotUrl).socketTimeout(SOCKET_TIME_WAIT);
            stringHttpResponse = getRequest.asString();
            responseStatus = HttpStatus.valueOf(stringHttpResponse.getStatus());
        } catch (Exception ex) {
            return links;
        }

        final String body = stringHttpResponse.getBody();

        boolean isOk = responseStatus.equals(HttpStatus.OK);

        if (!isOk || Objects.isNull(body)) {
            return links;
        }

        final String[] lines = body.toLowerCase().split("\n");

        return Stream.of(lines)
                .filter(line -> line.contains("sitemap"))
                .map(line -> line.replace("sitemap: ", ""))
                .collect(Collectors.toSet());

    }

}
