package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.dunjeon.server.world.entities.creatures.CreatureControls;
import com.ewan.meworking.data.client.ClientAction;
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

    /**
     * Acts as a buffer for actions that have been received very recently and are to be processed in the next update, and this list erased.
     */
    @Getter
    private List<ClientAction> actionBuffer = new ArrayList<>();

    @Override
    public final void update(double stepSize) {
        System.out.println("ClientBasedController.update");
        List<ClientAction> actionsReadyForProcessing = getActionBuffer();
        actionBuffer = new ArrayList<>();

        updateWithClientActions(actionsReadyForProcessing);
    }

    abstract void updateWithClientActions(List<ClientAction> actions);
}
