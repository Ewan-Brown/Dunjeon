package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.level.Floor;

import java.awt.geom.Point2D;

public record AbsoluteSoundEvent(float decibels, Point2D sourceLocation, Floor sourceFloor, String soundMessageIfVisible, String soundMessageIfNotVisible, SoundType type) {

//    public AbsoluteSoundEvent(float decibels, SoundSource source, Point2D sourceLocation, Floor sourceFloor, String soundMessageIfVisible, String soundMessageIfNotVisible, SoundType type) {
//        this.decibels = decibels;
//        this.source = source;
//        this.sourceLocation = sourceLocation;
//        this.sourceFloor = sourceFloor;
//        this.soundMessageIfVisible = soundMessageIfVisible;
//        this.soundMessageIfNotVisible = soundMessageIfNotVisible;
//        this.type = type;
//    }
//
//    float decibels;
//    SoundSource source;
//    Point2D sourceLocation;
//    Floor sourceFloor;
//
//    //TODO This works for now, could be made more extensible if more cases are defined?
//    String soundMessageIfVisible;
//    String soundMessageIfNotVisible;
//
//    //Used for player visualization
//
//    SoundType type;
    public enum SoundType{
        PHYSICAL, CHAT, AMBIENT
    }
}
