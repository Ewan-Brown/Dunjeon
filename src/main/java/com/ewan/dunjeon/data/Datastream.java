package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.util.ArrayList;
import java.util.List;

public abstract class Datastream<S extends DataStreamParameters> {
    private List<Sensor<S>> subscribers = new ArrayList<>();
    public abstract void update(Dunjeon d);

    public void addSubscriber(Sensor<S> sensor){
        subscribers.add(sensor);
    }

    public void removeSubscriber(Sensor<S> sensor){
        subscribers.remove(sensor);
    }

    public List<Sensor<S>> getSubscribers() {
        return subscribers;
    }

    public abstract Sensor<S> constructSensorForDatastream(Creature c, Sensor.ParameterCalculator<S> pCalc);



}
