package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Door extends Furniture implements Interactable {

    boolean open;
    static Color BROWN = new Color(165, 42, 42);

    public Door(boolean isOpen) {
        open = isOpen;
    }

    @Override
    public Color getColor() {
        return (open) ? null : BROWN;
    }

    public boolean isBlocking(){
        return !open;
    }

    //TODO IF entity closes door while inside door cell, it will get closed on
    public void onInteract(Entity e, Set<InteractionType> types) {
        open = !open;
    }

    @Override
    public void onInteract(Entity interactor, InteractionType type) {
        System.out.println((open) ? "the door slams shut" : "the door swings open");
        open = !open;

    }

//    @Override
//    public boolean isInteractable(Entity interactor) {
//        return true;
//    }

    @Override
    public Set<InteractionType> getAvailableInteractions(Entity interactor) {
        return new HashSet<>(Arrays.asList(InteractionType.TOUCH));
    }
}
