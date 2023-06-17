package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class Datastream<S extends DataStreamParameters> {
    private List<Sense<S>> subscribers = new ArrayList<>();
    public abstract void update(Dunjeon d);
    public abstract List<Data> generateDataForParams(S params);

    public void addSubscriber(Sense<S> sense){
        subscribers.add(sense);
    }

    public void removeSubscriber(Sense<S> sense){
        subscribers.remove(sense);
    }

    public List<Sense<S>> getSubscribers() {
        return subscribers;
    }

    public abstract List<Event> retrieveEventsForParams(S params);


}
