package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;

public class Player extends Creature{
    public Player(Color c, String name) {
        super(c, name);
    }

    @Override
    public void processSound(RelativeSoundEvent event) {
        super.processSound(event);
        //TODO This needs to be refined. What if the sound eminates from an invisible entity that is in viewing range??
        boolean isVisible = getVisibleCells().contains(getFloor().getCellAt(event.abs().sourceLocation()));
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if(!message.isEmpty()) {
            System.out.println("["+message+"]");
        }
    }
}
