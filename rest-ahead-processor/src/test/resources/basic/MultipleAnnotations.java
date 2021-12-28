package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Patch;

public interface MultipleAnnotations {
    @Delete("/delete")
    @Patch("/delete")
    void delete();
}