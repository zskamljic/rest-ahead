package com.lablizards.restahead.processor;

import org.junit.jupiter.api.Test;

class QueryProcessorTest extends CommonProcessorTest {
    @Test
    void queryWithMissingNameFailsToCompile() {
        commonCompilationAssertion("query/MissingName.java")
            .failsToCompile()
            .withErrorContaining("Generating query parameter");
    }

    @Test
    void pathWithMissingQueryFailsToCompile() {
        commonCompilationAssertion("query/MissingQueryValue.java")
            .failsToCompile()
            .withErrorContaining("Malformed query");
    }

    @Test
    void queryInPathCompiles() {
        commonCompilationAssertion("query/QueryInPath.java")
            .compilesWithoutWarnings();
    }

    @Test
    void combinedQueryCompiles() {
        commonCompilationAssertion("query/ValidCombinedQuery.java")
            .compilesWithoutWarnings();
    }

    @Test
    void validQueryCompiles() {
        commonCompilationAssertion("query/ValidQuery.java")
            .compilesWithoutWarnings();
    }

    @Test
    void iterablesAndPrimitivesCompile() {
        commonCompilationAssertion("query/CollectionAndArray.java")
            .compilesWithoutWarnings();
    }
}
