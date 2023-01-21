package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.items.Item;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

public class Player extends Creature {
    public Player(String name) {
        super(name);
        autoPickup = true;
//        true_sight_debug;
    }

    @Override
    public void onSoundEvent(RelativeSoundEvent event) {
        super.onSoundEvent(event);
        int sourceX = (int) event.abs().sourceLocation().getX();
        int sourceY = (int) event.abs().sourceLocation().getY();
        boolean isVisible = CreatureUtils.isCellCurrentlyVisible(this, sourceX, sourceY);
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if (!message.isEmpty()) {
            System.out.println("[" + message + "]");
        }
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

    public void stabWithWieldedWeapon(){
        System.err.println("No weapon equipped!");
    }

}
