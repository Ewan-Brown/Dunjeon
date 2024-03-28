package com.ewan.meworking.data.server.event;

import lombok.AllArgsConstructor;

//Represents instantaneous things, that should only be 'maintained/processed' for a single tick, and don't fit into the various 'Identifier' categories
@AllArgsConstructor
public abstract class Event {
    final float timestamp;
    final int tick;
}
