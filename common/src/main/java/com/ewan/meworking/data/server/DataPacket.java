package com.ewan.meworking.data.server;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import lombok.*;

@AllArgsConstructor
@Getter
public final class DataPacket {
    private DataWrapper<? extends Data, ?> dataWrapper;
    private double worldTime;

}