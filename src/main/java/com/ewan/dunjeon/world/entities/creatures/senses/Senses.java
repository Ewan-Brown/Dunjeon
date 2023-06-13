package com.ewan.dunjeon.world.entities.creatures.senses;

import com.ewan.dunjeon.world.data.Datastream;
import com.ewan.dunjeon.world.data.Datastreams;
import com.ewan.dunjeon.world.data.Datum;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;

public class Senses {
    public class EntitySightSense extends Sense<Datum.EntitySightData, Datastreams.SightDataStream.SightStreamParameters> {
        public EntitySightSense(Creature c, Datastream<Datum.EntitySightData, Datastreams.SightDataStream.SightStreamParameters> d) {
            super(c, d);
        }

        @Override
        public void updateCreature(Datum.EntitySightData data) {

        }

        @Override
        public Datastreams.SightDataStream.SightStreamParameters calculateParameters() {
            return null;
        }
    }

}
