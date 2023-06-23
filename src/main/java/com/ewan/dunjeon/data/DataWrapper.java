package com.ewan.dunjeon.data;

import lombok.Getter;

import java.util.List;


/**
 * Contains the data and an identifier to provide context for processing
 * @param <D> The actual data itself
 * @param <I> An object to be used in processing - can be considered higher level metadata. things like creature ID and cell position.g
 */
@Getter
public abstract class DataWrapper<D extends Data, I> {
    List<D> data;
    I identifier;

    public DataWrapper(List<D> data, I identifier) {
        this.data = data;
        this.identifier = identifier;
    }
}
