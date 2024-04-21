package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

@Getter
public class HeardSoundEvent extends ObservedEvent {
    final private SoundSourceCategory category;
    final private Vector2 approxLocation;
    final private float relativeIntensity;
    final private boolean selfCaused;

    public HeardSoundEvent(Timestamp timestamp, Vector2 location, SoundSourceCategory category, float i, boolean self) {
        super(timestamp);
        this.category = category;
        this.approxLocation = location;
        this.relativeIntensity = i;
        selfCaused = self;
    }

    public enum SoundSourceCategory{
        AMBIENT, ENVIRONMENTAL, ENTITY
    }

}
