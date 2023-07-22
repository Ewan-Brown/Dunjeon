package com.ewan.dunjeon.world;

import com.ewan.dunjeon.graphics.Graphics2DDisplay;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.world.level.Floor;
import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.game.Main.rand;

public class Dunjeon{

    private static Dunjeon dunjeon = new Dunjeon();

    /**
     * How much in-world time has passed
     */
    @Getter
    private double timeElapsed = 0;
    /**
     * How many individual world updates have occured
     */
    @Getter
    private int ticksElapsed = 0;

    private TestSubject testSubject;
    public static Dunjeon getInstance(){return dunjeon;}
    public static void resetDunjeon(){
        dunjeon = new Dunjeon();
    }

    List<Floor> floors = new ArrayList<>(); // TODO Should this be here, or should everything be stored in a node tree...?
    public void addLevel(Floor l){
        floors.add(l);
    }


    public Dunjeon(){
        initialize();
    }

    private void initialize() {

    }

    /**
     * Adds ,man entity at a random location On a level.
     * <i>TODO Add exception handling when there is no valid spots</i>
     */
    public boolean addEntityRandomLoc(Entity e, Floor l){
        List<BasicCell> validCells =  l.getCellsAsList().stream().filter(basicCell -> basicCell.canBeEntered(e)).collect(Collectors.toList());
        if(validCells.size() == 0) throw new Error("No valid spots for entity found");
        else {
            BasicCell randomValidCell = validCells.get(rand.nextInt(validCells.size()));
            e.translate(randomValidCell.getX() + 0.5d, randomValidCell.getY() + 0.5d);
            e.setFloor(l);
            l.addEntity(e);
            l.getWorld().addBody(e);
            return true;
        }

    }

    /*
    Updates the game, returns true if the game is over.
     */
    public void update(double t){
        this.timeElapsed += t;
        this.ticksElapsed += 1;


        //Remove entities, apply physics step and collect collisions
        getPlayer().getFloor().updatePhysics(t);

        //Update Datastreams!
        sightDataStream.update(this);

        //Update Controllers!
        getPlayer().getFloor().updateCreatureControllers(t);

        //Update Entities!
        getPlayer().getFloor().updateEntities(t);

    }


    //**************************************************
    //PLAYER SPECIFIC TODO MOVE THIS STUFF
    //**************************************************

    public TestSubject getPlayer(){
        return testSubject;
    }

    public void setPlayer(TestSubject p){ testSubject = p;}

    //****** Data streams ******//
    private Datastreams.SightDataStream sightDataStream = new Datastreams.SightDataStream();
    public Datastreams.SightDataStream getSightDataStream() {
        return sightDataStream;
    }

}
