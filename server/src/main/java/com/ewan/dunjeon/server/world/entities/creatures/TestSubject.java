package com.ewan.dunjeon.server.world.entities.creatures;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeoncommon.memory.BasicMemoryBank;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class TestSubject extends Creature {
    List<Sensor<? extends DataStreamParameters>> senses = new ArrayList<>();
    BasicMemoryBank b = new BasicMemoryBank();
    private final TestSubjectInterface subjectInterface = new TestSubjectInterface();

    public TestSubject(String name) {
        this(name, false);

    }

    public TestSubject(String name, Boolean trueSight) {
        super(name);
        senses.add(Dunjeon.getInstance().getSightDataStream().constructSensorForDatastream(this, c ->
                new Datastreams.SightDataStream.SightStreamParameters(10, Math.PI/4,getRotationAngle() , getWorldCenter(), trueSight)));

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

        public long getUUID(){return TestSubject.super.getUUID();}

        public void moveInDirection(Vector2 v){
            TestSubject.super.applyForce(v);
        }

        public void turn(double d){
            TestSubject.super.applyTorque(d);
        }

        public Vector2 getCurrentSpeed(){return TestSubject.super.getLinearVelocity();}

    }

}
