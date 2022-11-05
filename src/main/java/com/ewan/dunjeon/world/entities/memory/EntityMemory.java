package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Memory;

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

    private final long UUID; //Allows individual tracking of memory to entity - if an entity reappears multiple times, this will allow an old memory to be replaced by new.
    private final float x;
    private final float y;
    private final float xSpeed;
    private final float ySpeed;
    private final float size;
    private final boolean interactable; //For use with player

    EntityRenderData renderData;

    public static class EntityRenderData{

        public EntityRenderData(Entity e){
            this.size = e.getSize();
            this.color = e.getColor();
        }

        private final float size;
        private final Color color;

        public float getSize(){ return size;}
        public Color getColor(){ return color;}
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
