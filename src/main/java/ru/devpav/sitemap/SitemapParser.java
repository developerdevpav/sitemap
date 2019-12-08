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
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

/*https://fedpress.ru/sitemap/2019-11-08.xml*/
public class SitemapParser {

    private static final Set<String> requestExtensions = new HashSet<>();
    private static final Set<String> keyDate = new HashSet<>();
    private static final Set<String> locLink = new HashSet<>();

    private final static Logger logger = LoggerFactory.getLogger(SitemapParser.class);

    static {
        requestExtensions.addAll(Arrays.asList("xml", "php"));
        keyDate.addAll(Arrays.asList("date", "lastmod"));
        locLink.addAll(Collections.singletonList("loc"));
    }

    public Set<String> parse(URL url) {
        Set<String> finalizeLinks = new HashSet<>();
        try {
            HttpResponse<String> response;
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

        DocumentBuilder docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ignored) {
        }

        return parse(text, mapRequire, docBuilder);
    }

    private Set<String> parse(String urlXml, Function<String, String> deepenFunction, DocumentBuilder docBuilder) {

        if (isNull(docBuilder)) {
            throw new RuntimeException("DocumentBuilder wasn't created");
        }

        Document dom;
        try {
            dom = docBuilder.parse(new InputSource(new StringReader(urlXml)));
        } catch (SAXException | IOException e) {
            return Collections.emptySet();
        }

        if (isNull(dom)) {
            throw new RuntimeException("Root element external document not found");
        }

        return parse(dom.getDocumentElement(), deepenFunction, docBuilder);
    }


    private Set<String> parse(Node root, Function<String, String> deepenFunction, DocumentBuilder docBuilder) {
        final NodeList childNodes = root.getChildNodes();

        final Set<String> strings = new HashSet<>();

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            final Optional<Node> nodeLoc = findNodeLoc(item, locLink);

            if (!nodeLoc.isPresent()) {
                continue;
            }

            final Node loc = nodeLoc.get();

            final String nodeValue = loc.getTextContent();

            final boolean isExistsExtension = isExistsExtension(nodeValue, requestExtensions);

            if (isExistsExtension) {
                final String body = deepenFunction.apply(nodeValue);
                final Set<String> returnedLinks = parse(body, deepenFunction, docBuilder);
                strings.addAll(returnedLinks);
            } else {
                strings.add(nodeValue);
            }

            if (item.getChildNodes().getLength() == 0) {
                return strings;
            }
        }

        return strings;
    }

    private boolean isExistsExtension(String fileName, Set<String> requestExtensions) {
        if (isNull(fileName)) {
            return false;
        }

        final String toLowerCase = fileName.toLowerCase();

        Predicate<String> predicate = (extension) ->
                toLowerCase.endsWith(extension.toLowerCase()) || toLowerCase.contains(".xml");

        return requestExtensions.stream()
                .anyMatch(predicate);
    }

    private Optional<Node> findNodeLoc(Node node, Set<String> names) {
        final NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);

            if (isNull(item)) continue;

            final String nodeName = item.getNodeName();

            final boolean anyMatch = names.stream().anyMatch(
                    name -> name.toLowerCase().equals(nodeName.toLowerCase())
            );

            if (anyMatch) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

}
