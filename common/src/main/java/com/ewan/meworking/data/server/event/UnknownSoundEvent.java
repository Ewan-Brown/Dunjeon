package com.ewan.meworking.data.server.event;

import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.Optional;

public final class UnknownSoundEvent {

    public UnknownSoundEvent(SoundType t, double i, double dir){
        this(t, i, dir, null);
    }

    public UnknownSoundEvent(SoundType t, double i, double dir, Vector2 v){
        this.soundType = t;
        this.approximateRelativeDirection = dir;
        this.approximateLocation = v;
        this.intensity = i;
    }

    @Getter
    private final SoundType soundType;
    @Getter
    private final double approximateRelativeDirection;
    @Getter
    private final double intensity;

    private final Vector2 approximateLocation;

    public Optional<Vector2> getApproximateLocation(){
        if(approximateLocation == null){
            return Optional.empty();
        }else{
            return Optional.of(approximateLocation);
        }
    }


    public enum SoundType{
        AMBIENT, ENVIRONMENTAL, ENTITY_BASED
    }
    
}
