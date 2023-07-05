package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.function.Consumer;

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
        this.addFixture(new Rectangle(1,1));
        this.setFilled(true);
        this.fixtures.forEach(bodyFixture -> {
            final short WALL_CATEGORY = 0x0002; // 2 in binary
            bodyFixture.setFilter(new CategoryFilter(WALL_CATEGORY, (short) ~WALL_CATEGORY));
        });
//        this.translate(-0.5d, -0.5d);
    }

    public void setFilled(boolean f){
        filled = f;
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


    public void update() {

    }

    public String toString(){
        return String.format("(%d, %d)", x, y);
    }

}
