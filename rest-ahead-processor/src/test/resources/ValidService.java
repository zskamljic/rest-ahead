package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.*;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface ValidService {
    @Delete("/delete")
    void delete();
}