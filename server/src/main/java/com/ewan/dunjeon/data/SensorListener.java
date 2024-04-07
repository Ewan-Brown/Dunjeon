package com.ewan.dunjeon.data;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.event.Event;

public interface SensorListener {
    void passOnData(DataWrapper<? extends Data, ?> data);
    void passOnEvent(Event e);
}
