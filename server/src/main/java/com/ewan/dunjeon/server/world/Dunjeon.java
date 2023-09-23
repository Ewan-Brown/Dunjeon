package com.ewan.dunjeon.server.world;

import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.dunjeon.server.world.entities.ai.TestSubjectPlayerController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.server.world.floor.Floor;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import lombok.Getter;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Dunjeon{

    private static final Dunjeon dunjeon = new Dunjeon();

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

    public static Dunjeon getInstance(){return dunjeon;}

    List<Floor> floors = new ArrayList<>(); // TODO Should this be here, or should everything be stored in a node tree...?
    public void addLevel(Floor l){
        floors.add(l);
    }

    /*
    Updates the game, returns true if the game is over.
     */
    public void update(double t){
        this.timeElapsed += t;
        this.ticksElapsed += 1;

        for (Floor floor : floors) {
            floor.updatePhysics(t);
            floor.updateCreatureControllers(t);
            floor.updateEntities(t);
        }

        //Update Datastreams!
        //TODO COULD DATASTREAMS POTENTIALLY BE PER-FLOOR??????
        sightDataStream.update(this);
    }

    public CreatureController<?> createClientTestCreatureAndGetController(){
        TestSubject testSubject = new TestSubject("Player", true);
        testSubject.addFixture(new Rectangle(0.5,0.5));
        testSubject.setMass(new Mass(new Vector2(),1,1));

        CreatureController<?> controller = new TestSubjectPlayerController(testSubject);

        this.floors.get(0).addEntityRandomLoc(testSubject);
        this.floors.get(0).addCreatureController(controller);
        return controller;
    }


    //****** Data streams ******//
    private final Datastreams.SightDataStream sightDataStream = new Datastreams.SightDataStream();
    public Datastreams.SightDataStream getSightDataStream() {
        return sightDataStream;
    }

}
