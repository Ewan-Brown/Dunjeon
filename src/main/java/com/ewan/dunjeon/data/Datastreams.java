package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Datastreams {

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        //Worry about performance LATER we can think about caching or something
        @Override
        public void update(Dunjeon d) {
            for (Sensor<SightStreamParameters> sensor : getSubscribers()) {

                //Get necessary parameters from sensor
                SightStreamParameters params = sensor.getParameters();

                //************* Entity Data *********************//

                List<DataWrappers.EntityDataWrapper> entityDataWrappers = new ArrayList<>();

                Set<Entity> entitiesOnSameFloor = sensor.creature.getFloor().getEntities();
                for (Entity entity : entitiesOnSameFloor) {

                    Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getTransform().getRotationAngle(), entity.getAngularVelocity());
                    Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                    Long entityId = entity.getUUID();

                    List<Datas.EntityData> entityDataAmalgamated = List.of(kineticData, positionalData);
                    DataWrappers.EntityDataWrapper entityDataWrapper = new DataWrappers.EntityDataWrapper(entityDataAmalgamated, entityId, sensor, d.getTimeElapsed());
                    entityDataWrappers.add(entityDataWrapper);

                }


                //************* Cell Data *********************//


                List<DataWrappers.CellDataWrapper> cellDataAmalgamated = new ArrayList<>();
                for (BasicCell basicCell : sensor.creature.getFloor().getCellsAsList()) {
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        DataWrappers.CellDataWrapper cellDataWrapper = new DataWrappers.CellDataWrapper(List.of(cellData), new WorldUtils.CellPosition(basicCell.getWorldCenter(), sensor.creature.getFloor().getUUID()), sensor, d.getTimeElapsed());
                        cellDataAmalgamated.add(cellDataWrapper);
                }


                //************* PUSH *********************//

                sensor.passOnData(entityDataWrappers);
                sensor.passOnData(cellDataAmalgamated);

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
