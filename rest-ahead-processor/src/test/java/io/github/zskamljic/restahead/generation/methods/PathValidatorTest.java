package io.github.zskamljic.restahead.generation.methods;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.requests.request.BasicRequestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PathValidatorTest {
    @Mock
    private Messager messager;

    @Mock
    private ExecutableElement function;

    @Mock
    private Elements elements;

    @Mock
    private Types types;

    private PathValidator pathValidator;

    @BeforeEach
    void setUp() {
        var typeMock = mock(TypeElement.class);
        doReturn(mock(TypeMirror.class)).when(typeMock).asType();
        doReturn(typeMock).when(elements).getTypeElement(any());
        pathValidator = new PathValidator(messager, elements, types);
    }

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
    void pathReturnsCorrectForString(boolean invalid, String path) {
        var result = pathValidator.validatePathAndExtractQuery(
            function,
            new BasicRequestLine(Verb.GET, path),
            new ParameterDeclaration(List.of(), List.of(), List.of(), Optional.empty())
        );

        assertEquals(invalid, result.isEmpty());
    }
}