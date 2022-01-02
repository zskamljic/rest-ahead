package com.lablizards.restahead.generation.methods;

import com.lablizards.restahead.modeling.validation.HeaderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HeaderValidatorTest {
    @Mock
    private Messager messager;

    @Mock
    private Elements elements;

    @Mock
    private Types types;

    private HeaderValidator headerValidator;

    @BeforeEach
    void setUp() {
        doReturn(mock(TypeElement.class)).when(elements).getTypeElement(any());
        headerValidator = new HeaderValidator(messager, elements, types);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "A B"})
    void validatorReturnsEmptyForInvalidName(String header) {
        var result = headerValidator.getHeaderSpec(header, mock(VariableElement.class));

        assertTrue(result.isEmpty());
    }
}