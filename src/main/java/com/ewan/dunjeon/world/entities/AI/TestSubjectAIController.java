package com.ewan.dunjeon.world.entities.AI;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.util.List;

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
            List<Long> potentialTargets = memoryBank.getIdentifiersForAllValid(Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));
            if(potentialTargets.size() == 0){
                //No targets in memory! Do nothing
                return;
            }

            //Choose target quasi-randomly
            target = potentialTargets.get(0);
        }

        var targetPos = memoryBank.getDataFragment(Datas.EntityPositionalData.class, target).result().getInfo().getPosition();
        var selfPos = memoryBank.getDataFragment(Datas.EntityPositionalData.class, creatureInterface.getUUID()).result().getInfo().getPosition();

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

}
