package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface ValidCombinedQuery {
    @Delete("/delete?q=1")
    void delete(@Query("q") String query);
}