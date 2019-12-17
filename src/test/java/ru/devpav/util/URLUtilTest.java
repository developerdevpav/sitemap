package ru.devpav.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
class URLUtilTest {

    @Test
    void substringRegexp() {
        String[] lines = new String[] {
                "sitemap: https://www.confluent.io/blog/avro-kafka-data/",
                "https://www.confluent.io/blog/avro-kafka-data/ sifeiwfwef",
                "fefrejior https://www.confluent.io/blog/avro-kafka-data/ sifeiwfwef"
        };

        final Set<String> urls = Stream.of(lines)
                .map(URLUtil::substringRegexp)
                .collect(Collectors.toSet());

        final boolean allMatch = urls.stream()
                .allMatch(url -> url.equals("https://www.confluent.io/blog/avro-kafka-data/"));

        assertTrue(allMatch);
    }

}