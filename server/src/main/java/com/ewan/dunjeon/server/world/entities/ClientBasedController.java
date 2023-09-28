package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.dunjeon.server.world.entities.creatures.CreatureControls;
import com.ewan.meworking.data.client.ClientAction;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientBasedController<C extends Creature, D extends CreatureControls<C>> extends CreatureController<C, D> {

    //TODO generify creature interfaces, and then this controller for player usage.
    //  AI controllers will have to be specific, per-controller for some cases i'm sure.

    //Player controller should have a default then overrides if necessary
    public ClientBasedController(C connectedCreature) {
        super(connectedCreature);
    }

    public final BasicMemoryBank getMemoryBank(){
        return memoryBank;
    }

    /**
     * TODO this has to gete cleared or emptied after actions are processed. BUT I have to probably introduce some sort of cache as new actions recevied from client go directly to this list.
     * IF the list is partway through processing and a new action is entered the outcome is undefined. this should ___NOT___ be up to individual implementations of update()
     * */
    @Getter
    private final List<ClientAction> unprocessedClientActions = new ArrayList<>();

    @Override
    public void update() {

    }
}
