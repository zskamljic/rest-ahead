package com.lablizards.restahead.generation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;


@ExtendWith(MockitoExtension.class)
class PathValidatorTest {
    @Mock
    private Messager messager;

    @Mock
    private ExecutableElement function;

    @InjectMocks
    private PathValidator pathValidator;

    private final Answer<?> printingAnswer = invocationOnMock -> {
        var arguments = invocationOnMock.getArguments();
        System.out.print(arguments[0].toString());
        System.out.print(": ");
        System.out.print(arguments[1].toString());
        if (arguments.length == 3) {
            System.out.print(" on element ");
            System.out.print(arguments[2].toString());
        }
        System.out.println();
        return null;
    };

    @Test
    void pathHasErrorsReturnsFalseForEmptyString() {
        var result = pathValidator.containsErrors(function, "");

        assertFalse(result);
    }

    @Test
    void pathHasErrorsReturnsFalseForUnreservedString() {
        var result = pathValidator.containsErrors(function, "/abcdef1234-._~");

        assertFalse(result);
    }

    @Test
    void pathHasErrorsReturnsFalseForPctEncoded() {
        var result = pathValidator.containsErrors(function, "/%20%31");

        assertFalse(result);
    }

    @Test
    void pathHasErrorsReturnsFalseForSubDelimiters() {
        var result = pathValidator.containsErrors(function, "/a+b()");

        assertFalse(result);
    }

    @Test
    void pathHasErrorsReturnsFalseForCombined() {
        var result = pathValidator.containsErrors(function, "/%20a+b()abc");

        assertFalse(result);
    }

    @Test
    void pathHasErrorsReturnsFalseMultipleSegments() {
        var result = pathValidator.containsErrors(function, "/a+b()/%20%31/abc~");

        assertFalse(result);
    }
}