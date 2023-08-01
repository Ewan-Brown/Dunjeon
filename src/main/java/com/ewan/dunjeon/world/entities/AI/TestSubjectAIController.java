package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.world.entities.memory.KnowledgeFragment;
import com.ewan.dunjeon.world.entities.memory.KnowledgePackage;

public class TestSubjectAIController extends CreatureController<TestSubject>{
    public TestSubjectAIController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank memoryBank = getConnectedCreature().getMemoryBank();
        creatureInterface.turn(1);
//        creatureInterface.moveInDirection(moveDirection);
    }
}
