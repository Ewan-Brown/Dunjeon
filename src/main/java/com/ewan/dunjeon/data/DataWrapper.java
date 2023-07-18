package com.ewan.dunjeon.data;

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
    I identifier;
    Sensor<? extends DataStreamParameters> sourceSensor;
    double timestamp;

    public DataWrapper(double timestamp, List<D> data, I identifier, Sensor<? extends DataStreamParameters> s) {
        this.data = data;
        this.identifier = identifier;
        sourceSensor = s;
        this.timestamp = timestamp;
    }
}
