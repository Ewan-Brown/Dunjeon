package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.ItemHolder;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.furniture.Furniture;
import com.ewan.dunjeon.world.item.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BasicCell implements ItemHolder, Updateable {
    int x;
    int y;
    Level level;
    Entity entity;
    Furniture furniture;
    List<Item> items = new ArrayList<>();
    Color color;
    boolean filled;

    public BasicCell(int x, int y, Level level, boolean filled, Color c) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.filled = filled;
        color = c;
    }

    public Furniture getFurniture(){
        return furniture;
    }

    public void setFurniture(Furniture f){
        furniture = f;
        f.containingCell = this;
    }

    /*
     Don't forget about me :)
     */
    public boolean canBeSeenThrough(Entity e){
        return canBeEntered(e);
    }

    public boolean canBeEntered(Entity e){
        return !filled && (furniture == null || !furniture.isBlocking()) && entity == null;
    }

    public void onEntry(Entity e) {
        entity = e;
    }

    public void onExit(Entity e){
        entity = null;
    }

    public void onEntityDeath(Entity e) {entity = null; }

    public Color getColor(){
        return color;
    }

    public int getX(){return x;}
    public int getY(){return y;}

    @Override
    public List<Item> getItems() {
        return null;
    }

    public Entity getEntity(){ return entity;}

    public Level getLevel(){
        return level;
    }

    @Override
    public void update() {

    }

    public String toString(){
        return String.format("(%d, %d)", x, y);
    }
}
