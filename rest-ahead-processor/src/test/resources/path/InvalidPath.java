package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface InvalidPath {
    @Delete("/delete with invalid path")
    void delete();
}