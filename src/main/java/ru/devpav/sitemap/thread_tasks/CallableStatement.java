package ru.devpav.sitemap.thread_tasks;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CallableStatement<T, R> implements Callable<List<R>> {

    private final List<T> statements;
    private final Function<T, R> statementFunction;

    public CallableStatement(List<T> statements,
                             Function<T, R> statementFunction) {
        this.statements = statements;
        this.statementFunction = statementFunction;
    }

    @Override
    public List<R> call() {
        return statements.stream()
                .map(statementFunction)
                    .collect(Collectors.toList());
    }

}
