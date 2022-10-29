package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.Interactable;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NPC extends Creature implements Interactable {
    public NPC(Color c, String name) {
        super(c, name);
    }

    @Override
    public void onInteract(Entity interactor, InteractionType type) {
        System.out.println("Hello, interactor.");
    }

//    @Override
//    public float getPosX() {
//        return 0;
//    }
//
//    @Override
//    public float getPosY() {
//        return 0;
//    }

//    @Override
//    public boolean isInteractable(Entity interactor) {
//        return true;
//    }

    @Override
    public Set<InteractionType> getAvailableInteractions(Entity interactor) {
        return new HashSet<>(Arrays.asList(InteractionType.CHAT));
    }
}
