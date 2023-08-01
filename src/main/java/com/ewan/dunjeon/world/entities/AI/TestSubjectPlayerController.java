package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TestSubjectPlayerController extends CreatureController<TestSubject> {

    private final KeyBank keys;

    private final HashMap<Integer, Vector2> directionalKeys = new HashMap<>();
    private final HashMap<Integer, Double> angleKeys = new HashMap<Integer, Double>();

    public TestSubjectPlayerController(TestSubject connectedCreature, KeyBank keys) {
        super(connectedCreature);
        this.keys = keys;
        directionalKeys.put(KeyEvent.VK_W, new Vector2(0, 1));
        directionalKeys.put(KeyEvent.VK_A, new Vector2(-1, 0));
        directionalKeys.put(KeyEvent.VK_S, new Vector2(0, -1));
        directionalKeys.put(KeyEvent.VK_D, new Vector2(1, 0));

        angleKeys.put(KeyEvent.VK_Q, 1D);
        angleKeys.put(KeyEvent.VK_E, -1D);

    }

    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank basicMemoryBank = getConnectedCreature().getMemoryBank();
        Vector2 nominalDirection = new Vector2(0,0);
        Vector2 moveDirection;
        for (Map.Entry<Integer, Vector2> entry : directionalKeys.entrySet()) {
            if(keys.getKeySet().get(entry.getKey())){
                nominalDirection.add(entry.getValue());
            }
        }

        var selfKnowledge = basicMemoryBank.getCreatureKnowledgeHashMap().get(creatureInterface.getUUID());
        Vector2 selfSpeed = new Vector2();
        double selfAngle = 0;
        double selfAngularVelocity = 0;
        if(selfKnowledge != null) {
            var selfKineticKnowledge = selfKnowledge.get(Datas.EntityKineticData.class);
            if(selfKineticKnowledge != null) {
                selfSpeed = selfKineticKnowledge.getSpeed();
                selfAngle = selfKineticKnowledge.getRotation();
                selfAngularVelocity = selfKineticKnowledge.getRotationalSpeed();
            }
        }

        if(nominalDirection.getMagnitude() == 0){
            moveDirection = selfSpeed.copy().multiply(-5);
        }else {
            nominalDirection = nominalDirection.getNormalized();
            nominalDirection.multiply(10);
            Vector2 desiredSpeed = nominalDirection;
            Vector2 diff = selfSpeed.to(desiredSpeed);
            moveDirection = diff.setMagnitude(diff.getMagnitudeSquared());
        }

        double nominalTurn = 0;
        for (Map.Entry<Integer, Double> entry : angleKeys.entrySet()) {
            if(keys.getKeySet().get(entry.getKey())){
                nominalTurn += entry.getValue();
            }
        }

        double turnDirection = 0;
        if(nominalTurn == 0){
            turnDirection = -selfAngularVelocity;
        }else{
            turnDirection = nominalTurn*10;
        }

        creatureInterface.moveInDirection(moveDirection);
        creatureInterface.turn(turnDirection);
    }
}
