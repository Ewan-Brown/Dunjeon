package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.data.Sensor;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class TestSubject extends Creature {
    List<Sensor<? extends DataStreamParameters>> senses = new ArrayList<>();
    BasicMemoryBank b = new BasicMemoryBank();
    private final TestSubjectInterface subjectInterface = new TestSubjectInterface();

    public TestSubject(String name) {
        super(name);
        senses.add(Dunjeon.getInstance().getSightDataStream().constructSensorForDatastream(this, c -> new Datastreams.SightDataStream.SightStreamParameters(10, Math.PI, new Vector2())));

    }

    public void update(double stepSize) {
        super.update(stepSize);
    }


    public BasicMemoryBank getMemoryBank() {
        return b;
    }

    protected List<Sensor<? extends DataStreamParameters>> getSensors() {
        return senses;
    }

    public TestSubjectInterface getInterface() {
        return subjectInterface;
    }


    public class TestSubjectInterface extends CreatureControls {
        public void moveInDirection(Vector2 v){
            TestSubject.super.applyForce(v);
        }

        public Vector2 getCurrentSpeed(){return TestSubject.super.getLinearVelocity();}

    }

}
