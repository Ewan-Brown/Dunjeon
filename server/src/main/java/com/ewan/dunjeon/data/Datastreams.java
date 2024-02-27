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
import com.ewan.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Datastreams {

    static Logger logger = LogManager.getLogger();

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        /**
         * The desired arc length of the 'wedges' created between each of the rays
         */
        static final double DESIRED_ARCLENGTH = 0.01;

        static List<Line2D.Double> cached_raytracing_lines = new ArrayList<>();
        static List<Point2D> cached_filled_walls = new ArrayList<>();
        static List<Point2D> cached_known_filled_walls = new ArrayList<>();

        public static void PRINT_DEBUG_IMAGE(){
            logger.warn("PRINTING DEBUG IMAGE!");
            BufferedImage b = new BufferedImage(10000,10000, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2 = b.createGraphics();
            int SCALE = 400;
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 10000, 10000);

            g2.setColor(Color.BLACK);
            for (Point2D cachedFilledWall : cached_filled_walls) {
                g2.fillRect((int)((cachedFilledWall.getX()) * SCALE) - 1, (int)((cachedFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }
            g2.setColor(Color.GREEN);
            for (Point2D cachedKnownFilledWall : cached_known_filled_walls) {
                g2.fillRect((int)((cachedKnownFilledWall.getX()) * SCALE) - 1, (int)((cachedKnownFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }
            g2.setColor(Color.BLACK);
            for (Point2D cachedFilledWall : cached_filled_walls) {
                g2.drawRect((int)((cachedFilledWall.getX()) * SCALE), (int)((cachedFilledWall.getY()) * SCALE), SCALE, SCALE);
                g2.drawRect((int)((cachedFilledWall.getX()) * SCALE) - 1, (int)((cachedFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }

            g2.setColor(Color.BLUE);

            for (Line2D.Double cachedRaytracingLine : cached_raytracing_lines) {
                //Draw a stupid thick line
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE), (int)((cachedRaytracingLine.y1) * SCALE), (int)((cachedRaytracingLine.x2) * SCALE), (int)((cachedRaytracingLine.y2) * SCALE));
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)+1, (int)((cachedRaytracingLine.y1) * SCALE)+1, (int)((cachedRaytracingLine.x2) * SCALE)+1, (int)((cachedRaytracingLine.y2) * SCALE)+1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)-1, (int)((cachedRaytracingLine.y1) * SCALE)-1, (int)((cachedRaytracingLine.x2) * SCALE)-1, (int)((cachedRaytracingLine.y2) * SCALE)-1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)+1, (int)((cachedRaytracingLine.y1) * SCALE)-1, (int)((cachedRaytracingLine.x2) * SCALE)+1, (int)((cachedRaytracingLine.y2) * SCALE)-1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)-1, (int)((cachedRaytracingLine.y1) * SCALE)+1, (int)((cachedRaytracingLine.x2) * SCALE)-1, (int)((cachedRaytracingLine.y2) * SCALE)+1);
            }

            g2.dispose();
            try {
                ImageIO.write(b, "png", new File("C:\\Users\\Ewan\\Downloads\\image.png"));
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        @Override
        public void update(Dunjeon d) {

            List<Line2D.Double> rayTracingLines = new ArrayList<>();
            List<Point2D> filledWalls = new ArrayList<>();
            List<Point2D> knownFilledWalls = new ArrayList<>();

            logger.debug("Updating Datastream: Sight");
            for (int i = 0; i < getSubscribers().size(); i++) {
                Sensor<SightStreamParameters> sensor = getSubscribers().get(i);

                //Get necessary parameters from sensor
                SightStreamParameters params = sensor.getParameters();
                HashMap<Vector2, Set<WorldUtils.Side>> tilesMap = new HashMap<>();
                Vector2 sensorPos = params.getSightSourceLocation();

                for (BasicCell basicCell : sensor.creature.getFloor().getCellsAsList()) {
                    if(!basicCell.canBeSeenThroughBy(sensor.creature)){
                        filledWalls.add(new Point2D.Double(basicCell.getIntegerX(), basicCell.getIntegerY()));
                    }
                }

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
                    logger.trace("Processing Raycasting");
                    logger.trace("=====================");
                    //************* Do Raycasting *******************//
                    double range = params.getSightRange();
                    double fov = params.getSightFieldOfView();
                    double currentEntityAngle = params.getCurrentSightAngle();

                    double startingAngle = currentEntityAngle - fov / 2;
                    double endingAngle = currentEntityAngle + fov / 2;
                    double minAngle = 0.001f;
                    double tinyAngle = 0.00001f;
                    int rayCounter = 0;

                    logger.trace("pos: " + params.sightSourceLocation + " fov: " + fov + ", range: " + range + ", starting angle: " + startingAngle);

                    //Iterate across rays. Written to ensure that the first and last angles are casted, to avoid any funny business
                    double currentAngle = startingAngle;
                    boolean finalLap = false;
                    do {
                        if (currentAngle > endingAngle) {
                            logger.trace("Last ray in this loop, trimming angle from : " + currentAngle + ", to: " + endingAngle);
                            finalLap = true;
                            currentAngle = endingAngle;
                        }

                        logger.trace(String.format("============================== Ray # : %d, angle : %.6f", rayCounter, currentAngle));

                        Vector2 rayEnd = sensorPos.copy().add(Vector2.create(range, currentAngle));
                        var intersections = WorldUtils.getIntersectedTilesWithWall(sensorPos, rayEnd);

                        logger.trace("Ray end : " + StringUtils.formatVector(rayEnd) +", # of intersections = " + intersections.size());

                        for (WorldUtils.IntersectionData intersectionData : intersections) {
                            if (!tilesMap.containsKey(intersectionData.getCellCoordinate())) {
                                tilesMap.put(intersectionData.getCellCoordinate(), new HashSet<>());
                            }
                            tilesMap.get(intersectionData.getCellCoordinate()).add(intersectionData.getSide());
                            knownFilledWalls.add(new Point2D.Double(intersectionData.getCellCoordinate().x, intersectionData.getCellCoordinate().y));
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(intersectionData.getCellCoordinate());
                            if (basicCell == null || !basicCell.canBeSeenThroughBy(sensor.creature) || intersections.indexOf(intersectionData) == intersections.size()-1) {
                                logger.trace("Ray collided with cell: " + StringUtils.formatVector(intersectionData.getCellCoordinate()) + ", at " + StringUtils.formatVector(intersectionData.getIntersectionPoint()));
                                // Figure out the _minimum_ angle increase required to push past this cell

                                rayTracingLines.add(new Line2D.Double(sensorPos.x, sensorPos.y, intersectionData.getIntersectionPoint().x, intersectionData.getIntersectionPoint().y));

                                // 1. Define the wall/side that has been collided with
                                // 2. Get the corners of that wall
                                // 2.5 transform the angles to match - as the ray angle and the angle to the points will be on different domains
                                // 3. Compare the current angle + rotation direction (should always be the same?) to figure out which of the two is next in the raytracer's path
                                // 4. Take angle to point form #3 and add a tiny bit to it

                                Pair<Vector2, Vector2> endPoints = intersectionData.getAdjacentSideEndPoints();
                                if (endPoints != null) {

                                    //Collect relevant angles
                                    double theta1 = Math.atan2(endPoints.getElement0().y - sensorPos.y, endPoints.getElement0().x - sensorPos.x);
                                    double theta2 = Math.atan2(endPoints.getElement1().y - sensorPos.y, endPoints.getElement1().x - sensorPos.x);

                                    logger.trace(String.format("Endpoints: %s, %s on side %s", StringUtils.formatVector(endPoints.getElement0()), StringUtils.formatVector(endPoints.getElement1()), intersectionData.getSide().name()));
                                    logger.trace(String.format("angles to endpoints : %.2f, %.2f", theta1, theta2));

                                    if (theta1 == theta2) {
                                        logger.error(String.format("theta1: %f, theta2: %f, rayStart: %s, rayEnd: %s, currentAngle: %f, endPoints: %s", theta1, theta2, sensorPos, rayEnd, currentAngle, endPoints));
                                        logger.error("theta1 and theta2 are equal. This should NEVER occur, see error logs below");
                                        throw new RuntimeException("theta1 == theta2, should never occur");
                                    }

                                    //Calculate difference for both angles from current ray

                                    double theta1Diff = theta1 - currentAngle;
                                    double theta2Diff = theta2 - currentAngle;

                                    if (Math.abs(theta1Diff) > Math.PI) {
                                        double overshoot = Math.abs(theta1Diff) - Math.PI;
                                        theta1Diff = Math.PI * -1 * Math.signum(theta1Diff) - overshoot * -1 * Math.signum(theta1Diff);
                                    }
                                    if (Math.abs(theta2Diff) > Math.PI) {
                                        double overshoot = Math.abs(theta2Diff) - Math.PI;
                                        theta2Diff = Math.PI * -1 * Math.signum(theta2Diff) - overshoot * -1 * Math.signum(theta2Diff);
                                    }

                                    logger.trace(String.format("adjusted theta1, theta2 : %.2f, %.2f", theta1Diff, theta2Diff));
                                    if (Math.signum(theta1Diff) == Math.signum(theta2Diff)) {
                                        logger.error("theta1diff and theta2diff's signums are equal. This should NEVER occur, and signifies that I might be bad at trig");
                                        throw new RuntimeException("Math.signum(theta1Diff) == Math.signum(theta2Diff), should never occur");
                                    }
                                    if (theta1Diff > theta2Diff) {
                                        logger.trace("theta1diff is positive, choosing theta1+delta as next angle");
                                        currentAngle += theta1Diff + tinyAngle;
                                    } else if (theta1Diff < theta2Diff) {
                                        logger.trace("theta2diff is positive, choosing theta2+delta as next angle");
                                        currentAngle += theta2Diff + tinyAngle;
                                    }
                                }
                                break;
                            }
                        }

                        rayCounter++;
                        logger.trace("-----------------------");
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
                            if(basicCell != null && !basicCell.canBeSeenThroughBy(sensor.creature))
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
            cached_filled_walls = filledWalls;
            cached_raytracing_lines = rayTracingLines;
            cached_known_filled_walls = knownFilledWalls;
            if(!didPrint) {
                PRINT_DEBUG_IMAGE();
//                didPrint = true;
            }
        }
        boolean didPrint = false;

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
