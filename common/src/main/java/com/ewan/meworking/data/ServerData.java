package com.ewan.meworking.data;

import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.*;

@AllArgsConstructor
@Getter
public final class ServerData {
    private BasicMemoryBank basicMemoryBank;
    private double worldTime;


}