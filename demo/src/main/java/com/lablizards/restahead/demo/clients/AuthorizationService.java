package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.demo.models.AuthResponse;

import java.util.concurrent.Future;

public interface AuthorizationService {
    @Get("/basic-auth/user/password")
    Future<Response> getBasicAuth(@Header("Authorization") String authorization);

    @Get("/bearer")
    Future<AuthResponse> getBearer(@Header("Authorization") String authorization);
}
