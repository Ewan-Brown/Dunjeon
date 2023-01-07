package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Player extends Creature {
    public Player(Color c, String name) {
        super(c, name);
        autoPickup = true;
//        true_sight_debug;
    }

    @Override
    public void processSound(RelativeSoundEvent event) {
        super.processSound(event);
        int sourceX = (int) event.abs().sourceLocation().getX();
        int sourceY = (int) event.abs().sourceLocation().getY();
        boolean isVisible = CreatureUtils.isCellCurrentlyVisible(this, sourceX, sourceY);
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if (!message.isEmpty()) {
            System.out.println("[" + message + "]");
        }
    }

}
