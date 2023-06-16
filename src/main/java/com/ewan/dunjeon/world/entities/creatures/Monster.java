package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.entities.creatures.AI.CreatureWithAI;
import com.ewan.dunjeon.world.entities.creatures.senses.Sense;
import com.ewan.dunjeon.world.entities.memory.Brain;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class Monster extends CreatureWithAI {
    private Monster(String name, List<AIStateGenerator> generators) {
        super(name, generators);
    }

    public static Monster generateIdiotMonster(Color c, String name){
        List<AIStateGenerator> gens = new ArrayList<>();

        gens.add(new AIStateGenerator(creature -> {
            return true;
        }, StandAroundIdiotAI::new));

        return new Monster(name, gens);
    }

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
    }

    @Override
    public Brain getBrain() {
        return null;
    }

    @Override
    public List<Sense<? extends Data, ? extends DataStreamParameters>> getSenses() {
        return new ArrayList<>();
    }


}
