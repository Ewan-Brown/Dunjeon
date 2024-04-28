package com.ewan.meworking.data.server.event;

import com.ewan.meworking.data.server.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

@Getter
public class HeardSoundEvent extends ObservedEvent {
    final private SoundSourceCategory category;
    final private Vector2 approxLocation;
    final private float relativeIntensity;
    final private boolean selfCaused;

    public HeardSoundEvent(SoundSourceCategory category, Vector2 approxLocation, float relativeIntensity, boolean selfCaused, Timestamp timestamp) {
        super(timestamp);
        this.category = category;
        this.approxLocation = approxLocation;
        this.relativeIntensity = relativeIntensity;
        this.selfCaused = selfCaused;
    }

    public enum SoundSourceCategory{
        AMBIENT, ENVIRONMENTAL, ENTITY
    }

}
