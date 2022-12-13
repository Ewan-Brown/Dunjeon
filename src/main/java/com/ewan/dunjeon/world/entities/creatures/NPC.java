package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class NPC extends CreatureWithAI implements Interactable {

    public static NPC generateDumbNPC(Color c, String name){
        ArrayList<AIStateGenerator> generators = new ArrayList<>();

        generators.add(new AIStateGenerator(creature -> true, StandAroundIdiotAI::new));

        return new NPC(c, name, generators);
    }
    private NPC(Color c, String name, List<AIStateGenerator> generators) {
        super(c, name, generators);

    }

    @Override
    public void onInteract(Entity interactor, InteractionType type) {
        System.out.println("Hello, interactor.");
    }

    @Override
    public Set<InteractionType> getAvailableInteractions(Entity interactor) {
        return new HashSet<>(List.of(InteractionType.CHAT));
    }
}
