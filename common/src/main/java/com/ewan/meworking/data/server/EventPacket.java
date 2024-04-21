package com.ewan.meworking.data.server;

import com.ewan.meworking.data.server.event.ObservedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventPacket {
    private final ObservedEvent observedEvent;
}
