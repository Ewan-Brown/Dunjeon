package com.ewan.meworking.data;

import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerData {
    private BasicMemoryBank basicMemoryBank;

    public ServerData(BasicMemoryBank basicMemoryBank) {
        this.basicMemoryBank = basicMemoryBank;
    }

}