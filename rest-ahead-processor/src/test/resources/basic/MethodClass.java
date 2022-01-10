package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;
import com.lablizards.restahead.annotations.verbs.Delete;

public abstract class MethodClass {
    @Delete("/delete")
    abstract void delete();
}