package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class Monster extends Creature{
    public Monster(Color c, String name) {
        super(c, name);
        ai = new ExploreAI(this);
    }

    AI ai;

    @Override
    protected void processAI() {

        ai.process();

    }



}
