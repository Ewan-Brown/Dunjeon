package com.ewan.dunjeon.data;

import com.ewan.meworking.data.server.data.CellPosition;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.WorldUtils;
import com.ewan.dunjeon.server.world.cells.BasicCell;
import com.ewan.dunjeon.server.world.entities.Entity;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.data.DataWrappers;
import com.ewan.meworking.data.server.data.Datas;
import com.ewan.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.ConfigurationKeys;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.dyn4j.geometry.Vector2;

import javax.swing.plaf.OptionPaneUI;
import javax.swing.text.html.Option;
import java.util.*;
import java.util.List;

import static com.ewan.dunjeon.server.world.WorldUtils.*;

public class Datastreams {

    static Logger logger = LogManager.getLogger();

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        public static boolean do_debug = false;

        @Override
        public void update(Dunjeon d) {
            Level l = logger.getLevel();
            if(do_debug){
                Configurator.setLevel(logger, Level.TRACE);
            }

            for (int i = 0; i < getSubscribers().size(); i++) {
                Sensor<SightStreamParameters> sensor = getSubscribers().get(i);

                //Get necessary parameters from sensor
                SightStreamParameters params = sensor.getParameters();
                HashMap<Vector2, Set<WorldUtils.Side>> tileVisibilityMap = new HashMap<>();
                Vector2 sensorPos = params.getSightSourceLocation();

                List<DataWrapper<? extends Data, ?>> dataAmalgamated = new ArrayList<>();

                if(params.getTrueSight()){
                    logger.trace("truesight is enabled");

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
                    if (logger.isTraceEnabled())
                        logger.trace("=====Processing Raycasting=====");

                    //************* Do Raycasting *******************//
                    double range = params.getSightRange();
                    double rangeSquared = range*range;
                    double fov = params.getSightFieldOfView();
                    double currentEntityAngle = params.getCurrentSightAngle();

                    double startingAngle = currentEntityAngle - fov / 2;
                    double endingAngle = currentEntityAngle + fov / 2;
                    final double tinyAngle = 0.00001d;
                    final double minRelativeAngle = 0; //Used to cut off some floating point errors... May need reinspection
                    int rayCounter = 0;

                    logger.trace("pos: " + params.sightSourceLocation + " fov: " + fov + ", range: " + range + ", starting angle: " + startingAngle + " endingAngle: " + endingAngle);

                    tileVisibilityMap.put(new Vector2( Math.floor(sensorPos.x), Math.floor(sensorPos.y)), Set.of(Side.values()));


                    //Iterate across rays. Written to ensure that the first and last angles are casted, to avoid any funny business
                    double currentAngle = startingAngle;
                    boolean finalLap = false;

                    Optional<Vector2> previousEndingTileCoords = Optional.empty();

                    //Updated every step, referring to the corners of every gridline segment that the ray intersects, used on the next ray if it turns out we jumped too far
                    Set<Double> cachedStepAnglesFromLastRay = new HashSet<>();
                    Optional<Vector2> worstCaseLastTileCoord = Optional.empty();
                    boolean doingWorstCase = false;
                    //Ray loop
                    rayLoop:
                    do {

                        logger.trace("======================== ");

                        if(doingWorstCase){
                            if(cachedStepAnglesFromLastRay.isEmpty()){
//                                throw new RuntimeException("Attempting to do worst case but no cached steps!");
                                doingWorstCase = false;
                                logger.trace("ran out of worst case angles, reverting back to normal");
                            }else {
                                currentAngle = cachedStepAnglesFromLastRay.stream().min(Double::compareTo).get();
                                cachedStepAnglesFromLastRay.remove(currentAngle);
                                logger.trace("continuing with worst case, setting angle to : " + currentAngle + ", with " + cachedStepAnglesFromLastRay.size() + " angles left to try");
                            }
                            }

                        if (currentAngle > endingAngle) {
                            if(logger.isTraceEnabled())
                                logger.trace("Last ray in this loop, trimming angle from : " + currentAngle + ", to: " + endingAngle);
                            finalLap = true;
                            currentAngle = endingAngle;
                        }

                        Vector2 currentPoint = sensorPos;

                        if(logger.isTraceEnabled())
                            logger.trace(String.format("Ray # : %d, angle : %.6f", rayCounter, currentAngle));

                        Vector2 rayEnd = sensorPos.copy().add(Vector2.create(range, currentAngle));

                        //IntersectionData used to determine if we can just 'skip' to the corner of this tile
                        // - Is there 'space' in between these blocks relative to the line of sight?
                        // Empty if no collision occurred
                        Optional<IntersectionData> collidingIntersection = Optional.empty();

                        Set<Double> potentialWorstCaseAngles = new HashSet<>();

                        //Marching loop
                        while (true) {

                            logger.trace("------------------------");

                            Optional<IntersectionData> intersectionDataOpt = WorldUtils.getNextGridIntersect(currentPoint, rayEnd);

                            if(intersectionDataOpt.isEmpty()){
                                if(logger.isTraceEnabled())
                                    logger.trace("no intersections returned");
                                break;
                            }
                            IntersectionData intersectionData = intersectionDataOpt.get();
                            // INSPECTING AN INTERSECTION
                            if(logger.isTraceEnabled())
                                logger.trace("inspecting intersection: " + intersectionData);

                            if (!tileVisibilityMap.containsKey(intersectionData.getCellCoordinate())) {
                                tileVisibilityMap.put(intersectionData.getCellCoordinate(), new HashSet<>());
                            }

                            tileVisibilityMap.get(intersectionData.getCellCoordinate()).add(intersectionData.getSide());

                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(intersectionData.getCellCoordinate());
                            boolean isBlocking = !basicCell.canBeSeenThroughBy(sensor.creature);

                            if (isBlocking) {
                                collidingIntersection = Optional.of(intersectionData);
                                if(logger.isTraceEnabled())
                                    logger.trace("Ray collided with cell: " + StringUtils.formatVector(intersectionData.getCellCoordinate()) + ", at " + StringUtils.formatVectorFullPrecision(intersectionData.getIntersectionPoint()));
                                break;
                            }else{
                                logger.trace("gathering angles for potential worst case...");
                                Optional<Double> currentChosenAngle = Optional.empty();
                                for (Corner corner : intersectionData.getSide().getCorners()) {
                                    Vector2 potentialEndPoint = corner.getLocalCoord().sum(intersectionData.getCellCoordinate());
                                    Vector2 vectorToEndpoint = potentialEndPoint.difference(sensorPos);
//                                    logger.trace("potentialEndpoint: " + potentialEndPoint);
//                                    logger.trace("vectorToEndpoint: " + vectorToEndpoint);
                                    double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                    double relativeAngle = getAngleDiffInAtan2Domain(angle, currentAngle);
                                    logger.trace("angle: " + angle);
//                                    logger.trace("relativeAngle: " + relativeAngle);
                                    if((currentChosenAngle.isEmpty() || relativeAngle < currentChosenAngle.get()) && relativeAngle >= minRelativeAngle){
                                        logger.trace("taken : " + relativeAngle);
                                        currentChosenAngle = Optional.of(relativeAngle);
                                    }
                                }
                                if(currentChosenAngle.isEmpty()){
                                    throw new RuntimeException("None of the corners had valid angles? Not possible");
                                }else{
                                    potentialWorstCaseAngles.add(currentChosenAngle.get() + currentAngle + tinyAngle);
                                    logger.trace("added another angle, size is now: " + potentialWorstCaseAngles.size());
                                }

                            }
                            currentPoint = intersectionData.getIntersectionPoint();

                        }

                        if(logger.isTraceEnabled())
                            logger.trace("Calculating next ray angle");

                        double nextRayAngleIncrement;

                        //Calculations to to make a change to currentAngle

                        // possible cases:
                        // - Best case: We collided with a cell directly adjacent to previous collision
                        // - Ok case: We didn't hit anything and can skip to the end of the cell
                        // - Worst case We collided with a cell that was NOT directly adjacent to the previous collision and need to _go back_

                        boolean didCollide = collidingIntersection.isPresent();
                        boolean didCollideAdjacentToPrevious = false;
                        boolean forceBestCase = false;

                        logger.trace("didCollide: " + didCollide);

                        if(rayCounter == 0 && didCollide){
                            forceBestCase = true;
                        }

                        if(doingWorstCase){
                            logger.trace("🚨 working on worst case");
                        }

                        if(didCollide){
                            if(doingWorstCase){
                                Vector2 collidingCellCoord = collidingIntersection.get().getCellCoordinate();
                                Vector2 diff = collidingCellCoord.difference(worstCaseLastTileCoord.get());
                                if(diff.getMagnitude() > 0 ){
                                    //Ok we've moved far enough to skip past the last 'ok' cell
                                    doingWorstCase = false;
                                    forceBestCase = true;
                                    logger.trace("Ending worst case, we've collided with a cell close enough. Forcing best case.");
                                }
                            }
                            if(previousEndingTileCoords.isPresent()) {
                                Vector2 vectorToCurrentTile = collidingIntersection.get().getCellCoordinate();
                                Vector2 vectorToPreviousCollidingTile = previousEndingTileCoords.get();
                                if(vectorToCurrentTile.getMagnitudeSquared() > vectorToPreviousCollidingTile.getMagnitudeSquared()){
                                    forceBestCase = true;
                                }
                            }
                        }

                        if(!doingWorstCase || forceBestCase){
                            if (!forceBestCase && didCollide) {
                                if (previousEndingTileCoords.isPresent()) {
                                    Vector2 diff = collidingIntersection.get().getCellCoordinate().difference(previousEndingTileCoords.get());
                                    if (diff.getMagnitudeSquared() <= 2.00001) {
                                        didCollideAdjacentToPrevious = true;
                                        logger.trace("Identified that we we have collided adjacent to pervious");
                                    }
                                }
                            }

                            if (forceBestCase || didCollideAdjacentToPrevious) {
                                if (logger.isTraceEnabled())
                                    logger.trace("Best case scenario: " + ((didCollideAdjacentToPrevious) ? "did collide adjacent" : "forced"));

                                IntersectionData intersection = collidingIntersection.get();
                                Optional<Double> currentChosenAngle = Optional.empty();
                                for (Corner corner : intersection.getSide().getCorners()) {
                                    Vector2 potentialEndPoint = corner.getLocalCoord().sum(intersection.getCellCoordinate());
                                    Vector2 vectorToEndpoint = potentialEndPoint.copy().subtract(sensorPos);
                                    double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                    double relativeAngle = getAngleDiffInAtan2Domain(angle, currentAngle);
                                    if ((currentChosenAngle.isEmpty() || relativeAngle < currentChosenAngle.get()) && relativeAngle >= minRelativeAngle) {
                                        if (logger.isTraceEnabled())
                                            logger.trace(" taking endpoint: " + StringUtils.formatVector(potentialEndPoint) + ", relativeAngle: " + relativeAngle);
                                        currentChosenAngle = Optional.of(relativeAngle);
                                    } else {
                                        if (logger.isTraceEnabled())
                                            logger.trace(" ignoring endpoint: " + StringUtils.formatVectorFullPrecision(potentialEndPoint) + ", relativeAngle: " + relativeAngle);
                                    }
                                }
                                if (currentChosenAngle.isEmpty()) {
                                    throw new RuntimeException("None of the endpoint angles were valid! This should be impossible");
                                }
                                nextRayAngleIncrement = currentChosenAngle.get();

                            } else if (!didCollide) {
                                if (logger.isTraceEnabled())
                                    logger.trace("Best case scenario, didn't collide");
//                             OPTIMIZATION opportunity
//                             If we are here then the ray has ended and we have NOT collided
                                List<Vector2> potentialEndPoints = new ArrayList<>();
                                Optional<Double> furthestAngle = Optional.empty();
                                for (Side side : Side.values()) {
                                    List<Vector2> points = WorldUtils.getIntersectsBetweenCircleAndTileSide(sensorPos, rangeSquared, new Vector2(Math.floor(rayEnd.x), Math.floor(rayEnd.y)), side);
                                    if (logger.isTraceEnabled())
                                        logger.trace("for side " + side + ", " + points.size() + " intersects found");
                                    potentialEndPoints.addAll(points);
                                }
                                for (Vector2 potentialEndPoint : potentialEndPoints) {

                                    Vector2 vectorToEndpoint = potentialEndPoint.copy().subtract(sensorPos);
                                    double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                    double relativeAngle = getAngleDiffInAtan2Domain(angle, currentAngle);
                                    if (logger.isTraceEnabled()) {
                                        logger.trace("looking at endpoint: " + StringUtils.formatVectorFullPrecision(potentialEndPoint));
                                        logger.trace("angle: " + angle + ", relative angle: " + relativeAngle);
                                    }
                                    if ((furthestAngle.isEmpty() || furthestAngle.get() < relativeAngle) && relativeAngle > minRelativeAngle) {
                                        if (logger.isTraceEnabled())
                                            logger.trace("taking this endpoint!");
                                        furthestAngle = Optional.of(relativeAngle);
                                    }
                                }
                                if (furthestAngle.isPresent()) {
                                    nextRayAngleIncrement = furthestAngle.get();
                                } else {
                                    throw new RuntimeException("None of the endpoints were valid!");
                                }
                            } else {
                                if (logger.isTraceEnabled())
                                    logger.trace("We might worst case scenario, did collide but not adjacent");

                                doingWorstCase = true;
                                worstCaseLastTileCoord = previousEndingTileCoords;
                                rayCounter++;
                                continue rayLoop;
                            }

                            cachedStepAnglesFromLastRay = potentialWorstCaseAngles;
                            logger.trace("size of cachedAngles: " + cachedStepAnglesFromLastRay.size());

                            if (didCollide) {
                                previousEndingTileCoords = Optional.of(collidingIntersection.get().getCellCoordinate());
                            } else {
                                previousEndingTileCoords = Optional.of(new Vector2(Math.floor(rayEnd.x), Math.floor(rayEnd.y)));
                            }

                            currentAngle += nextRayAngleIncrement + tinyAngle;

                            rayCounter++;
                        }
                    } while (!finalLap);
                    if(logger.isDebugEnabled())
                        logger.debug("# of rays: " + rayCounter);

                    // Do tile sight algorithm
                    for (Map.Entry<Vector2, Set<WorldUtils.Side>> tile : tileVisibilityMap.entrySet()) {
                        BasicCell basicCell = sensor.creature.getFloor().getCellAt(tile.getKey());
                        if (basicCell == null) continue;
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        dataAmalgamated.add(DataWrappers.wrapCellData(List.of(cellData), new CellPosition(basicCell.getWorldCenter(), basicCell.getFloor().getUUID()), d.getTimeElapsed(), d.getTicksElapsed()));
                    }

                    int entity_count = 0;
                    //Do entity sight algorithm
                    for (Entity entity : sensor.creature.getFloor().getEntities()) {

                        Vector2 entityPos = entity.getWorldCenter();
                        Vector2 tilePos = new Vector2(Math.floor(entityPos.x), Math.floor(entityPos.y));

                        if(entity != sensor.creature) {
                            if(!tileVisibilityMap.containsKey(tilePos)){
                                continue;
                            }
                        }

                        entity_count++;

                        Datas.EntityKineticData kineticData = new Datas.EntityKineticData(entity.getLinearVelocity(), entity.getRotationAngle(), entity.getAngularVelocity());
                        Datas.EntityPositionalData positionalData = new Datas.EntityPositionalData((entity.getWorldCenter()), entity.getUUID());
                        Long entityId = entity.getUUID();
                        dataAmalgamated.add(DataWrappers.wrapEntityData(List.of(kineticData, positionalData), entityId, d.getTimeElapsed(), d.getTicksElapsed()));
                    }
                    logger.trace(entity_count + " entities visible!");

                }
                sensor.passOnData(dataAmalgamated);
            }
            if(do_debug){
                Configurator.setLevel(logger, l);
                do_debug = false;
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
            private final Boolean trueSight; //Magic sight that lets you see everything! (really just for debugging)
        }
    }
}
