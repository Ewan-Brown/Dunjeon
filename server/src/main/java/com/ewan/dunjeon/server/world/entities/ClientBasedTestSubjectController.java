package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.data.client.TurnEntity;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.client.MoveEntity;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class ClientBasedTestSubjectController extends ClientBasedController<TestSubject, TestSubject.TestSubjectControls> {
    public ClientBasedTestSubjectController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    private Vector2 currentMoveVector = new Vector2(0,0);
    private double currentTurn = 0;

    @Override
    void updateWithUserInputs(List<UserInput> inputs) {
        System.out.println("ClientBasedTestSubjectController.updateWithUserInputs");
        for (UserInput input : inputs) {
            if(input instanceof MoveEntity moveEntityInput){
                currentMoveVector = moveEntityInput.getMoveDir().multiply(10);
            }
            if(input instanceof TurnEntity turnEntity){
                currentTurn = turnEntity.getTurn();
            }
        }
        getControls().moveInDirection(currentMoveVector);
        getControls().turn(currentTurn);

    }


}
