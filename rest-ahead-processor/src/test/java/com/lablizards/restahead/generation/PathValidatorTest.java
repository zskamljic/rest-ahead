package com.lablizards.restahead.generation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PathValidatorTest {
    @Mock
    private Messager messager;

    @Mock
    private ExecutableElement function;

    @InjectMocks
    private PathValidator pathValidator;

    @ParameterizedTest
    @CsvSource({
        "false,",
        "false,''",
        "false,/abcdef1234-._~",
        "false,/%20%31",
        "false,/a+b()",
        "false,/%20a+b()abc",
        "false,/a+b()/%20%31/abc~",
        "true,/p ath;"
    })
    void pathReturnsCorrectForString(boolean valid, String path) {
        var result = pathValidator.containsErrors(function, path);

        assertEquals(valid, result);
    }
}