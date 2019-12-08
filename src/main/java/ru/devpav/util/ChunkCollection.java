package ru.devpav.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ChunkCollection {

    public static <T> Collection<List<T>> chunk(Collection<T> collection, Integer chunkSize) {
        final AtomicInteger counter = new AtomicInteger(chunkSize);
        return collection.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / Math.max(chunkSize, 1)))
                .values();
    }

}
