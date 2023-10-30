package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.client.MoveEntity;

import java.util.List;

public class ClientBasedTestSubjectController extends ClientBasedController<TestSubject, TestSubject.TestSubjectControls> {
    public ClientBasedTestSubjectController(TestSubject connectedCreature) {
        super(connectedCreature);
        System.out.println("ClientBasedTestSubjectController.ClientBasedTestSubjectController");
    }

    @Override
    void updateWithUserInputs(List<UserInput> inputs) {
        for (UserInput input : inputs) {
            if(input instanceof MoveEntity moveEntityInput){
                getControls().turn(1);
            }
        }
    }


}
