package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public class NormalClassMethod {
    @Delete("/delete")
    void delete() {
    }
}