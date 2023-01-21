package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Container extends Furniture implements Interactable {

    private static Color CLOSED_COLOR = Color.magenta;
    private static Color OPEN_COLOR = new Color(150, 0, 150);

    private boolean unOpened = true;

    @Override
    public Color getColor() {
        return unOpened ? CLOSED_COLOR : OPEN_COLOR;
    }

    @Override
    public void onInteract(Entity interactor, InteractionType type) {
        unOpened = false;
    }

    @Override
    public Set<InteractionType> getAvailableInteractions(Entity interactor) {
        return new HashSet<>();
    }
}
