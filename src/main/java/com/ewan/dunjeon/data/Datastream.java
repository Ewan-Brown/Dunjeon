package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class Datastream<T extends Data, S extends DataStreamParameters> {
    private List<Sense<T, S>> subscribers = new ArrayList<>();
    public abstract void update(Dunjeon d);
    public abstract T generateDataForParams(S params);

    public void addSubscriber(Sense<T, S> sense){
        subscribers.add(sense);
    }

    public void removeSubscriber(Sense<T, S> sense){
        subscribers.remove(sense);
    }

    public List<Sense<T, S>> getSubscribers() {
        return subscribers;
    }

    public abstract List<Event> retrieveEventsForParams(S params);


}
