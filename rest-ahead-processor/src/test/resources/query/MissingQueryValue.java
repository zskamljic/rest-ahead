package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public interface MissingQueryValue {
    @Delete("/delete?q=")
    void delete();
}