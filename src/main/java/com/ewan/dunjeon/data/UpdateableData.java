package com.ewan.dunjeon.data;

public interface UpdateableData<T extends Data> {

    void updateWithData(T data);

}
