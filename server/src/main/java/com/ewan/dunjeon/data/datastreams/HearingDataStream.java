package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastream;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.meworking.data.server.event.HeardSoundEvent;
import lombok.AllArgsConstructor;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class HearingDataStream extends Datastream<HearingDataStream.HearingDataStreamParameters> {

    private List<SoundRequest> amalgamatedSoundEvents = new ArrayList<>();

    public void appendSoundEvent(SoundRequest e){
        amalgamatedSoundEvents.add(e);
    }

    @Override
    public void update(Dunjeon d) {
        System.out.println("HearingDataStream.update, with : " + amalgamatedSoundEvents.size() + " sounds to process!");
        for (Sensor<HearingDataStreamParameters> subscriber : getSubscribers()) {

            List<HeardSoundEvent> events = new ArrayList<>();
            for (SoundRequest sound : amalgamatedSoundEvents) {
                HeardSoundEvent event = new HeardSoundEvent(sound.category, sound.sourceLocation, 1.0f, sound.sourceUUID == subscriber.getParameters().sensorHostUUID, d.getTimestamp());
                events.add(event);
            }
            //Transform each of the existing soundrequests to heardSoundEvents for this subscriber
            subscriber.passOnEvents(events);
        }
        amalgamatedSoundEvents.clear();
    }

    @AllArgsConstructor
    public static class HearingDataStreamParameters extends DataStreamParameters{
        private final Vector2 hearingSourceLocation;
        private final long sensorHostUUID;
    }

    @AllArgsConstructor
    public static class SoundRequest{
        private final Vector2 sourceLocation;
        private final float intensity;
        private final long sourceUUID;
        private final HeardSoundEvent.SoundSourceCategory category;
    }
}
