package com.ewan.dunjeon.world.entities.memory;


import com.ewan.dunjeon.world.entities.Entity;

/**
 * Contains data used for basics of an entity
 */
public class EntityStateData {

    float x;
    float y;
    float xSpeed;
    float ySpeed;
    float rotation;
    float rotationalSpeed;
    long UUID;
    Entity.ZPositionState zState;
    String name;

    public EntityStateData(float x, float y, float xSpeed, float ySpeed, float rotation, float rotationalSpeed, long UUID, Entity.ZPositionState zState, String name) {
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getXSpeed() {
        return xSpeed;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    public float getRotation() {
        return rotation;
    }

    public float getRotationalSpeed() {
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
