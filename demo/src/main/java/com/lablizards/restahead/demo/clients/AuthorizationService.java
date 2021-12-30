package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.demo.models.AuthResponse;

public interface AuthorizationService {
    @Get("/basic-auth/user/password")
    Response getBasicAuth(@Header("Authorization") String authorization);

    @Get("/bearer")
    AuthResponse getBearer(@Header("Authorization") String authorization);
}
