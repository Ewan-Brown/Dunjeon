package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.world.entities.BasicMemoryBank;
import com.ewan.dunjeon.world.items.Item;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player extends Creature {
    List<Sensor<? extends DataStreamParameters>> senses = new ArrayList<>();
    BasicMemoryBank b = new BasicMemoryBank();

    public Player(String name) {
        super(name);
        autoPickup = true;
        senses.add(Dunjeon.getInstance().getSightDataStream().constructSensorForDatastream(this, c -> new Datastreams.SightDataStream.SightStreamParameters(10, Math.PI, new Vector2())));

    }

    @Override
    public BasicMemoryBank getMemoryProcessor() {
        return b;
    }

    @Override
    public List<Sensor<? extends DataStreamParameters>> getSensors() {
        return senses;
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

}
