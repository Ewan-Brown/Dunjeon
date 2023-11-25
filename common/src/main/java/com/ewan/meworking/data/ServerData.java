package com.ewan.meworking.data;

import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.*;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Getter
public final class ServerData {
    private BasicMemoryBank basicMemoryBank;
    private double worldTime;

}