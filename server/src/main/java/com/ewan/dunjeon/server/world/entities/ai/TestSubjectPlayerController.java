package com.ewan.dunjeon.server.world.entities.ai;

import com.ewan.meworking.data.server.data.Datas;
import com.ewan.dunjeon.input.KeyBank;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSubjectPlayerController extends CreatureController<TestSubject> {

    private final KeyBank keys;

    private final HashMap<Integer, Vector2> directionalKeys = new HashMap<>();
    private final HashMap<Integer, Double> angleKeys = new HashMap<Integer, Double>();

    public TestSubjectPlayerController(TestSubject connectedCreature) {
        super(connectedCreature);
        this.keys = new KeyBank();
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

        BasicMemoryBank.QueryResult<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>, Boolean> selfQuery = basicMemoryBank.querySinglePackage(creatureInterface.getUUID(), Datas.EntityData.class, List.of(Datas.EntityKineticData.class, Datas.EntityPositionalData.class));
        if(!selfQuery.status()){
            return;
        }
        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> selfDataAccessor = selfQuery.result();
        Vector2 selfSpeed = selfDataAccessor.getKnowledge(Datas.EntityKineticData.class).getInfo().getSpeed();
        double selfAngle = selfDataAccessor.getKnowledge(Datas.EntityKineticData.class).getInfo().getRotation();
        double selfAngularVelocity = selfDataAccessor.getKnowledge(Datas.EntityKineticData.class).getInfo().getRotationalSpeed();

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
