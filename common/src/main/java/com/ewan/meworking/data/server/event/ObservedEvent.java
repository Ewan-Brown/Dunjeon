package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Time;

//Represents instantaneous things, that should only be 'maintained/processed' for a single tick, and don't fit into the various 'Identifier' categories
@Getter
public abstract class ObservedEvent {
    final Timestamp timestamp;
    public ObservedEvent(Timestamp timestamp){
        this.timestamp = timestamp;
    }
}
