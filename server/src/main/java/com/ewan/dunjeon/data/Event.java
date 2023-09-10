package com.ewan.dunjeon.data;

import com.ewan.dunjeoncommon.data.Data;
import com.ewan.dunjeoncommon.data.DataWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Event<D extends DataWrapper<? extends Data, ?>> {

    D dataWrapper;

}
