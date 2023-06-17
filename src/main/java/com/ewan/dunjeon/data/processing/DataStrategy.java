package com.ewan.dunjeon.data.processing;

import com.ewan.dunjeon.data.Data;

public interface DataStrategy<T extends Data>{
    public void processData(T data);
}
