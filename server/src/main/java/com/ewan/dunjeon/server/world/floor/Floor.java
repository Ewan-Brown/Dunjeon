package com.ewan.dunjeon.server.world.floor;

import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.dunjeon.server.world.cells.BasicCell;
import com.ewan.dunjeon.server.world.entities.Entity;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.*;
import org.dyn4j.world.listener.CollisionListener;

import java.util.*;
import java.util.List;

import static com.ewan.dunjeon.server.game.StartServer.rand;


//TODO Need to take a look at what is really FLOOR dependent and what is more global...
public class Floor {

    private static long UUIDCounter = 0;
    @Getter
    final Long UUID;
    BasicCell[][] cells;
    Set<Entity> entities = new HashSet<>();
    Set<CreatureController<?, ?>> creatureControllers = new HashSet<>();
    World<Body> world = new World<>();
    Set<ManifoldCollisionData<CollisionBody<Fixture>, Fixture>> collisionDataAccumulator = new HashSet<>();
    static Logger logger = LogManager.getLogger();

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
                logger.info("Floor.collision - Broadphase");
                return true;
            }

            @Override
            public boolean collision(NarrowphaseCollisionData collision) {
                logger.info("Floor.collision - Narrowphase");
                return true;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean collision(ManifoldCollisionData collision) {
                logger.info("Floor.collision - Manifold");
                collisionDataAccumulator.add(collision);
                return true;

            }
        };

        world.setGravity(0,0);
//        world.addCollisionListener(collisionListener);

    }

    public void setCells(BasicCell[][] cells){
        this.cells = cells;
    }

    public Set<Entity> getEntities(){
        return entities;
    }

    public void addEntity(Entity e){
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
    }

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

        for (Entity entity : entities) {
            entity.update(stepSize);
        }

    }

    public void updateCreatureControllers(double stepSize){
        for (CreatureController<?, ?> creatureController : creatureControllers) {
            creatureController.update(stepSize);
        }
    }

    public BasicCell getCellAt(int x, int y){
        if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()){
            return null;
        }
        else {
            return cells[y][x];
        }
    }

    public void addCreatureController(CreatureController<?, ?> c){
        creatureControllers.add(c);
    }

    public void addEntitySpecificLocation(Entity e, double x, double y, double a){
        BasicCell matchingCell = getCellAt((int)Math.floor(x), (int)Math.floor(y));
        if(matchingCell == null) throw new Error(String.format("Cell at %f, %f is not valid because null", x, y));
        if(matchingCell.isFilled()) throw new Error(String.format("Cell at %f, %f is not valid because it's filled", x, y));
        else {
            e.rotate(a, 0, 0);
            e.translate(x, y);
            e.setFloor(this);
            addEntity(e);
            getWorld().addBody(e);
        }

    }

    public void addEntityRandomLoc(Entity e){
        List<BasicCell> validCells =  getCellsAsList().stream().filter(basicCell -> basicCell.canBeEntered(e)).toList();
        if(validCells.isEmpty()) throw new Error("No valid spots for entity found");
        else {
            BasicCell randomValidCell = validCells.get(rand.nextInt(validCells.size()));
            e.translate(randomValidCell.getIntegerX() + 0.5, randomValidCell.getIntegerY() + 0.5);
            e.setFloor(this);
            addEntity(e);
            getWorld().addBody(e);
        }

    }

    public BasicCell getCellAt(double x, double y){
        return getCellAt((int)Math.floor(x),(int)Math.floor(y));
    }
    public BasicCell getCellAt(Vector2 v){
        return getCellAt((int)Math.floor(v.x), (int)Math.floor(v.y));
    }
}
