package io.github.zskamljic;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class LocalHttpBinServer {
    private HttpServer server;

    public int start() throws IOException {
        int port = 0;
        while (port == 0) {
            try (var socket = new ServerSocket(ThreadLocalRandom.current().nextInt(1025, 65536))) {
                port = socket.getLocalPort();
            } catch (IOException ignored) {
                // Port taken
            }
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/get").setHandler(this::methodHandler);
        server.createContext("/post").setHandler(this::methodHandler);
        server.start();
        return port;
    }

    private void methodHandler(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("content-type", "application/json");

        String requestBody;
        try (var request = exchange.getRequestBody()) {
            requestBody = new String(request.readAllBytes(), StandardCharsets.UTF_8);
        }

        exchange.sendResponseHeaders(200, 0);

        try (var response = exchange.getResponseBody()) {
            response.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
