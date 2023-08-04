package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.world.entities.memory.KnowledgeFragment;
import com.ewan.dunjeon.world.entities.memory.KnowledgePackage;
import org.dyn4j.geometry.Vector2;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestSubjectAIController extends CreatureController<TestSubject>{
    public TestSubjectAIController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    private Long target = null;
    @Override
    public void update() {
        TestSubject.TestSubjectInterface creatureInterface = getConnectedCreature().getInterface();
        BasicMemoryBank memoryBank = getConnectedCreature().getMemoryBank();

        if(target == null){
            List<Long> potentialTargets = memoryBank.getCreatureKnowledgeHashMap().keySet().stream().filter(l -> l != creatureInterface.getUUID()).toList();
            if(potentialTargets.size() == 0){
                //No targets in memory! Do nothing
                return;
            }

            //Choose target quasi-randomly
            target = potentialTargets.get(0);
        }

        var targetMemory = memoryBank.getCreatureKnowledgeHashMap().get(target);
        var targetPos = targetMemory.get(Datas.EntityPositionalData.class).getInfo().getPosition();
        var selfPos = memoryBank.getCreatureKnowledgeHashMap().get(creatureInterface.getUUID()).get(Datas.EntityPositionalData.class).getInfo().getPosition();

        var posDiff = selfPos.to(targetPos);
        var posDiffMagnitude = posDiff.getMagnitude();

        Vector2 thrust = new Vector2();

        int minDist = 1;
        int maxDist = 2;

        if(posDiffMagnitude < minDist){
            thrust = new Vector2(posDiff.copy().multiply(-minDist/posDiffMagnitude));
        }if(posDiffMagnitude > maxDist){
            thrust = new Vector2(posDiff);
        }

        creatureInterface.moveInDirection(thrust);
    }

//    public static class MemoryQuery
}
