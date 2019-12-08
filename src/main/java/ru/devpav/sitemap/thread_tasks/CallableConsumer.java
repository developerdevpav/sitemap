package ru.devpav.sitemap.thread_tasks;

import java.util.Collection;
import java.util.function.Consumer;

public class CallableConsumer<T> implements Runnable {

    private final Collection<T> chunkList;
    private final Consumer<Collection<T>> consumer;

    public CallableConsumer(Collection<T> chunkList, Consumer<Collection<T>> consumer) {
        this.chunkList = chunkList;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        consumer.accept(chunkList);
    }

}
