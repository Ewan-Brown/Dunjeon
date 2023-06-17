package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Datastreams {

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        @Override
        public void update(Dunjeon d) {
            for (Sense<SightStreamParameters> subscriber : getSubscribers()) {
                
            }
        }

        @Override
        public List<Data> generateDataForParams(SightStreamParameters params) {
            return null;
        }

        @Override
        public List<Event> retrieveEventsForParams(SightStreamParameters params) {
            return new ArrayList<>();
        }

        @AllArgsConstructor
        @Getter
        public static class SightStreamParameters extends DataStreamParameters {
            final double sightRange;
            final double sightFieldOfView;
            final Vector2 sightSourceLocation;
        }
    }
}
