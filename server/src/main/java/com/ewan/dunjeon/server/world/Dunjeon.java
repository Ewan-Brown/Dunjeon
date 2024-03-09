package com.ewan.dunjeon.server.world;

import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.ClientBasedTestSubjectController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.server.world.floor.Floor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Dunjeon{

    static Logger logger = LogManager.getLogger();

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
        logger.debug("calling Update");
        this.timeElapsed += t;
        this.ticksElapsed += 1;

        for (int i = 0; i < floors.size(); i++) {
            Floor floor = floors.get(i);
            floor.updatePhysics(t);
            floor.updateCreatureControllers(t);
            floor.updateEntities(t);
        }

        //Update Datastreams!
        //TODO SHOULD DATASTREAMS POTENTIALLY BE PER-FLOOR??????
        sightDataStream.update(this);
        logger.debug("sightDataStream done");
    }

    public ClientBasedController<TestSubject, TestSubject.TestSubjectControls> createClientTestCreatureAndGetController(){
        TestSubject testSubject = new TestSubject("Player");
        testSubject.addFixture(new Rectangle(0.5,0.5));
        testSubject.setMass(new Mass(new Vector2(),1,1));
        // Do this for raycasting testing
        testSubject.rotate(-Math.PI);

        ClientBasedController<TestSubject, TestSubject.TestSubjectControls> controller = new ClientBasedTestSubjectController(testSubject);

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
