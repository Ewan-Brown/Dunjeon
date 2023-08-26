package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.*;
import java.util.function.Predicate;

public class Datastreams {

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        /**
         * The desired arc length of the 'wedges' created between each of the rays
         */
        static final double DESIRED_ARCLENGTH = 0.1;

        //Worry about performance LATER we can think about caching or something
        @Override
        public void update(Dunjeon d) {
            for (Sensor<SightStreamParameters> sensor : getSubscribers()) {


                //Get necessary parameters from sensor
                SightStreamParameters params = sensor.getParameters();
                HashMap<Vector2, Set<WorldUtils.Side>> tilesMap = new HashMap<>();
                Vector2 sensorPos = params.getSightSourceLocation();

                List<DataWrappers.CellDataWrapper> cellDataAmalgamated = new ArrayList<>();
                List<DataWrappers.EntityDataWrapper> entityDataAmalgamated = new ArrayList<>();

                if(params.getTrueSight()){

                    for (BasicCell basicCell : sensor.creature.getFloor().getCellsAsList()) {
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        DataWrappers.CellDataWrapper wrapper = new DataWrappers.CellDataWrapper(List.of(cellData), new WorldUtils.CellPosition(basicCell), sensor, d.getTimeElapsed());
                        cellDataAmalgamated.add(wrapper);
                    }

                    for (Entity entity : sensor.creature.getFloor().getEntities()){

                        Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getRotationAngle(), entity.getAngularVelocity());
                        Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                        Long entityId = entity.getUUID();

                        DataWrappers.EntityDataWrapper entityDataWrapper = new DataWrappers.EntityDataWrapper(List.of(kineticData, positionalData), entityId, sensor, d.getTimeElapsed());
                        entityDataAmalgamated.add(entityDataWrapper);
                    }

                }else {

                    //************* Do Raycasting *******************//
                    double range = params.getSightRange();
                    double fov = params.getSightFieldOfView();
                    double currentEntityAngle = params.getCurrentSightAngle();

                    double startingAngle = currentEntityAngle - fov / 2;
                    double endingAngle = currentEntityAngle + fov / 2;
                    double angleSpacing = DESIRED_ARCLENGTH / fov;

                    //Iterate across rays. Written to ensure that the first and last angles are casted, to avoid any funny business
                    double currentAngle = startingAngle;
                    boolean finalLap = false;
                    do {
                        if (currentAngle > endingAngle) {
                            finalLap = true;
                            currentAngle = endingAngle;
                        }

                        Vector2 rayEnd = sensorPos.copy().add(Vector2.create(range, currentAngle));
                        var results = WorldUtils.getIntersectedTilesWithWall(sensorPos, rayEnd);

                        for (Pair<Vector2, WorldUtils.Side> result : results) {
                            if (!tilesMap.containsKey(result.getElement0())) {
                                tilesMap.put(result.getElement0(), new HashSet<>());
                            }
                            tilesMap.get(result.getElement0()).add(result.getElement1());
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(result.getElement0());
                            if (basicCell == null || !basicCell.canBeSeenThrough(sensor.creature))
                                break;
                        }

                        currentAngle += angleSpacing;

                    } while (!finalLap);

                    for (Map.Entry<Vector2, Set<WorldUtils.Side>> tile : tilesMap.entrySet()) {
                        BasicCell basicCell = sensor.creature.getFloor().getCellAt(tile.getKey());
                        if (basicCell == null) continue;
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        DataWrappers.CellDataWrapper cellDataWrapper = new DataWrappers.CellDataWrapper(List.of(cellData), new WorldUtils.CellPosition(basicCell.getWorldCenter(), sensor.creature.getFloor().getUUID()), sensor, d.getTimeElapsed());
                        cellDataAmalgamated.add(cellDataWrapper);

                    }

                    Set<Entity> entitiesOnSameFloor = sensor.creature.getFloor().getEntities();
                    for (Entity entity : entitiesOnSameFloor) {

                        Vector2 entityPos = entity.getWorldCenter();
                        var results = WorldUtils.getIntersectedTilesWithWall(sensorPos, entityPos);

                        for (Pair<Vector2, WorldUtils.Side> result : results) {
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(result.getElement0());
                            if(basicCell != null && !basicCell.canBeSeenThrough(sensor.creature))
                                break;
                        }

                        Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getRotationAngle(), entity.getAngularVelocity());
                        Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                        Long entityId = entity.getUUID();

                        DataWrappers.EntityDataWrapper entityDataWrapper = new DataWrappers.EntityDataWrapper(List.of(kineticData, positionalData), entityId, sensor, d.getTimeElapsed());
                        entityDataAmalgamated.add(entityDataWrapper);
                    }

                }


                sensor.passOnData(entityDataAmalgamated);
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
            /**
             * Radius of the circle of range of vision
             */
            private final double sightRange;
            /**
             * Arc length of the full width of view
             */
            private final double sightFieldOfView;
            private final double currentSightAngle;
            /**
             * Where his eyeball at
             */
            private final Vector2 sightSourceLocation;
            /**
             * Magic sight that lets you see everything! (Debugging)
             */
            private final Boolean trueSight;
        }
    }
}
