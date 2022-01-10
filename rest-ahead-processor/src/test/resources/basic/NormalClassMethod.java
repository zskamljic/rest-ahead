package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;
import com.lablizards.restahead.annotations.verbs.Delete;

public class NormalClassMethod {
    @Delete("/delete")
    void delete() {
    }
}