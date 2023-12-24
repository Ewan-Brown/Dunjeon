package com.ewan.meworking.data;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.*;

import java.net.InetSocketAddress;
import java.util.List;

@AllArgsConstructor
@Getter
public final class ServerData {
//    private BasicMemoryBank basicMemoryBank;
    private List<DataWrapper<? extends Data, ?>> dataWrappers;
    private double worldTime;

}