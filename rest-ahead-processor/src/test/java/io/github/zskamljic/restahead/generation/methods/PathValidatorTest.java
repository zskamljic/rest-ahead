package io.github.zskamljic.restahead.generation.methods;

import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.requests.request.RequestLine;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.Optional;

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
    void pathReturnsCorrectForString(boolean invalid, String path) {
        var result = pathValidator.validatePathAndExtractQuery(
            function,
            new RequestLine(Request.class, path),
            new ParameterDeclaration(List.of(), List.of(), Optional.empty())
        );

        assertEquals(invalid, result.isEmpty());
    }
}