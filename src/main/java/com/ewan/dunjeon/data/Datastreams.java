package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Datastreams {

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        //Worry about performance LATER we can think about caching or something
        @Override
        public void update(Dunjeon d) {
            System.out.println("updating datastream: " + getClass());
            for (Sensor<SightStreamParameters> subscriber : getSubscribers()) {

                //Get necessary parameters from subscriber
                SightStreamParameters params = subscriber.getParameters();

                //************* Entity Data *********************//

                //Pretend that data calculation actually occurs here
                Datas.EntityKineticData kineticData = new Datas.EntityKineticData(d.getTime(), new Vector2(), 0, 0);
                Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData(d.getTime(), subscriber.creature.getWorldCenter().add(-5,-5), subscriber.creature.getUUID());
                Long entityId = 2L;

                //All data about a given entity grouped together
                List<Datas.EntityData> entityDataAmalgamated = List.of(kineticData, positionalData);

                //Now give it context, with the entity ID and a link to this sensor, and wrap it together nicely
                DataWrappers.EntityDataWrapper entityDataWrapper = new DataWrappers.EntityDataWrapper(entityDataAmalgamated, entityId, subscriber);

                //************* Cell Data *********************//


                List<DataWrappers.CellDataWrapper> cellDataAmalgamated = new ArrayList<>();
                for (BasicCell basicCell : subscriber.creature.getFloor().getCellsAsList()) {
                        Datas.CellData cellData = (new Datas.CellEnterableData(d.getTime(), basicCell.canBeEntered(subscriber.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        DataWrappers.CellDataWrapper cellDataWrapper = new DataWrappers.CellDataWrapper(List.of(cellData), new WorldUtils.CellPosition(basicCell.getX(), basicCell.getY(), subscriber.creature.getFloor().getUUID()), subscriber);
                        cellDataAmalgamated.add(cellDataWrapper);
                }


                //************* PUSH *********************//

                subscriber.passOnData(List.of(entityDataWrapper));
                subscriber.passOnData(cellDataAmalgamated);

            }
        }

        @Override
        public Sensor<SightStreamParameters> constructSensorForDatastream(Creature c, Sensor.ParameterCalculator<SightStreamParameters> pCalc) {
            return new Sensor<>(c, this, pCalc);
        }

        @AllArgsConstructor
        @Getter
        public static class SightStreamParameters extends DataStreamParameters {
            final double sightRange;
            final double sightFieldOfView;
            /**
             * Where his eyeball at
             */
            final Vector2 sightSourceLocation;
        }
    }
}
