package com.ewan.dunjeon.world.sounds;

import com.ewan.dunjeon.world.entities.KinematicEntity;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    List<AbsoluteSoundEvent> events = new ArrayList<>();
    public void exposeSound(AbsoluteSoundEvent event){
        events.add(event);
    }

    public void propogateSounds(){
        for (AbsoluteSoundEvent event : events) {
            for (KinematicEntity entity : event.sourceFloor().getEntities()) {
                double dX = event.sourceLocation().getX() - entity.getPosX();
                double dY = event.sourceLocation().getY() - entity.getPosY();
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
