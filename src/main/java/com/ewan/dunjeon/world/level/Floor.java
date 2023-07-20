package com.ewan.dunjeon.world.level;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import lombok.Getter;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.*;
import org.dyn4j.world.listener.CollisionListener;

import java.util.*;
import java.util.List;


public class Floor {

    private static long UUIDCounter = 0;
    @Getter
    final Long UUID;
    BasicCell[][] cells;
    List<Entity> entities = new ArrayList<>();
    World<Body> world = new World<>();
    List<ManifoldCollisionData<CollisionBody<Fixture>, Fixture>> collisionDataAccumulator = new ArrayList<>();

    @Getter
    public final int width;
    @Getter
    public final int height;

    public Floor(int w, int h){
        width = w;
        height = h;
        UUID = UUIDCounter;
        UUIDCounter++;

        //TODO Collisions are to be passed by datastream!
        CollisionListener<Body, BodyFixture> collisionListener = new CollisionListener<>() {

            @Override
            public boolean collision(BroadphaseCollisionData collision) {
                return false;
            }

            @Override
            public boolean collision(NarrowphaseCollisionData collision) {
                return false;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean collision(ManifoldCollisionData collision) {
                collisionDataAccumulator.add(collision);
                return true;

            };
        };

        world.setGravity(0,0);
        world.addCollisionListener(collisionListener);

    }

    public void setCells(BasicCell[][] cells){
        this.cells = cells;
    }

    public List<Entity> getEntities(){
        return entities;
    }

    public void addEntity(Entity e){
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
    }

    public int getWidth(){return cells[0].length;}
    public int getHeight(){return cells.length;}

    public BasicCell[][] getCells() {
        return cells;
    }

    public World<Body> getWorld(){return world;}


    public List<BasicCell> getCellsAsList(){
        List<BasicCell> cellsTotal = new ArrayList<>();
        for (BasicCell[] cellArr : cells) {
            cellsTotal.addAll(Arrays.asList(cellArr));
        }
        return cellsTotal;
    }

    public void updatePhysics(double stepSize){
        // Physics update
        // - (Pre-Dyn4J) Check for entities to be removed, clear accumulators
        collisionDataAccumulator.clear();

        // - (Dyn4J) Call physics update
        world.update(stepSize);

    }

    public void updateEntities(double stepSize){


        // AI Update
        // - Preprocess anything that might be useful ... another good spot for parallelization
        // - Iterate across them, updating and/or starting new "actions"
        // - - See if I can stick to an abstract 'Action' framework.
        // Human interface update
        // - Harvest UI/UX info, poll and update for controls.
        // - Pass user UI data

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            e.update(stepSize);
//            e.update();
//            doBoundsCheck(e);
        }

    }



//    public BasicCell getCellAt(double x, double y){
//        return getCellAt((int)Math.floor(x),(int)Math.floor(y));
//    }
//
//    public BasicCell getCellAt(Point2D point){
//        return getCellAt((double)Math.floor(point.getX()),(double)Math.floor(point.getY()));
//    }

//    public BasicCell getCellAt(Point point){
//        return getCellAt((int)point.getX(), (int)point.y);
//    }
    public BasicCell getCellAt(int x, int y){
        if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()){
            return null;
        }
        else {
            return cells[y][x];
        }
    }

    public BasicCell getCellAt(double x, double y){
        return getCellAt((int)Math.floor(x),(int)Math.floor(y));
    }
    public BasicCell getCellAt(Vector2 v){
        return getCellAt((int)Math.floor(v.x), (int)Math.floor(v.y));
    }
}
