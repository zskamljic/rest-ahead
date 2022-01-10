package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface MethodService {
    @Delete("/delete")
    default void delete() {
    }
}