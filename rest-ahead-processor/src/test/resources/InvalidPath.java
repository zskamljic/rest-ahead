package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public interface InvalidPath {
    @Delete("/delete with invalid path")
    void delete();
}