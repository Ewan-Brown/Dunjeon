package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TestSubjectAI extends CreatureController<TestSubject> {

    private final KeyBank keys;
    private final HashMap<Integer, Vector2> directionalKeys = new HashMap<>();

    public TestSubjectAI(TestSubject connectedCreature, KeyBank keys) {
        super(connectedCreature);
        this.keys = keys;
        directionalKeys.put(KeyEvent.VK_W, new Vector2(0, 1));
        directionalKeys.put(KeyEvent.VK_A, new Vector2(-1, 0));
        directionalKeys.put(KeyEvent.VK_S, new Vector2(0, -1));
        directionalKeys.put(KeyEvent.VK_D, new Vector2(1, 0));

    }

    @Override
    public void update() {
        System.out.println(keys.getKeysDown());
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank memoryBank = getConnectedCreature().getMemoryProcessor();
        Vector2 nominalDirection = new Vector2(0,0);
        for (Map.Entry<Integer, Vector2> entry : directionalKeys.entrySet()) {
            if(keys.getKeySet().get(entry.getKey())){
                nominalDirection.add(entry.getValue());
            }
        }
        nominalDirection = nominalDirection.getNormalized();

        creatureInterface.moveInDirection(nominalDirection, 1);
    }
}
