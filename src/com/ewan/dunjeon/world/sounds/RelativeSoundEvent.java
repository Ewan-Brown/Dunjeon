package com.ewan.dunjeon.world.sounds;

//Wraps around an absoluteSoundEvent, adding data for a receiving entity to use.
public record RelativeSoundEvent(double db, double direction, AbsoluteSoundEvent abs) {

}
