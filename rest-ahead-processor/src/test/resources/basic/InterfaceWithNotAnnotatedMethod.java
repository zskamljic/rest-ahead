package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

public interface InterfaceWithNotAnnotatedMethod {
    @Delete("/delete")
    void delete();

    void missingAnnotation();
}