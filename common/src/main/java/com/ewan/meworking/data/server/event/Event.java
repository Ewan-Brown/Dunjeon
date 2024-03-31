package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.AllArgsConstructor;

//Represents instantaneous things, that should only be 'maintained/processed' for a single tick, and don't fit into the various 'Identifier' categories
@AllArgsConstructor
public abstract class Event {
    final Timestamp timestamp;
}
