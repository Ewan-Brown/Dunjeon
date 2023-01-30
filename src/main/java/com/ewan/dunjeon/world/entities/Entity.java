package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.graphics.RenderableObject;
import com.ewan.dunjeon.world.CollidingObject;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.entities.memory.EntityStateData;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Entity {

    private static long UUIDcounter = 0;

    private String name;

    private float posX;
    private float posY;
    private float velX;
    private float rotation;
    private float rotationalSpeed;
    protected float friction = 10;
    private float velY;
    private long UUID;

    public ZPositionState zState;

    public enum ZPositionState{
        CEILING,
        FLOATING,
        GROUND
    }

    private Floor floor;

    public String getName(){return name;}

    public Entity(String name){
        UUID = UUIDcounter;
        this.name = name;
        UUIDcounter++;
    }

    public void setPosition(float x, float y){
        posX = x;
        posY = y;
    }

    public void setVelocity(float x, float y){
        velX = x;
        velY = y;
    }

    public void onEnterCell(BasicCell c){}

    public void update() {
        if(!exists()){
            System.out.println(exists() + " " + getName());
            throw new RuntimeException("Attempted to update dead entity");
        }else {

            posX += velX;
            posY += velY;
            rotation += rotationalSpeed;

            if(friction != 0) {
                velX -= velX / friction;
                velY -= velY / friction;
                rotationalSpeed -= rotation / friction;
            }

        }
    }

    public boolean exists(){
        return true;
    }

    public void onCollideWithWall(BasicCell cell){};
    public void onCollideWithEntity(Entity e){ };
    public void onSoundEvent(RelativeSoundEvent event){

    }

    public void addVelocity(float x, float y){
        velX += x;
        velY += y;
    }

    public BasicCell getContainingCell() {
        return getFloor().getCellAt(posX,posY);
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getVelX() {
        return velX;
    }

    public float getRotation() { return rotation;}

    public float getRotationSpeed() { return rotationalSpeed;}

    public float getVelY() {
        return velY;
    }

    public long getUUID(){ return UUID;}

    public boolean doesCollideWithWall(Entity e){
        return true;
    }

    public float getSpeed(){
        return (float)Math.sqrt(velX*velX+velY*velY);
    }

    public ZPositionState getZState() {
        return zState;
    }

    public void setZState(ZPositionState zState) {
        this.zState = zState;
    }

    public List<Pair<AffineTransform, Entity>> getAttachedEntities() {
        return new ArrayList<>();
    }

    public List<CollidingObject> getCollidables() {
        Polygon p = new Polygon();
        p.addPoint(-1,-1);
        p.addPoint(-1,1);
        p.addPoint(1,1);
        p.addPoint(1,-1);
        AffineTransform shrinker = new AffineTransform();
        shrinker.scale(0.3, 0.3);
        Shape s = shrinker.createTransformedShape(p);
        System.err.println("DUPLICATED CODE HERE, UNNECESSARY CALCULATIONS");
        return Collections.singletonList(new CollidingObject() {
            public Shape getShape() {
                return s;
            }

            public CollideableRule getRule() {
                return CollideableRule.ALL;
            }
        });
    }

    public List<RenderableObject> getDrawables() {
        Polygon p = new Polygon();
        p.addPoint(-1,-1);
        p.addPoint(-1,1);
        p.addPoint(1,1);
        p.addPoint(1,-1);
        AffineTransform shrinker = new AffineTransform();
        shrinker.scale(0.3, 0.3);
        Shape s = shrinker.createTransformedShape(p);
        return Collections.singletonList(new RenderableObject() {
            @Override
            public Shape getShape() {
                return s;
            }

            @Override
            public Color getColor() {
                return Color.BLUE;
            }
        });
    }

    public EntityStateData getEntityStateData(){
        return new EntityStateData(getPosX(), getPosY(), getVelX(), getVelY(), getRotation(), getRotationSpeed(), UUID, zState, name);
    }
}
