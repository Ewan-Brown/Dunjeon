package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.data.client.TurnEntity;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.client.MoveEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class ClientBasedTestSubjectController extends ClientBasedController<TestSubject, TestSubject.TestSubjectControls> {
    public ClientBasedTestSubjectController(TestSubject connectedCreature) {
        super(connectedCreature);
    }
    double currentTurn = 0;
    Vector2 currentMoveVector = new Vector2(0, 0);

    static Logger logger = LogManager.getLogger();

    @Override
    void updateWithUserInputs(List<UserInput> inputs) {
        if(logger.isTraceEnabled())
            logger.trace("updateWithUserInputs, # of inputs: " + inputs.size());
        for (UserInput input : inputs) {
            if(input instanceof MoveEntity moveEntityInput){
                logger.debug("received moveEntity control: " + moveEntityInput.getMoveDir());
                currentMoveVector = moveEntityInput.getMoveDir().multiply(10);
            }
            if(input instanceof TurnEntity turnEntity){
                logger.debug("received turnEntity control: " + turnEntity.getTurn());
                currentTurn = turnEntity.getTurn();
            }
        }
        getControls().setDesiredVelocity(currentMoveVector);
        getControls().setDesiredAngularVelocity(currentTurn);

    }


}
