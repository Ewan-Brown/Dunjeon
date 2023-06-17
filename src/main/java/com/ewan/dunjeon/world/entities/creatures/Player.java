package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.data.Sense;
import com.ewan.dunjeon.data.Senses;
import com.ewan.dunjeon.world.entities.BasicMemoryBank;
import com.ewan.dunjeon.world.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Player extends Creature {
    List<Sense<? extends DataStreamParameters>> senses = new ArrayList<>();
    public Player(String name) {
        super(name);
        autoPickup = true;
        Sense<Datastreams.SightDataStream.SightStreamParameters> sightSense = new Sense<>(this, Dunjeon.getInstance().getSightDataStream(),
                c -> new Datastreams.SightDataStream.SightStreamParameters(10,Math.PI*2, Player.this.getWorldCenter()));
        senses.add(sightSense);

    }

    @Override
    public BasicMemoryBank getMemoryProcessor() {
        return null;
    }

    @Override
    public List<Sense<? extends DataStreamParameters>> getSenses() {
        return senses;
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

}
