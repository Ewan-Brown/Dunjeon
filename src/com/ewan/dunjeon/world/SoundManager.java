package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.entities.Entity;

public class SoundManager {
    //TODO DO sound logic here - decrease in intensity exponentially, transform an absolute event into a relative event
    public void exposeSound(AbsoluteSoundEvent event){
        for (Entity entity : event.sourceFloor().getEntities()) {
            double dX = event.sourceLocation().getX() - entity.getX();
            double dY = event.sourceLocation().getY() - entity.getY();
            double dist = Math.sqrt(dX*dX + dY*dY);
            double decibels = event.decibels() / (dist*dist); //Intensity decreases with inverse square law
            double angle = Math.atan2(dY, dX);
            RelativeSoundEvent relativeSoundEvent = new RelativeSoundEvent(decibels, angle, event);
            entity.onReceiveSound(relativeSoundEvent);
        }
    }
}
