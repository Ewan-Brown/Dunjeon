package com.ewan.dunjeon.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Event<D extends DataWrapper<? extends Data, ?>> {

    D dataWrapper;

}
