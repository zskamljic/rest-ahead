package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface ValidQuery {
    @Delete("/delete")
    void delete(@Query("q") String query);
}