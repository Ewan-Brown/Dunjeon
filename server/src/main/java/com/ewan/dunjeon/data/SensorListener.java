package com.ewan.dunjeon.data;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;

import java.util.List;

public interface SensorListener {
    void passOnData(DataWrapper<? extends Data, ?> data);
}
