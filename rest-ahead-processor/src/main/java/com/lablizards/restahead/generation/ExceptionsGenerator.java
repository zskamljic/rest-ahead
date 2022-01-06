package com.lablizards.restahead.generation;

import com.lablizards.restahead.exceptions.RestException;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generates exception code for code that may or may not require exceptions.
 */
public class ExceptionsGenerator {
    /**
     * Surrounds the statement returned by {@param statementGenerator} by try-catch block if needed.
     *
     * @param builder            the builder to add the exception to
     * @param declaredExceptions the exceptions that the function declares - these will not be caught
     * @param actualExceptions   the exceptions that can be thrown by the surrounded statement
     * @param statementGenerator the runnable that generates
     */
    public void generateTryCatchIfNeeded(
        MethodSpec.Builder builder,
        List<TypeMirror> declaredExceptions,
        List<TypeMirror> actualExceptions,
        Runnable statementGenerator
    ) {
        var missingExceptions = actualExceptions.stream()
            .filter(Predicate.not(declaredExceptions::contains))
            .toList();
        if (!missingExceptions.isEmpty()) {
            builder.beginControlFlow("try");
        }
        statementGenerator.run();
        if (!missingExceptions.isEmpty()) {
            var exceptionList = String.join(" | ", missingExceptions.stream().map(e -> "$T").toList());
            builder.nextControlFlow("catch (" + exceptionList + " exception)", missingExceptions.toArray(Object[]::new))
                .addStatement("throw $T.getAppropriateException(exception)", RestException.class)
                .endControlFlow();
        }
    }
}
