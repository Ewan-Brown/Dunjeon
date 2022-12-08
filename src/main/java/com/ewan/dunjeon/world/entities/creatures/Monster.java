package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import org.w3c.dom.ls.LSOutput;

import java.awt.Color;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    @Override
    public void update() {
        super.update();
    }

    public static Monster generateChasingMonster(Color c, String name){
        List<AIStateGenerator> gens = new ArrayList<>();

        gens.add(new AIStateGenerator(creature -> creature.getCurrentFloorMemory().getEntityMemory().stream().anyMatch(entityMemory -> !entityMemory.isOldData()), creature -> new ChaseAI(creature, creature.getCurrentFloorMemory().getEntityMemory().stream().filter(entityMemory -> !entityMemory.isOldData()).findAny().orElse(null).getUUID())));

        gens.add(new AIStateGenerator(creature -> CreatureUtils.countUnexploredVisibleCells(creature) > 0, ExploreAI::new));

        gens.add(new AIStateGenerator(creature -> {
            return true;
        }, StandAroundIdiotAI::new));

        return new Monster(c, name, gens);
    }



}
