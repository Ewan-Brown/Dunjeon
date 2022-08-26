package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.entities.Entity;

import java.util.Map;

public interface Interactable {
    public void onInteract(Entity interactor);
    public boolean isInteractable(Entity interactor);
    public float getX();
    public float getY();
}
