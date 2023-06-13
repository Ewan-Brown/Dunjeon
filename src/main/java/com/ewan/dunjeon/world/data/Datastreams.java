package com.ewan.dunjeon.world.data;

import com.ewan.dunjeon.world.Dunjeon;

public class Datastreams {

    public static class SightDataStream extends Datastream<Datum.EntitySightData, SightDataStream.SightStreamParameters>{

        @Override
        public void update(Dunjeon d) {

        }

        @Override
        public Datum.EntitySightData generateDataForParams(SightStreamParameters params) {
            return null;
        }

        public static class SightStreamParameters extends DataStreamParameters{

        }
    }
}
