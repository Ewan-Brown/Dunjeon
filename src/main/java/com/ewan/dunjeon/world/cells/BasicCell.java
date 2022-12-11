package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.awt.geom.Point2D;

public class BasicCell implements Updateable {
    int x;
    int y;
    Floor floor;
    Furniture furniture;
    public Color color;

    public boolean isFilled() {
        return filled;
    }

    private boolean filled; //TODO Replace this with something a little more flexible?

    public BasicCell(int x, int y, Floor f, Color c) {
        this.x = x;
        this.y = y;
        this.floor = f;
        this.color = c;
    }

    public void setFilled(boolean f){
        filled = f;
    }

//    public void setColor(Color c){
//        this.color = c;
//    }

    public Furniture getFurniture(){
        return furniture;
    }

    public void setFurniture(Furniture f){
        if(this.furniture != null){
            throw new RuntimeException("Tried to add furniture to cell that already contains furniture!");
        }
        furniture = f;
        f.containingCell = this;
    }

    public void updateFurniture(){
        if(furniture!= null) {
            furniture.update();
        }
    }

    /*
     Don't forget about me :)
     */
    public boolean canBeSeenThrough(Entity e){
        return canBeEntered(e);
    }

    public boolean canBeEntered(Entity e){
        return !filled && (furniture == null || !furniture.isBlocking());
    }

    public void onEntry(Entity e) {}

    public void onExit(Entity e){}

    public void onDeath(Entity e){}

    public void onEntityDeath(Entity e) {}

    public int getX(){return x;}
    public int getY(){return y;}
    public Point2D getPoint2D(){return new Point2D.Double(getX(), getY());}
    public Floor getFloor(){
        return floor;
    }


    @Override
    public void update() {

    }

    public String toString(){
        return String.format("(%d, %d)", x, y);
    }

    public enum CellSide{
        NORTH,WEST,EAST,SOUTH
    }
}
