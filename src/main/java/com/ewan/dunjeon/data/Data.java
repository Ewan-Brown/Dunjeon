package com.ewan.dunjeon.data;

import lombok.Getter;

@Getter
public abstract class Data {
    private final double timestamp;
    protected Data(double timestamp) {
        this.timestamp = timestamp;
    }
}
