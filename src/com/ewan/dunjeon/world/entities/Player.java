package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.RelativeSoundEvent;

import java.awt.*;

public class Player extends Creature{
    public Player(Color c, String name) {
        super(c, name);
    }

    @Override
    public void onReceiveSound(RelativeSoundEvent event) {
        super.onReceiveSound(event);
        //TODO This needs to be refined. What if the sound eminates from an invisible entity that is in viewing range??
        boolean isVisible = getVisibleCells().contains(getFloor().getCellAt(event.abs().sourceLocation()));
        System.out.println(isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible());
    }
}
