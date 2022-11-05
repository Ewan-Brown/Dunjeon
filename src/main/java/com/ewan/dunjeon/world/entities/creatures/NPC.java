package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NPC extends Creature implements Interactable {
    public NPC(Color c, String name) {
        super(c, name);
    }

    @Override
    protected void processAI() {

    }

    @Override
    public void onInteract(Entity interactor, InteractionType type) {
        System.out.println("Hello, interactor.");
    }

    @Override
    public Set<InteractionType> getAvailableInteractions(Entity interactor) {
        return new HashSet<>(List.of(InteractionType.CHAT));
    }
}
