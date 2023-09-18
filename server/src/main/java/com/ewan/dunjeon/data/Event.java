package com.ewan.dunjeon.data;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Event<D extends DataWrapper<? extends Data, ?>> {

    D dataWrapper;

}
