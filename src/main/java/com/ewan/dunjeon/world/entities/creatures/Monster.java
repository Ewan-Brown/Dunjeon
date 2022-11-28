package com.ewan.dunjeon.world.entities.creatures;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class Monster extends CreatureWithAI {
    private Monster(Color c, String name, List<AIStateGenerator> generators) {
        super(c, name, generators);
//        currentState = new ExploreAI(this);
    }

    public static Monster generateExploringMonster(Color c, String name){
        List<AIStateGenerator> gens = new ArrayList<>();

        gens.add(new AIStateGenerator(creature -> CreatureUtils.countUnexploredVisibleCells(creature) > 0, ExploreAI::new));

        gens.add(new AIStateGenerator(creature -> {
            return true;
        }, StandAroundIdiotAI::new));

        return new Monster(c, name, gens);
    }



}
