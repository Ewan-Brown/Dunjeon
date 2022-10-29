package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public interface Interactable {
    public enum InteractionType{
        TOUCH, CHAT
    }

    public void onInteract(Entity interactor, InteractionType type);
//    public boolean isInteractable(Entity interactor);
    public float getPosX();
    public float getPosY();
    public Set<InteractionType> getAvailableInteractions(Entity interactor);
}
