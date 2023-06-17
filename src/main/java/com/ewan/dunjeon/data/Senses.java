package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.Creature;

public class Senses {
    public static abstract class EntitySightSense extends Sense<Datum.EntityVisualData, Datastreams.SightDataStream.SightStreamParameters> {
        public EntitySightSense(Creature c, Datastream<Datum.EntityVisualData, Datastreams.SightDataStream.SightStreamParameters> d) {
            super(c, d);
        }

        @Override
        public abstract Datastreams.SightDataStream.SightStreamParameters calculateDatastreamParameters();

    }

    public static abstract class CellSightSense extends Sense<Datum.CellVisualData, Datastreams.SightDataStream.SightStreamParameters> {
        public CellSightSense(Creature c, Datastream<Datum.CellVisualData, Datastreams.SightDataStream.SightStreamParameters> d) {
            super(c, d);
        }

        @Override
        public abstract Datastreams.SightDataStream.SightStreamParameters calculateDatastreamParameters();

    }

}
