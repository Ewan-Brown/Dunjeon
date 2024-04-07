package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastream;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.data.SensorListener;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.meworking.data.server.event.GenericSoundEvent;
import lombok.AllArgsConstructor;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HearingDataStream extends Datastream<HearingDataStream.HearingDataStreamParameters> {

    private List<GenericSoundEvent> amalgamatedSoundEvents = new ArrayList<>();

    public void appendSoundEvent(GenericSoundEvent e){
        amalgamatedSoundEvents.add(e);
    }

    @Override
    public void update(Dunjeon d) {
        System.out.println("HearingDataStream.update, with : " + amalgamatedSoundEvents.size() + " sounds to process!");
        for (Sensor<HearingDataStreamParameters> subscriber : getSubscribers()) {
            HearingDataStreamParameters params = subscriber.getParameters();
            subscriber.passOnEvents(amalgamatedSoundEvents);
        }
        amalgamatedSoundEvents.clear();
    }

    @AllArgsConstructor
    public static class HearingDataStreamParameters extends DataStreamParameters{
        private final Vector2 hearingSourceLocation;
        private final long sensorHostUUID;
    }
}
