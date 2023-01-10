package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;
import java.util.ArrayList;

public abstract class Entity implements Updateable {

    private static long UUIDcounter = 0;

    private String name;
    private float size;

    private float posX;
    private float posY;
    private float velX;
    private float angle;
    protected float friction = 10;
    private float velY;
    private long UUID;

    public ZPositionState zState;

    public enum ZPositionState{
        CEILING,
        FLOATING,
        GROUND
    }

    public Shape getRenderShape(){return null;}

    private Floor floor;

    public float getSize() { return size; }

    public String getName(){return name;}

    protected Color color;

    public Entity(Color c, String name){
        this.color = c;
        UUID = UUIDcounter;
        this.name = name;
        this.size = 0.5f;
        UUIDcounter++;
    }

    public Entity(Color c, String name, float size){
        this.color = c;
        UUID = UUIDcounter;
        this.name = name;
        this.size = size;
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

    public Color getColor(){
        return color;
    }

    @Override
    public void update() {
        if(!exists()){
            System.out.println(exists() + " " + getName());
            throw new RuntimeException("Attempted to update dead entity");
        }else {

            posX += velX;
            posY += velY;

            if(friction != 0) {
                velX -= velX / friction;
                velY -= velY / friction;
            }

//            //I don't like this but it's necessary.
//            if(Math.abs(velX) < 0.00001){
//                velX = 0;
//            }
//            if(Math.abs(velY) < 0.00001){
//                velY = 0;
//            }

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

    public float getFriction() {
        return friction;
    }

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

    public java.util.List<Entity> getAttachedEntities(){
        return new ArrayList<>();
    }
}
