package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.Optional;

public abstract class GenericSoundEvent extends Event{
    Sounds.SoundSourceCategory category;

    public GenericSoundEvent(Timestamp timestamp) {
        super(timestamp);
    }
}
