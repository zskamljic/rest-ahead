package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

public interface QueryInPath {
    @Delete("/delete?q=1")
    void delete();
}