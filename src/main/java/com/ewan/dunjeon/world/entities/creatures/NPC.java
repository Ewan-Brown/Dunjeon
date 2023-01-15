package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.entities.KinematicEntity;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    public void onInteract(KinematicEntity interactor, InteractionType type) {
        System.out.println("Hello, interactor.");
    }

    @Override
    public Set<InteractionType> getAvailableInteractions(KinematicEntity interactor) {
        return new HashSet<>(List.of(InteractionType.CHAT));
    }
}
