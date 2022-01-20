package io.github.zskamljic.restahead.client.requests;

import io.github.zskamljic.restahead.client.requests.parts.FieldPart;
import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MultiPartRequestTest {
    @Captor
    private ArgumentCaptor<InputStream> stream;

    @Test
    void multiplePartsGeneratesValidInput() throws IOException {
        var requestBuilder = mock(Request.Builder.class);

        var builder = spy(MultiPartRequest.builder());
        doReturn("----3ff7b05f-9b3f-4f1e-8389-99e1c5686d61")
            .when(builder)
            .generateBoundary();

        builder.addPart(new FieldPart("field1", "value1"))
            .addPart(new FilePart("file1", "filename1", new ByteArrayInputStream("content1".getBytes())))
            .addPart(new FieldPart("field2", "value2"))
            .addPart(new FilePart("file2", "filename2", new ByteArrayInputStream("content2".getBytes())))
            .buildInto(requestBuilder);

        verify(requestBuilder).setBody(stream.capture());

        var inputStream = stream.getValue();
        var stringData = new String(inputStream.readAllBytes());

        assertEquals("""
            ------3ff7b05f-9b3f-4f1e-8389-99e1c5686d61\r
            Content-Disposition: form-data; name="field1"\r
            \r
            value1\r
            ------3ff7b05f-9b3f-4f1e-8389-99e1c5686d61\r
            Content-Disposition: form-data; name="file1"; filename="filename1"\r
            Content-Type: application/octet-stream\r
            \r
            content1\r
            ------3ff7b05f-9b3f-4f1e-8389-99e1c5686d61\r
            Content-Disposition: form-data; name="field2"\r
            \r
            value2\r
            ------3ff7b05f-9b3f-4f1e-8389-99e1c5686d61\r
            Content-Disposition: form-data; name="file2"; filename="filename2"\r
            Content-Type: application/octet-stream\r
            \r
            content2\r
            ------3ff7b05f-9b3f-4f1e-8389-99e1c5686d61--""", stringData);
    }
}