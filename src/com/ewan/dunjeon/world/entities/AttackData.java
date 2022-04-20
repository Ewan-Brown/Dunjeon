package com.ewan.dunjeon.world.entities;

public class AttackData {

    private int x;
    private int y;
    private int timeToHit;
    private int damage;

    public AttackData(int timeToHit, int xDir, int yDir, int d) {
        this.timeToHit = timeToHit;
        x = xDir;
        y = yDir;
        damage = d;
    }

    public int getDamage(){
        return damage;
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
