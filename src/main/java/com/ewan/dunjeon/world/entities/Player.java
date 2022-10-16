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
        int sourceX = (int)event.abs().sourceLocation().getX();
        int sourceY = (int)event.abs().sourceLocation().getY();
        boolean isVisible = !getFloorMemory(event.abs().sourceFloor()).getDataAt(sourceX, sourceY).isOldData();
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if(!message.isEmpty()) {
            System.out.println("["+message+"]");
        }
    }
}
