package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public abstract class MethodClass {
    @Delete("/delete")
    abstract void delete();
}