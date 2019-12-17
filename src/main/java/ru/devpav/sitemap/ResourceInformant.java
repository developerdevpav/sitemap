package ru.devpav.sitemap;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.devpav.util.URLUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ResourceInformant {

    private static final Integer SOCKET_TIME_WAIT = 10000;



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
                .map(URLUtil::substringRegexp)
                .collect(Collectors.toSet());

    }




}
