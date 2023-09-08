package com.ewan.dunjeon.server.world;

import com.ewan.dunjeon.server.world.entities.Entity;

import java.util.Set;

public interface Interactable {
    public enum InteractionType{
        TOUCH, CHAT
    }

    public void onInteract(Entity interactor, InteractionType type);
//    public boolean isInteractable(Entity interactor);
    public Set<InteractionType> getAvailableInteractions(Entity interactor);
}
