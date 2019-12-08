package ru.devpav.sitemap.thread_tasks;

import ru.devpav.util.ChunkCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class ChunkThreadJoin {

    public static <T, R> Collection<R> execute(Integer sizeChunk,
                                         Collection<T> statements,
                                         Function<T, R> statementFunction) {
        return execute(sizeChunk, statements, statementFunction, (collection) -> {});
    }

    public static <T> void execute(Integer sizeChunk,
                                         Collection<T> statements,
                                         Consumer<Collection<T>> consumer) {
        if (isNull(statements) || statements.isEmpty() ) {
            return;
        }

        final int countThreads = statements.size() / Math.max(sizeChunk, 1);

        final Collection<List<T>> chunks = ChunkCollection.chunk(statements, sizeChunk);

        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(countThreads, 1));

        final List<Runnable> chunkRunnables = chunks.stream()
                .map(chunk -> new CallableConsumer<>(chunk, consumer))
                .collect(Collectors.toList());

        try {
            final List<? extends Future<?>> futures = chunkRunnables.stream().map(executorService::submit)
                    .collect(Collectors.toList());
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public static <T, R> Collection<R> execute(Integer sizeChunk,
                                               Collection<T> statements,
                                               Function<T, R> statementFunction,
                                               Consumer<Collection<R>> consumer) {
        if (isNull(statements) || statements.isEmpty() ) {
            return Collections.emptyList();
        }

        final int countThreads = statements.size() / Math.max(sizeChunk, 1);

        final Collection<List<T>> chunks = ChunkCollection.chunk(statements, sizeChunk);

        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(countThreads, 1));

        final List<CallableStatement<T, R>> chunkCallables = chunks.stream()
                .map(chunk -> new CallableStatement<>(chunk, statementFunction))
                .collect(Collectors.toList());

        List<R> joinedResult = new ArrayList<>();

        try {
            final List<Future<List<R>>> futures = executorService.invokeAll(chunkCallables);

            final Function<Future<List<R>>, List<R>> futureListResultFunction = future -> {
                List<R> tmp = new ArrayList<>();
                try {
                    tmp = future.get();
                    consumer.accept(tmp);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return tmp;
            };

            final BinaryOperator<List<R>> mergeElements = (origin, another) -> {
                origin.addAll(another);
                return origin;
            };

            joinedResult = futures.stream()
                    .map(futureListResultFunction)
                    .reduce(mergeElements)
                    .orElse(Collections.emptyList());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return joinedResult;
    }
}
