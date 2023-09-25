package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;

public class ClientBasedTestSubjectController extends ClientBasedController<TestSubject, TestSubject.TestSubjectControls> {
    public ClientBasedTestSubjectController(TestSubject connectedCreature) {
        super(connectedCreature);
    }


}
