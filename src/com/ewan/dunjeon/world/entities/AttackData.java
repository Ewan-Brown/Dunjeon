package com.ewan.dunjeon.world.entities;

public class AttackData {

    public AttackData(int timeToHit) {
        this.timeToHit = timeToHit;
    }

    // This represents the 'swing' time, the time between starting the attack and the actual hit.
    private int timeToHit;

    public int getTimeToHit(){
        return timeToHit;
    }
}
