package com.ewan.dunjeon.server.world.cells;

import com.ewan.dunjeon.server.world.entities.Entity;
import com.ewan.dunjeon.server.world.floor.Floor;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;
import java.awt.geom.Point2D;

public class BasicCell extends Body {
    int x;
    int y;
    Floor floor;
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
        this.addFixture();
        this.setMass(MassType.INFINITE);
        this.translate(x+0.5, y+0.5);
    }

    public void setFilled(boolean f){
        filled = f;
        setEnabled(filled);
    }

    public void addFixture(){
        this.addFixture(new Rectangle(1,1));
        this.fixtures.forEach(bodyFixture -> {
            final short WALL_CATEGORY = 0x0002; // 2 in binary
            bodyFixture.setFilter(new CategoryFilter(WALL_CATEGORY, (short) ~WALL_CATEGORY));
        });
    }

    /*
     Don't forget about me :)
     */
    public boolean canBeSeenThrough(Entity e){
        return canBeEntered(e);
    }

    public boolean canBeEntered(Entity e){
        return !filled;
    }

    public int getIntegerX(){return x;}
    public int getIntegerY(){return y;}
    public Floor getFloor(){
        return floor;
    }


    public void update() {

    }

    public String toString(){
        return String.format("(%d, %d)", x, y);
    }

}
