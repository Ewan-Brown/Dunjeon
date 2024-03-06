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
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Datastreams {

    static Logger logger = LogManager.getLogger();

    public static class SightDataStream extends Datastream<SightDataStream.SightStreamParameters> {

        /**
         * The desired arc length of the 'wedges' created between each of the rays
         */
        static final double DESIRED_ARCLENGTH = 0.01;

        //Delete all this hacky bullshit TODO

        static List<Line2D.Double> cached_raytracing_lines = new ArrayList<>();
        static List<Line2D.Double> cached_full_raytracing_lines = new ArrayList<>();
        static List<Line2D.Double> cached_visible_sides = new ArrayList<>();
        static List<Point2D> cached_filled_walls = new ArrayList<>();
        static List<Point2D> cached_visible_walls = new ArrayList<>();
        static List<Point2D> cached_visible_floors = new ArrayList<>();

        public static Line2D.Double convertVectorsToLine(Vector2 v1, Vector2 v2){
            return new Line2D.Double(v1.x, v1.y, v2.x, v2.y);
        }

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
            for (Point2D cachedKnownFilledWall : cached_visible_walls) {
                g2.fillRect((int)((cachedKnownFilledWall.getX()) * SCALE) - 1, (int)((cachedKnownFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }
            g2.setColor(Color.ORANGE);
            for (Point2D cachedKnownFilledWall : cached_visible_floors) {
                g2.fillRect((int)((cachedKnownFilledWall.getX()) * SCALE) - 1, (int)((cachedKnownFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }
            g2.setColor(Color.BLACK);
            for (Point2D cachedFilledWall : cached_filled_walls) {
                g2.drawRect((int)((cachedFilledWall.getX()) * SCALE), (int)((cachedFilledWall.getY()) * SCALE), SCALE, SCALE);
                g2.drawRect((int)((cachedFilledWall.getX()) * SCALE) - 1, (int)((cachedFilledWall.getY()) * SCALE) - 1, SCALE - 2, SCALE -2 );
            }

            g2.setColor(Color.CYAN);

            for (Line2D.Double cachedRaytracingLine : cached_full_raytracing_lines) {
                //Draw a stupid thick line
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE), (int)((cachedRaytracingLine.y1) * SCALE), (int)((cachedRaytracingLine.x2) * SCALE), (int)((cachedRaytracingLine.y2) * SCALE));
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)+1, (int)((cachedRaytracingLine.y1) * SCALE)+1, (int)((cachedRaytracingLine.x2) * SCALE)+1, (int)((cachedRaytracingLine.y2) * SCALE)+1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)-1, (int)((cachedRaytracingLine.y1) * SCALE)-1, (int)((cachedRaytracingLine.x2) * SCALE)-1, (int)((cachedRaytracingLine.y2) * SCALE)-1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)+1, (int)((cachedRaytracingLine.y1) * SCALE)-1, (int)((cachedRaytracingLine.x2) * SCALE)+1, (int)((cachedRaytracingLine.y2) * SCALE)-1);
                g2.drawLine((int)((cachedRaytracingLine.x1) * SCALE)-1, (int)((cachedRaytracingLine.y1) * SCALE)+1, (int)((cachedRaytracingLine.x2) * SCALE)-1, (int)((cachedRaytracingLine.y2) * SCALE)+1);
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

            g2.setColor(Color.BLUE);

            for (Line2D.Double cachedVisibleSide : cached_visible_sides) {
                g2.drawLine((int)((cachedVisibleSide.x1) * SCALE), (int)((cachedVisibleSide.y1) * SCALE), (int)((cachedVisibleSide.x2) * SCALE), (int)((cachedVisibleSide.y2) * SCALE));
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
            List<Line2D.Double> fullRayTracingLines = new ArrayList<>();
            List<Line2D.Double> visibleSides = new ArrayList<>();
            List<Point2D> filledWalls = new ArrayList<>();
            List<Point2D> visibleFilledWalls = new ArrayList<>();
            List<Point2D> visibleFloors = new ArrayList<>();

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
                    WorldUtils.Side lastRayEndSide = null;

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
                        fullRayTracingLines.add(convertVectorsToLine(sensorPos, rayEnd));

                        logger.trace("Ray end : " + StringUtils.formatVector(rayEnd) +", # of intersections = " + intersections.size());

                        WorldUtils.IntersectionData previousIntersection = null;
                        for (WorldUtils.IntersectionData intersectionData : intersections) {
                            if (!tilesMap.containsKey(intersectionData.getCellCoordinate())) {
                                tilesMap.put(intersectionData.getCellCoordinate(), new HashSet<>());
                            }
                            tilesMap.get(intersectionData.getCellCoordinate()).add(intersectionData.getSide());
                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(intersectionData.getCellCoordinate());
                            if(basicCell.canBeSeenThroughBy(sensor.creature)){
                                visibleFloors.add(new Point2D.Double(intersectionData.getCellCoordinate().x, intersectionData.getCellCoordinate().y));
                            }else{
                                visibleFilledWalls.add(new Point2D.Double(intersectionData.getCellCoordinate().x, intersectionData.getCellCoordinate().y));
                            }
                            if (!basicCell.canBeSeenThroughBy(sensor.creature) || intersections.indexOf(intersectionData) == intersections.size() - 1) {


                                logger.trace("Ray collided with cell: " + StringUtils.formatVector(intersectionData.getCellCoordinate()) + ", at " + StringUtils.formatVectorFullPrecision(intersectionData.getIntersectionPoint()));
                                // Figure out the _minimum_ angle increase required to push past this cell

                                rayTracingLines.add(convertVectorsToLine(sensorPos, intersectionData.getIntersectionPoint()));
                                List<Vector2> potentialEndPoints = new ArrayList<>(intersectionData.getAdjacentSideEndPoints());

                                if(previousIntersection != null && previousIntersection.getSide() != intersectionData.getSide()){
                                    potentialEndPoints.addAll(previousIntersection.getAdjacentSideEndPoints());
                                }

                                //Only null if the current cell is the one the player is in
                                Vector2 closestPoint = null;
                                double closestAngle = 0;
                                for (Vector2 potentialEndPoint : potentialEndPoints) {
                                    Vector2 vectorToEndpoint = potentialEndPoint.copy().subtract(sensorPos);
                                    double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                    double relativeAngle = angle - currentAngle;
                                    if((closestPoint == null || relativeAngle < closestAngle) && relativeAngle > 0){
                                        closestPoint = potentialEndPoint;
                                        closestAngle = relativeAngle;
                                    }
                                }
                                currentAngle += closestAngle + tinyAngle;
                                break;
                            }
                            previousIntersection = intersectionData;
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
            cached_visible_walls = visibleFilledWalls;
            cached_visible_floors = visibleFloors;
            cached_visible_sides = visibleSides;
            cached_full_raytracing_lines = fullRayTracingLines;

            if(!didPrint) {
                PRINT_DEBUG_IMAGE();
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
