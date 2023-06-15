package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.data.Datastreams;
import com.ewan.dunjeon.world.entities.creatures.senses.Sense;
import com.ewan.dunjeon.world.entities.creatures.senses.Senses;
import com.ewan.dunjeon.world.entities.memory.Brain;
import com.ewan.dunjeon.world.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Player extends Creature {
    List<Sense<? extends Data, ? extends DataStreamParameters>> senses = new ArrayList<>();
    public Player(String name) {
        super(name);
        autoPickup = true;
        Sense<? extends Data, ? extends DataStreamParameters> sightSense = new Senses.EntitySightSense(this, Dunjeon.getInstance().getSightDataStream()) {
            @Override
            public Datastreams.SightDataStream.SightStreamParameters calculateDatastreamParameters() {
                return new Datastreams.SightDataStream.SightStreamParameters(10,Math.PI*2);
            }
        };
        senses.add(sightSense);

    }

    @Override
    public Brain getBrain() {
        return null;
    }

    @Override
    public List<Sense<? extends Data, ? extends DataStreamParameters>> getSenses() {
        return senses;
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

}
