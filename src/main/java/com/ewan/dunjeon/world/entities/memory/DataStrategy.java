package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.data.Data;

public interface DataStrategy<T extends Data>{
    public void processData(T data);
}
