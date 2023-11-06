package com.ewan.meworking.data.server.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


/**
 * Contains the data and an identifier to provide context for processing
 * @param <D> The actual data itself
 * @param <I> An object to be used in processing - can be considered higher level metadata. things like creature ID and cell position.g
 */
@Getter
@AllArgsConstructor
public abstract class DataWrapper<D extends Data, I> {
    List<D> data;
    Class<D> baseClass;
    I identifier;
    double timestamp;
}
