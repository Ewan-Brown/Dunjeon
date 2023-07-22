package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

public class TestSubjectAI extends CreatureController<TestSubject> {

    public TestSubjectAI(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank memoryBank = getConnectedCreature().getMemoryProcessor();
        creatureInterface.moveInDirection(new Vector2(1,0),1);
    }
}
