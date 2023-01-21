package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.Entity;

import java.util.*;
import java.util.List;

public class NPC extends CreatureWithAI implements Interactable {

    public static NPC generateDumbNPC(String name){
        ArrayList<AIStateGenerator> generators = new ArrayList<>();

        generators.add(new AIStateGenerator(creature -> true, StandAroundIdiotAI::new));

        return new NPC(name, generators);
    }
    private NPC(String name, List<AIStateGenerator> generators) {
        super(name, generators);

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
