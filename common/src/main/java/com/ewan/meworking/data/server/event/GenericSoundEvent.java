package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.Optional;

public abstract class GenericSoundEvent extends Event{
    final private Sounds.SoundSourceCategory category;
    final private float intensity;

    public GenericSoundEvent(Timestamp timestamp, Sounds.SoundSourceCategory category, float i) {
        super(timestamp);
        this.category = category;
        this.intensity = i;
    }
}
