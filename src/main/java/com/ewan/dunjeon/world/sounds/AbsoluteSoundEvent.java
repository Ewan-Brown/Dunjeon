package com.ewan.dunjeon.world.sounds;

import com.ewan.dunjeon.world.entities.KinematicEntity;
import com.ewan.dunjeon.world.level.Floor;

import java.awt.geom.Point2D;

//sourceEntity is used for identification of foreign sounds (e.x entity shouldn't register its own footsteps!)
/**
 * TODO CLEAN THIS UP! Make a factory for it or something this is terrible.
 */
public record AbsoluteSoundEvent(float intensity, Point2D sourceLocation, Floor sourceFloor, String soundMessageIfVisible, String soundMessageIfNotVisible, SoundType type, KinematicEntity sourceEntity) {

    public enum SoundType{
        PHYSICAL, CHAT, AMBIENT
    }
}
