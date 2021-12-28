package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

public interface Parameters {
    @Delete("/delete")
    void delete(String parameter);
}