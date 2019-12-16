package ru.devpav.sitemap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.devpav.domain.Link;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/*https://fedpress.ru/sitemap/2019-11-08.xml*/
@Component
@Slf4j
public class SitemapParser {

    private static final String[] requestExtensions = Stream.of("xml", "php").map(String::toLowerCase).toArray(String[]::new);
    private static final String[] keyDate = new String[]{"date", "lastmod"};
    private static final String[] locLink = new String[]{"loc"};


    public Set<Link> parse(Link root) {
        Set<Link> finalizeLinks = new HashSet<>();
        try {
            HttpResponse<String> response;
            response = Unirest.get(String.valueOf(root.getLink())).asString();
            final String body = response.getBody();
            root.setHash(Objects.hash(body));
            finalizeLinks = parse(body, root);
        } catch (Exception ex) {
            return finalizeLinks;
        }

        return finalizeLinks;
    }

    public Set<Link> parse(String text, Link rootLink) {
        final Function<String, String> mapRequire = (url) -> {
            HttpResponse<String> response;
            try {
                response = Unirest.get(url).asString();
            } catch (Exception ignored) {
                return null;
            }
            return response.getBody();
        };

        BlockingQueue<DocumentBuilder> docBuilderCache = new LinkedBlockingQueue<>();
        BlockingQueue<HashSet<Link>> setCache = new LinkedBlockingQueue<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        for (int i = 0, threadSize = Runtime.getRuntime().availableProcessors() * 2; i < threadSize; i++) {
            try {
                setCache.put(new HashSet<>());
                docBuilderCache.put(documentBuilderFactory.newDocumentBuilder());
            } catch (InterruptedException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        parseToCache(text, mapRequire, docBuilderCache, setCache, rootLink);

        return setCache
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private void parseToCache(String urlXml, Function<String, String> deepenFunction,
                              BlockingQueue<DocumentBuilder> docBuilderCache,
                              BlockingQueue<HashSet<Link>> setCache,
                              Link rootLink) {

        if (isNull(docBuilderCache)) {
            throw new RuntimeException("DocumentBuilder wasn't created");
        }

        Document dom = null;
        try {
            DocumentBuilder builder;
            if (docBuilderCache.isEmpty()) {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } else {
                builder = docBuilderCache.take();
            }

            dom = builder.parse(new InputSource(new StringReader(urlXml)));
            docBuilderCache.put(builder);
        } catch (SAXException | IOException e) {
            return;
        } catch (ParserConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }

        if (isNull(dom)) {
            throw new RuntimeException("Root element external document not found");
        }

        parseToCache(dom.getDocumentElement(), deepenFunction, docBuilderCache, setCache, rootLink);
    }


    private void parseToCache(Node root,
                              Function<String, String> deepenFunction,
                              BlockingQueue<DocumentBuilder> docBuilder,
                              BlockingQueue<HashSet<Link>> setCache, Link rootLink) {
        final NodeList childNodes = root.getChildNodes();

        int len = childNodes.getLength();
        final List<Node> docNodes = new ArrayList<>(len);

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            docNodes.add(item);
        }

        docNodes.stream()
                .parallel()
                .filter(Objects::nonNull)
                .map(node -> findNode(node, locLink))
                .filter(Objects::nonNull)
                .forEach(nodeLoc -> {
                    final String nodeValue = nodeLoc.getTextContent();

                    Link link = new Link();
                    link.setTime(new Date().getTime());
                    link.setLink(nodeValue);

                    final boolean isExistsExtension = isExistsExtension(nodeValue, requestExtensions);

                    if (isExistsExtension) {
                        final String body = deepenFunction.apply(nodeValue);
                        link.setSitemap(true);
                        parseToCache(body, deepenFunction, docBuilder, setCache, link);
                    } else {
                        rootLink.setMiddling(true);
                    }

                    HashSet<Link> curSet;

                    if (setCache.isEmpty()) {
                        curSet = new HashSet<>();
                    } else {
                        try {
                            curSet = setCache.take();
                        } catch (InterruptedException e) {
                            curSet = new HashSet<>();
                        }
                    }

                    link.setParent(rootLink);
                    curSet.add(link);

                    try {
                        setCache.put(curSet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean isExistsExtension(String fileName, String[] requestExtensions) {
        if (isNull(fileName)) {
            return false;
        }

        String toLowerCase = fileName.toLowerCase();
        return hasArray(requestExtensions, toLowerCase::contains);
    }

    private Node findNode(Node root, String[] names) {
        final NodeList childNodes = root.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);

            if (isNull(item)) continue;

            final String nodeName = item.getNodeName();

            if (hasArray(names, name -> name.equalsIgnoreCase(nodeName))) {
                return item;
            }
        }

        return null;
    }

    private static boolean hasArray(String[] array, Predicate<String> filter) {
        for (String s : array) {
            if (filter.test(s))
                return true;
        }

        return false;
    }
}
