package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.level.Floor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Datastreams {

    public static class SightDataStream extends Datastream<Datum.EntityVisualData, SightDataStream.SightStreamParameters> {

        @Override
        public void update(Dunjeon d) {

            for (Sense<Datum.EntityVisualData, SightStreamParameters> subscriber : getSubscribers()) {
                
            }
        }

        @Override
        public Datum.EntityVisualData generateDataForParams(SightStreamParameters params) {
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
            final double sourceX;
            final double sourceY;
        }
    }
}
