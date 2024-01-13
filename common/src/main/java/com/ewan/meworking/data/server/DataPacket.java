package com.ewan.meworking.data.server;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import lombok.*;

@AllArgsConstructor
@Getter
/**
 * Wraps around a DataWrapper for networking purposes. Redundant at the moment but will be useful if I need to attach anything to it later.
 */
public final class DataPacket {
    private DataWrapper<? extends Data, ?> dataWrapper;

}