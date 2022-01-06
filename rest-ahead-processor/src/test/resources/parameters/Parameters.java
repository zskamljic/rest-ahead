package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Post;

public interface Parameters {
    @Delete("/delete")
    void delete(String parameter);

    @Post
    void post(@Header("head") @Query("hello") String multiAnnotated);
}