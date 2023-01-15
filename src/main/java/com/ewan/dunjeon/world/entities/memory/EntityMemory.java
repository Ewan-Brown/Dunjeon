package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.entities.KinematicEntity;

import java.awt.*;

public class EntityMemory extends Memory {

    public EntityMemory(long UUID, float x, float y, float xSpeed, float ySpeed, float size, boolean interactable, EntityRenderData renderData) {
        this.UUID = UUID;
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.size = size;
        this.interactable = interactable;
        this.renderData = renderData;
    }

    private long UUID; //Allows individual tracking of memory to entity - if an entity reappears multiple times, this will allow an old memory to be replaced by new.
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;
    private float size;
    private boolean interactable; //For use with player

    public void update(EntityMemory newMemory){
        this.UUID = newMemory.UUID;
        this.x = newMemory.x;
        this.y = newMemory.y;
        this.xSpeed = newMemory.xSpeed;
        this.ySpeed = newMemory.ySpeed;
        this.size = newMemory.size;
        this.interactable = newMemory.interactable;
        this.renderData = newMemory.renderData;
        this.isOldData = newMemory.isOldData;
    }

    EntityRenderData renderData;

    public static class EntityRenderData{

        public EntityRenderData(KinematicEntity e){
            this.size = e.getSize();
            this.color = e.getColor();
            this.Shape = e.getRenderShape();
        }

        private final float size;
        private final Color color;
        private final Shape Shape;

        public float getSize(){ return size;}
        public Color getColor(){ return color;}
        public Shape getShape(){ return Shape;}
    }

    public long getUUID() {
        return UUID;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public float getSize(){
        return size;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public EntityRenderData getRenderData() {
        return renderData;
    }
}
