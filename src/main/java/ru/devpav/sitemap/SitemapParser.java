package ru.devpav.sitemap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/*https://fedpress.ru/sitemap/2019-11-08.xml*/
public class SitemapParser {

    // ВАЖНО!!! Не в коем случае нельзя менять из потоков
    //
    // Не стоит вызывать всегда toLowerCase у элементов массива, сделай их сразу маленькими
    // Лучше использовать массив, будет быстрее, чем HashSet, по которому нужно пройти как по массиву
    private static final String[] requestExtensions = Stream.of("xml", "php").map(String::toLowerCase).toArray(String[]::new);
    private static final String[] keyDate = new String[]{"date", "lastmod"};
    private static final String[] locLink = new String[]{"loc"};

    private final static Logger logger = LoggerFactory.getLogger(SitemapParser.class);

    public Set<String> parse(URL url) {
        Set<String> finalizeLinks = new HashSet<>();
        try {
            HttpResponse<String> response;
            // Как альтернативу быстрому выполнению, найти более низкоуревневый API для получения тела. Чтобы работать эффективнее
            // Использовать пул подключений
            response = Unirest.get(String.valueOf(url)).asString();
            finalizeLinks = parse(response.getBody());
        } catch (Exception ex) {
            return finalizeLinks;
        }

        return finalizeLinks;
    }

    public Set<String> parse(String text) {
        final Function<String, String> mapRequire = (url) -> {
            HttpResponse<String> response;
            try {
                response = Unirest.get(url).asString();
            } catch (Exception ignored) {
                return null;
            }
            return response.getBody();
        };

        LinkedBlockingQueue<DocumentBuilder> docBuilderCache = new LinkedBlockingQueue<DocumentBuilder>();
        LinkedBlockingQueue<HashSet<String>> setCache = new LinkedBlockingQueue<HashSet<String>>();
        // DocumentBuilderFactory НЕ многопоточный и DocumentBuilder
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        for (int i = 0, threadSize = Runtime.getRuntime().availableProcessors() * 2; i < threadSize; i++) {
            try {
                // Первичное заполнение кэша
                setCache.put(new HashSet<String>());
                docBuilderCache.put(documentBuilderFactory.newDocumentBuilder());
            } catch (InterruptedException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        parseToCache(text, mapRequire, docBuilderCache, setCache);
        return setCache
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private void parseToCache(String urlXml, Function<String, String> deepenFunction,
                              LinkedBlockingQueue<DocumentBuilder> docBuilderCache,
                              LinkedBlockingQueue<HashSet<String>> setCache) {

        if (isNull(docBuilderCache)) {
            throw new RuntimeException("DocumentBuilder wasn't created");
        }

        Document dom = null;
        try {
            DocumentBuilder builder;
            if(docBuilderCache.isEmpty()){
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            }else{
                builder = docBuilderCache.take();
            }

            dom = builder.parse(new InputSource(new StringReader(urlXml)));
            docBuilderCache.put(builder);
        } catch (SAXException | IOException e) {
            return;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isNull(dom)) {
            throw new RuntimeException("Root element external document not found");
        }

        parseToCache(dom.getDocumentElement(), deepenFunction, docBuilderCache, setCache);
    }


    private void parseToCache(Node root, Function<String, String> deepenFunction, LinkedBlockingQueue<DocumentBuilder> docBuilder,
                              LinkedBlockingQueue<HashSet<String>> setCache) {
        final NodeList childNodes = root.getChildNodes();

        int len = childNodes.getLength();
        final ArrayList<Node> docNodes = new ArrayList<Node>(len);
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            docNodes.add(item);
        }

        docNodes
                .stream()
                .parallel()
                .map(item -> findNodeLoc(item, locLink))
                .filter(Objects::nonNull)
                .forEach(loc -> {
                    final String nodeValue = loc.getTextContent();

                    final boolean isExistsExtension = isExistsExtension(nodeValue, requestExtensions);

                    if (isExistsExtension) {
                        final String body = deepenFunction.apply(nodeValue);
                        parseToCache(body, deepenFunction, docBuilder, setCache);
                    } else {
                        HashSet<String> curSet = null;
                        // Если переделать алгоритм и брать на каждый потом 1 раз мапу и заполнять, должно стать быстрее
                        if(setCache.isEmpty()){
                            curSet = new HashSet<String>();
                        }else{
                            try {
                                curSet = setCache.take();
                            } catch (InterruptedException e) {
                                curSet = new HashSet<String>();
                            }
                        }

                        curSet.add(nodeValue);

                        try {
                            setCache.put(curSet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private boolean isExistsExtension(String fileName, String[] requestExtensions) {
        if (isNull(fileName)) {
            return false;
        }

        String toLowerCase = fileName.toLowerCase();
        return hasArray(requestExtensions, toLowerCase::endsWith);
    }

    private Node findNodeLoc(Node node, String[] names) {
        final NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);

            if (isNull(item)) continue;

            final String nodeName = item.getNodeName();

            if (hasArray(names, name -> name.equalsIgnoreCase(nodeName))) {
                return item;
            }
        }
        // Дабы не выделять лишнюю память
        return null;
    }

    private static boolean hasArray(String[] array, Predicate<String> filter){
        for (String s : array) {
            if(filter.test(s))
                return true;
        }

        return false;
    }
}
