package com.ewan.dunjeon.world.data;

import com.ewan.dunjeon.world.Dunjeon;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Datastreams {

    public static class SightDataStream extends Datastream<Datum.EntityVisualData, SightDataStream.SightStreamParameters>{

        @Override
        public void update(Dunjeon d) {

        }

        @Override
        public Datum.EntityVisualData generateDataForParams(SightStreamParameters params) {
            return null;
        }

        @AllArgsConstructor
        @Getter
        public static class SightStreamParameters extends DataStreamParameters{
            final double sightRange;
            final double sightFieldOfView;
        }
    }
}
