package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TestSubjectPlayerController extends CreatureController<TestSubject> {

    private final KeyBank keys;
    private final HashMap<Integer, Vector2> directionalKeys = new HashMap<>();

    public TestSubjectPlayerController(TestSubject connectedCreature, KeyBank keys) {
        super(connectedCreature);
        this.keys = keys;
        directionalKeys.put(KeyEvent.VK_W, new Vector2(0, 1));
        directionalKeys.put(KeyEvent.VK_A, new Vector2(-1, 0));
        directionalKeys.put(KeyEvent.VK_S, new Vector2(0, -1));
        directionalKeys.put(KeyEvent.VK_D, new Vector2(1, 0));

    }

    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        Vector2 nominalDirection = new Vector2(0,0);
        Vector2 moveDirection;
        for (Map.Entry<Integer, Vector2> entry : directionalKeys.entrySet()) {
            if(keys.getKeySet().get(entry.getKey())){
                nominalDirection.add(entry.getValue());
            }
        }
        if(nominalDirection.getMagnitude() == 0){
            moveDirection = creatureInterface.getCurrentSpeed().copy().multiply(-5);
        }else {
            nominalDirection = nominalDirection.getNormalized();
            nominalDirection.multiply(10);
            Vector2 desiredSpeed = nominalDirection;
            Vector2 creatureSpeed = creatureInterface.getCurrentSpeed();
            Vector2 diff = creatureSpeed.to(desiredSpeed);
            moveDirection = diff.setMagnitude(diff.getMagnitudeSquared());
        }


        creatureInterface.moveInDirection(moveDirection);
    }
}
