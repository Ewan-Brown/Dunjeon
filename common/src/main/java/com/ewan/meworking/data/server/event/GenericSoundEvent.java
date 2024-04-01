package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import com.ewan.meworking.data.server.data.DataWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.List;
import java.util.Optional;

public abstract class GenericSoundEvent extends Event{
    final private SoundSourceCategory category;
    final private float intensity;
    final private List<SoundHint> soundHints;

    public GenericSoundEvent(Timestamp timestamp, SoundSourceCategory category, float i, List<SoundHint> hints) {
        super(timestamp);
        this.category = category;
        this.intensity = i;
        soundHints = hints;
    }

    public GenericSoundEvent(Timestamp timestamp, SoundSourceCategory category, float i) {
        super(timestamp);
        this.category = category;
        this.intensity = i;
        soundHints = List.of();
    }

    public enum SoundSourceCategory{
        AMBIENT, ENVIRONMENTAL, ENTITY
    }

    //Allows us to attach granular data to a sound, which may or may not be picked up by a listener
    public static class SoundHint{
        List<DataWrapper<?,?>> wrappedDatas;
    }
}
