package com.ewan.dunjeon.data;

import com.ewan.meworking.data.server.data.CellPosition;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.Pair;
import com.ewan.dunjeon.server.world.WorldUtils;
import com.ewan.dunjeon.server.world.cells.BasicCell;
import com.ewan.dunjeon.server.world.entities.Entity;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.data.DataWrappers;
import com.ewan.meworking.data.server.data.Datas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.util.*;

public class Datastreams {

    static Logger logger = LogManager.getLogger();

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        /**
         * The desired arc length of the 'wedges' created between each of the rays
         */
        static final double DESIRED_ARCLENGTH = 0.1;

        //Worry about performance LATER we can think about caching or something
        @Override
        public void update(Dunjeon d) {
            for (int i = 0; i < getSubscribers().size(); i++) {
                Sensor<SightStreamParameters> sensor = getSubscribers().get(i);


                //Get necessary parameters from sensor
                SightStreamParameters params = sensor.getParameters();
                HashMap<Vector2, Set<WorldUtils.Side>> tilesMap = new HashMap<>();
                Vector2 sensorPos = params.getSightSourceLocation();

                List<DataWrapper<? extends Data, ?>> dataAmalgamated = new ArrayList<>();

                if(params.getTrueSight()){

                    for (BasicCell basicCell : sensor.creature.getFloor().getCellsAsList()) {
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        dataAmalgamated.add(DataWrappers.wrapCellData(List.of(cellData), new CellPosition(basicCell.getWorldCenter(), basicCell.getFloor().getUUID()), d.getTimeElapsed(), d.getTicksElapsed()));
                    }

                    for (Entity entity : sensor.creature.getFloor().getEntities()){

                        Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getRotationAngle(), entity.getAngularVelocity());
                        Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                        Long entityId = entity.getUUID();
                        dataAmalgamated.add(DataWrappers.wrapEntityData(List.of(kineticData, positionalData), entityId, d.getTimeElapsed(), d.getTicksElapsed()));
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

                        for (WorldUtils.IntersectionData result : results) {
                            if (!tilesMap.containsKey(result.getCellCoordinate())) {
                                tilesMap.put(result.getCellCoordinate(), new HashSet<>());
                            }
                            tilesMap.get(result.getCellCoordinate()).add(result.getSide());
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(result.getCellCoordinate());
                            if (basicCell == null || !basicCell.canBeSeenThrough(sensor.creature)){
                                // Figure out the _minimum_ angle increase required to push past this cell

                                // 1. Define the wall/side that has been collided with
                                // 2. Get the corners of that wall
                                // 2.5 transform the angles to match - as the ray angle and the angle to the points will be on different domains
                                // 3. Compare the current angle + rotation direction (should always be the same?) to figure out which of the two is next in the raytracer's path
                                // 4. Take angle to point form #3 and add a tiny bit to it


                                //1 and 2
                                Pair<Vector2, Vector2> endPoints = result.getAdjacentSideEndPoints();
                                if(endPoints != null) {

                                    double theta1 = Math.atan2(endPoints.getElement0().x, endPoints.getElement0().y);
                                    double theta2 = Math.atan2(endPoints.getElement1().x, endPoints.getElement1().y);
                                    double phi = currentAngle;

                                    //2.5
                                    if (phi > Math.PI) phi -= Math.PI * 2;

                                    //calculate difference for both angles from current ray
                                    double theta1diff = theta1 - phi;
                                    double theta2diff = theta2 - phi;

                                    //Correct for case where |theta_diff| > PI to make further comparison easier
                                    if (Math.abs(theta1diff) > Math.PI) {
                                        theta1diff = theta1diff - Math.PI * 2 * Math.signum(theta1diff);
                                    }

                                    if (Math.abs(theta2diff) > Math.PI) {
                                        theta2diff = theta2diff - Math.PI * 2 * Math.signum(theta2diff);
                                    }

                                    if (theta1diff > theta2diff) {

                                    } else if (theta2diff > theta1diff) {

                                    } else {
                                        logger.error("theta1 and theta2 are equal. This should NEVER occur, and signifies that I might be bad at trig");
                                        throw new RuntimeException("theta1 == theta2, should never occur");
                                    }

                                }
                                break;
                            }
                        }

                        currentAngle += angleSpacing;

                    } while (!finalLap);

                    for (Map.Entry<Vector2, Set<WorldUtils.Side>> tile : tilesMap.entrySet()) {
                        BasicCell basicCell = sensor.creature.getFloor().getCellAt(tile.getKey());
                        if (basicCell == null) continue;
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        dataAmalgamated.add(DataWrappers.wrapCellData(List.of(cellData), new CellPosition(basicCell.getWorldCenter(), basicCell.getFloor().getUUID()), d.getTimeElapsed(), d.getTicksElapsed()));
                    }

                    Set<Entity> entitiesOnSameFloor = sensor.creature.getFloor().getEntities();
                    for (Entity entity : entitiesOnSameFloor) {

                        Vector2 entityPos = entity.getWorldCenter();
                        var results = WorldUtils.getIntersectedTilesWithWall(sensorPos, entityPos);

                        for (WorldUtils.IntersectionData result : results) {
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(result.getCellCoordinate());
                            if(basicCell != null && !basicCell.canBeSeenThrough(sensor.creature))
                                break;
                        }

                        Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getRotationAngle(), entity.getAngularVelocity());
                        Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                        Long entityId = entity.getUUID();
                        dataAmalgamated.add(DataWrappers.wrapEntityData(List.of(kineticData, positionalData), entityId, d.getTimeElapsed(), d.getTicksElapsed()));
                    }

                }

                sensor.passOnData(dataAmalgamated);

            }
        }

        @Override
        public Sensor<SightStreamParameters> constructSensorForDatastream(Creature c, Sensor.ParameterCalculator<SightStreamParameters> pCalc) {
            return new Sensor<>(c, this, pCalc);
        }

        @AllArgsConstructor
        @Getter
        public static class SightStreamParameters extends DataStreamParameters {
            private final double sightRange; //Radius of the circle of range of vision
            private final double sightFieldOfView; //Arc length of the full width of view
            private final double currentSightAngle; //What is the absolute current angle of the viewer

            private final Vector2 sightSourceLocation; /// Where the eyeball at
            private final Boolean trueSight; //Magic sight that lets you see everything! (hopefully just for debugging)
        }
    }
}
