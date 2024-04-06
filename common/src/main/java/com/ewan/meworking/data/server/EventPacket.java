package com.ewan.meworking.data.server;

import com.ewan.meworking.data.server.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventPacket {
    private final Event event;
}
