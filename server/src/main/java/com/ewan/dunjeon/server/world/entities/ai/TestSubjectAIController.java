package com.ewan.dunjeon.server.world.entities.ai;

import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class TestSubjectAIController extends CreatureController<TestSubject, TestSubject.TestSubjectControls>{
    public TestSubjectAIController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    private Long target = null;
    private static final List<Class<? extends Datas.EntityData>> CLASSES = List.of(Datas.EntityPositionalData.class);
    @Override
    public void update() {

        BasicMemoryBank.QueryResult<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>, Boolean> selfQuery = memoryBank.querySinglePackage(controls.getUUID(), Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));
        if(!selfQuery.status()){
            return;
        }
        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> selfAccessor = selfQuery.result();

        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> accessor = null;
        if(target != null){
            BasicMemoryBank.QueryResult<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>, Boolean> querySinglePackage = memoryBank.querySinglePackage(target, Datas.EntityData.class, CLASSES);
            if(!querySinglePackage.status()){
                target = null;
            }else{
                accessor = querySinglePackage.result();
            }
        }
        if(target == null) {
            BasicMemoryBank.MultiQueryAccessor<Long, Datas.EntityData> a = memoryBank.queryMultiPackage(Datas.EntityData.class, CLASSES);
            accessor = a.getIndividualAccessors().values().stream().findAny().get();
            target = accessor.getIdentifier();
        }

        var targetPos = accessor.getKnowledge(Datas.EntityPositionalData.class).getInfo().getPosition();
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

        controls.moveInDirection(thrust);
    }

}
