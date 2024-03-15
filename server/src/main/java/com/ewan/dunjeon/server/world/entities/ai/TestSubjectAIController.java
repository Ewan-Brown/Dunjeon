package com.ewan.dunjeon.server.world.entities.ai;

import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.util.List;
import java.util.Optional;

public class TestSubjectAIController extends CreatureController<TestSubject, TestSubject.TestSubjectControls>{
    public TestSubjectAIController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    private Long target = null;
    private static final List<Class<? extends Datas.EntityData>> CLASSES = List.of(Datas.EntityPositionalData.class);
    @Override
    public void update(double stepSize) {

        Optional<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>> selfQuery = getBasicMemoryBank().querySinglePackage(controls.getUUID(), Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));
        if(selfQuery.isEmpty()){
            //TODO Is this really a sensible path? I think the only case this will occur is during server loading, in which case the server should hold off updating AI until every creature's had a chance to gather data...
            return;
        }
        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> selfAccessor = selfQuery.get();

        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> targetAccessor = null;
        if(target != null){
            Optional<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>> querySinglePackage = getBasicMemoryBank().querySinglePackage(target, Datas.EntityData.class, CLASSES);
            if(querySinglePackage.isEmpty()){
                target = null;
            }else{
                targetAccessor = querySinglePackage.get();
            }
        }
        if(target == null) {
            BasicMemoryBank.MultiQueryAccessor<Long, Datas.EntityData> potentialTargets = getBasicMemoryBank().queryMultiPackage(Datas.EntityData.class, CLASSES);
            targetAccessor = potentialTargets.getIndividualAccessors().values().stream().findAny().get();
            target = targetAccessor.getIdentifier();
        }

        var targetPos = targetAccessor.getKnowledge(Datas.EntityPositionalData.class).getInfo().getPosition();
        var selfPos = selfAccessor.getKnowledge(Datas.EntityPositionalData.class).getInfo().getPosition();

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

        controls.setDesiredVelocity(thrust);
    }

}
