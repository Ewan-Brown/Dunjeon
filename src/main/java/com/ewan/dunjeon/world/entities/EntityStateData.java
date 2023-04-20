package com.ewan.dunjeon.world.entities;


/**
 * This is a class used to package generic data about an entity at a given moment. <p> It is open to extension.
 */
public class EntityStateData {

    double x;
    double y;
    double xSpeed;
    double ySpeed;
    double rotation;
    double rotationalSpeed;
    long UUID;
    Entity.ZPositionState zState;
    String name;

    public EntityStateData(double x, double y, double xSpeed, double ySpeed, double rotation, double rotationalSpeed, long UUID, Entity.ZPositionState zState, String name) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.rotation = rotation;
        this.rotationalSpeed = rotationalSpeed;
        this.UUID = UUID;
        this.zState = zState;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public double getRotation() {
        return rotation;
    }

    public double getRotationalSpeed() {
        return rotationalSpeed;
    }

    public long getUUID() {
        return UUID;
    }

    public Entity.ZPositionState getZState() {
        return zState;
    }

    public String getName() {
        return name;
    }
}
