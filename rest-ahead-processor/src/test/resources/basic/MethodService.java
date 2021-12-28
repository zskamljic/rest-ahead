package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public interface MethodService {
    @Delete("/delete")
    default void delete() {
    }
}