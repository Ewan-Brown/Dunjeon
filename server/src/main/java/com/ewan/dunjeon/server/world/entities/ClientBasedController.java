package com.ewan.dunjeon.server.world.entities;

import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.dunjeon.server.world.entities.creatures.CreatureControls;
import com.ewan.meworking.data.client.UserInput;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientBasedController<C extends Creature, D extends CreatureControls<C>> extends CreatureController<C, D> {

    static Logger logger = LogManager.getLogger();

    //Player controller should have a default then overrides if necessary
    public ClientBasedController(C connectedCreature) {
        super(connectedCreature);
    }

    /**
     * Acts as a buffer for inputs that have been received very recently and are to be processed in the next update, and this list erased.
     */
    @Getter
    private List<UserInput> userInputBuffer = new ArrayList<>();

    @Override
    public final void update(double stepSize) {
        List<UserInput> inputsReadyForProcessing = getUserInputBuffer();
        userInputBuffer = new ArrayList<>();

        updateWithUserInputs(inputsReadyForProcessing);
    }

    abstract void updateWithUserInputs(List<UserInput> inputs);

}
