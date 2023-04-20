package com.ewan.dunjeon.world.sounds;

import com.ewan.dunjeon.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    List<AbsoluteSoundEvent> events = new ArrayList<>();
    public void exposeSound(AbsoluteSoundEvent event){
        events.add(event);
    }

    public void propogateSounds(){
        for (AbsoluteSoundEvent event : events) {
            for (Entity entity : event.sourceFloor().getEntities()) {
                double dX = event.sourceLocation().getX() - entity.getWorldCenter().x;
                double dY = event.sourceLocation().getY() - entity.getWorldCenter().y;
                double dist = Math.sqrt(dX*dX + dY*dY);
                double decibels = event.intensity() / (dist*dist); //Intensity decreases with inverse square law
                double angle = Math.atan2(dY, dX);
                RelativeSoundEvent relativeSoundEvent = new RelativeSoundEvent(decibels, angle, event);
                entity.onSoundEvent(relativeSoundEvent);
            }
        }
        events.clear();
    }
}
