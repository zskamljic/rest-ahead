package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;

public interface ValidService {
    @Delete("/delete")
    void delete();
}