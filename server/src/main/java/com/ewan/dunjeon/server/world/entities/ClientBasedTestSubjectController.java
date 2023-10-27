package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.data.client.ClientAction;
import com.ewan.meworking.data.client.MoveEntity;

import java.util.List;

public class ClientBasedTestSubjectController extends ClientBasedController<TestSubject, TestSubject.TestSubjectControls> {
    public ClientBasedTestSubjectController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    @Override
    void updateWithClientActions(List<ClientAction> actions) {
        System.out.println("ClientBasedTestSubjectController.updateWithClientActions");
        for (ClientAction action : actions) {
            System.out.println("Action: " + action.toString());
            if(action instanceof MoveEntity moveEntityAction){
                getControls().moveInDirection(moveEntityAction.getMoveDir());
            }
        }
    }


}
