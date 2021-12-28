package com.lablizards.restahead.processor;

import org.junit.jupiter.api.Test;

class HeaderProcessorTest extends CommonProcessorTest {
    @Test
    void validHeaderCompiles() {
        commonCompilationAssertion("headers/StringHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void invalidHeaderFailsToCompile() {
        commonCompilationAssertion("headers/InvalidHeader.java")
            .failsToCompile()
            .withErrorContaining("Only primitives, String, UUID and their arrays and collections are supported.");
    }

    @Test
    void primitiveHeaderCompiles() {
        commonCompilationAssertion("headers/PrimitiveHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void boxedHeaderCompiles() {
        commonCompilationAssertion("headers/BoxedHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void uuidHeaderCompiles() {
        commonCompilationAssertion("headers/UuidHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void arrayHeaderCompiles() {
        commonCompilationAssertion("headers/ValidArrayHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void listHeaderCompiles() {
        commonCompilationAssertion("headers/ValidCollectionHeader.java")
            .compilesWithoutWarnings();
    }

    @Test
    void invalidArrayHeaderFailsToCompile() {
        commonCompilationAssertion("headers/InvalidArrayHeader.java")
            .failsToCompile()
            .withErrorContaining("Only primitives, String, UUID and their arrays and collections are supported.");
    }

    @Test
    void invalidListHeaderFailsToCompile() {
        commonCompilationAssertion("headers/InvalidCollectionHeader.java")
            .failsToCompile()
            .withErrorContaining("Only primitives, String, UUID and their arrays and collections are supported.");
    }

    @Test
    void emptyHeaderFailsToCompile() {
        commonCompilationAssertion("headers/EmptyHeader.java")
            .failsToCompile()
            .withErrorContaining("Generating header names from parameter");
    }
}
