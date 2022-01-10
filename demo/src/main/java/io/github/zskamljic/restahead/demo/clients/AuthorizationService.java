package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.client.Response;
import io.github.zskamljic.restahead.demo.models.AuthResponse;

public interface AuthorizationService {
    @Get("/basic-auth/user/password")
    Response getBasicAuth(@Header("Authorization") String authorization);

    @Get("/bearer")
    AuthResponse getBearer(@Header("Authorization") String authorization);
}
