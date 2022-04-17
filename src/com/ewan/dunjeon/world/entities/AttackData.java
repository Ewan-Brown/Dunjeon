package com.ewan.dunjeon.world.entities;

public class AttackData {

    private int x;
    private int y;
    private int timeToHit;

    public AttackData(int timeToHit, int xDir, int yDir) {
        this.timeToHit = timeToHit;
        x = xDir;
        y = yDir;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTimeToHit(){
        return timeToHit;
    }
}
