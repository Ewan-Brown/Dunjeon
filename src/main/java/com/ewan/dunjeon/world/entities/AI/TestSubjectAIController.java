package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;

public class TestSubjectAIController extends TestSubjectPlayerController{
    public TestSubjectAIController(TestSubject connectedCreature, KeyBank keys) {
        super(connectedCreature, keys);
    }

    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank memoryBank = getConnectedCreature().getMemoryBank();
//        creatureInterface.moveInDirection(moveDirection);
    }
}
