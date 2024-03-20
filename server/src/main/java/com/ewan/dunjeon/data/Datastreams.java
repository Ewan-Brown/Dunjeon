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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
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

import static com.ewan.dunjeon.server.world.WorldUtils.*;

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
        public static boolean debugNextImage = false;

        public static Line2D.Double convertVectorsToLine(Vector2 v1, Vector2 v2){
            return new Line2D.Double(v1.x, v1.y, v2.x, v2.y);
        }

        public static void PRINT_DEBUG_IMAGE(){
            logger.warn("PRINTING DEBUG IMAGE!");
            BufferedImage b = new BufferedImage(10000,10000, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2 = b.createGraphics();
            int SCALE = 200;
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
                throw new RuntimeException(e);
            }
        }



        //Optimization tip : We are currently very conservative about how we select "nextPointToMarchTo", we should ONLY
        // grab an intersection if the angle to the point is minimum AND we haven't yet identified the tile on the 'opposing' side of the intersection. If we know that that is a wall/floor then no point right?!
        // Use debug
        @Override
        public void update(Dunjeon d) {
            if(debugNextImage){
                Configurator.setLevel(logger, Level.TRACE);
                Configurator.setLevel("logger.worldutils.name", Level.TRACE);
            }

            List<Line2D.Double> rayTracingLines = new ArrayList<>();
            List<Line2D.Double> fullRayTracingLines = new ArrayList<>();
            List<Line2D.Double> visibleSides = new ArrayList<>();
            List<Point2D> filledWalls = new ArrayList<>();
            List<Point2D> visibleFilledWalls = new ArrayList<>();
            List<Point2D> visibleFloors = new ArrayList<>();

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
                    if (logger.isTraceEnabled())
                        logger.trace("=====Processing Raycasting=====");

                    //************* Do Raycasting *******************//
                    double range = params.getSightRange();
                    double rangeSquared = range*range;
                    double fov = params.getSightFieldOfView();
                    double currentEntityAngle = params.getCurrentSightAngle();

                    double startingAngle = currentEntityAngle - fov / 2;
                    double endingAngle = currentEntityAngle + fov / 2;
                    double tinyAngle = 0.00001d;
                    double minRelativeAngle = 0.0001d; //Used to cut off some floating point errors... May need reinspection
                    int rayCounter = 0;

                    logger.trace("pos: " + params.sightSourceLocation + " fov: " + fov + ", range: " + range + ", starting angle: " + startingAngle + " endingAngle: " + endingAngle);


                    tilesMap.put(new Vector2( Math.floor(sensorPos.x), Math.floor(sensorPos.y)), Set.of(Side.values()));


                    //Iterate across rays. Written to ensure that the first and last angles are casted, to avoid any funny business
                    double currentAngle = startingAngle;
                    boolean finalLap = false;

                    //Keeps track of the last ray's final intersection point IF it hit a wall, used for optimization. if null, we didn't hit a wall and gotta use a different optimization technique
                    IntersectionData previousCollidingIntersection = null;
                    do {
                        if (currentAngle > endingAngle) {
                            if(logger.isTraceEnabled())
                                logger.trace("Last ray in this loop, trimming angle from : " + currentAngle + ", to: " + endingAngle);
                            finalLap = true;
                            currentAngle = endingAngle;
                        }

                        Vector2 currentPoint = sensorPos;

                        if(logger.isTraceEnabled())
                            logger.trace(String.format("============================== Ray # : %d, angle : %.6f", rayCounter, currentAngle));

                        Vector2 rayEnd = sensorPos.copy().add(Vector2.create(range, currentAngle));

                        // ************************************* GET INTERSECTIONS **********************************************



                        IntersectionData finalIntersectionOfThisRay = null;

                        Vector2 nextPointToMarchTo = null;
                        double nextRayAngle = 0;
                        boolean didCollide = false;

                        //Iterate across intersects and find tiles
                        while (true) /**/{

                            Optional<IntersectionData> intersectionDataOpt = WorldUtils.getNextGridIntersect(currentPoint, rayEnd);

                            if(intersectionDataOpt.isEmpty()){
                                if(logger.isTraceEnabled())
                                    logger.trace("no intersections returned");
                                break;
                            }
                            IntersectionData intersectionData = intersectionDataOpt.get();
                            finalIntersectionOfThisRay = intersectionData;
                            // INSPECTING AN INTERSECTION
                            if(logger.isTraceEnabled())
                                logger.trace("inspecting intersection: " + intersectionData);
                            if (!tilesMap.containsKey(intersectionData.getCellCoordinate())) {
                                tilesMap.put(intersectionData.getCellCoordinate(), new HashSet<>());
                            }
                            tilesMap.get(intersectionData.getCellCoordinate()).add(intersectionData.getSide());

                            BasicCell basicCell = sensor.creature.getFloor().getCellAt(intersectionData.getCellCoordinate());
                            boolean isBlocking = !basicCell.canBeSeenThroughBy(sensor.creature);
                            if(logger.isTraceEnabled()){
                                logger.trace("blocking cell : " + isBlocking);
                            }
                            if(!isBlocking){
                                visibleFloors.add(new Point2D.Double(intersectionData.getCellCoordinate().x, intersectionData.getCellCoordinate().y));
                            }else{
                                visibleFilledWalls.add(new Point2D.Double(intersectionData.getCellCoordinate().x, intersectionData.getCellCoordinate().y));
                            }

                            //OPTIMIZATION : If the ray has collided with a wall that is parallel and colinear to the last wall, we can ignore all previous potential endpoints
                            // This does mean discarding already-calculated data but we save on multiple _entire_ rays so it's a net positive from what we have now

                            if(isBlocking){
                                if(previousCollidingIntersection != null){
                                    if(previousCollidingIntersection.getSide() == intersectionData.getSide()){
                                        double currentIntersectX = intersectionData.getIntersectionPoint().x;
                                        double currentIntersectY = intersectionData.getIntersectionPoint().y;
                                        double previousIntersectX = previousCollidingIntersection.getIntersectionPoint().x;
                                        double previousIntersectY = previousCollidingIntersection.getIntersectionPoint().y;
                                        if(currentIntersectX == previousIntersectX || currentIntersectY == previousIntersectY){
                                            nextPointToMarchTo = null; // "clear" the results from previous angle checks
                                        }
                                    }
                                }

                            }

                            //****************************************************************

                            for (Corner corner : intersectionData.getSide().getCorners()) {
                                Vector2 potentialEndPoint = corner.getLocalCoord().sum(intersectionData.getCellCoordinate());
                                Vector2 vectorToEndpoint = potentialEndPoint.copy().subtract(sensorPos);
                                double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                double relativeAngle = getAngleDiffInAtan2Domain(angle, currentAngle);
                                if((nextPointToMarchTo == null || relativeAngle < nextRayAngle) && relativeAngle > minRelativeAngle){
                                    if(logger.isTraceEnabled())
                                        logger.trace(" taking endpoint: " + StringUtils.formatVector(potentialEndPoint) + ", relativeAngle: " + relativeAngle);
                                    nextPointToMarchTo = potentialEndPoint;
                                    nextRayAngle = relativeAngle;
                                }else{
                                    if(logger.isTraceEnabled())
                                        logger.trace(" ignoring endpoint: " + StringUtils.formatVectorFullPrecision(potentialEndPoint) + ", relativeAngle: "+relativeAngle);
                                }
                            }

                            if(logger.isTraceEnabled())
                                logger.trace("endpoint chosen: " + StringUtils.formatVector(nextPointToMarchTo)+", with angle: " + nextRayAngle);

                            if (isBlocking) {
                                rayTracingLines.add(convertVectorsToLine(sensorPos, intersectionData.getIntersectionPoint()));
                                didCollide = true;
                                if(logger.isTraceEnabled())
                                    logger.trace("Ray collided with cell: " + StringUtils.formatVector(intersectionData.getCellCoordinate()) + ", at " + StringUtils.formatVectorFullPrecision(intersectionData.getIntersectionPoint()));
                                break;
                            }
                            currentPoint = intersectionData.getIntersectionPoint();

                        }

                        if(didCollide) {
                            previousCollidingIntersection = finalIntersectionOfThisRay;
                        }else{
                            if(logger.isTraceEnabled())
                                logger.trace("ray ended without colliding, entering Optimization 2");
                            rayTracingLines.add(convertVectorsToLine(sensorPos, rayEnd));
                            // OPTIMIZATION 2
                            // If we are here then the ray has ended and we have NOT collided
                            List<Vector2> potentialEndPoints = new ArrayList<>();
                            Vector2 furthestPossibleEndpoint = null;
                            double furthestAngle = 0;
                            for (Side side : Side.values()) {
                                List<Vector2> points = WorldUtils.getIntersectsBetweenCircleAndTileSide(sensorPos, rangeSquared,new Vector2(Math.floor(rayEnd.x), Math.floor(rayEnd.y)), side);
                                if(logger.isTraceEnabled())
                                    logger.trace("for side " + side+ ", " + points.size()+" intersects found");
                                potentialEndPoints.addAll(points);
                            }
                            for (Vector2 potentialEndPoint : potentialEndPoints) {

                                Vector2 vectorToEndpoint = potentialEndPoint.copy().subtract(sensorPos);
                                double angle = Math.atan2(vectorToEndpoint.y, vectorToEndpoint.x);
                                double relativeAngle = getAngleDiffInAtan2Domain(angle, currentAngle);
                                if(logger.isTraceEnabled()){
                                    logger.trace("looking at endpoint: " + StringUtils.formatVectorFullPrecision(potentialEndPoint));
                                    logger.trace("angle: " + angle + ", relative angle: " + relativeAngle);
                                }
                                if((furthestPossibleEndpoint == null || furthestAngle  < relativeAngle) && relativeAngle > minRelativeAngle){
                                    if(logger.isTraceEnabled())
                                        logger.trace("taking this endpoint!");
                                    furthestPossibleEndpoint = potentialEndPoint;
                                    furthestAngle = relativeAngle;
                                    nextRayAngle = furthestAngle;
                                }
                            }
                        }
                        fullRayTracingLines.add(convertVectorsToLine(sensorPos, rayEnd));
                        currentAngle += nextRayAngle + tinyAngle;

                        rayCounter++;
                        if(logger.isTraceEnabled())
                            logger.trace("-----------------------");
                    } while (!finalLap);
                    if(logger.isTraceEnabled())
                        logger.trace("# of rays: " + rayCounter);

                    for (Map.Entry<Vector2, Set<WorldUtils.Side>> tile : tilesMap.entrySet()) {
                        BasicCell basicCell = sensor.creature.getFloor().getCellAt(tile.getKey());
                        if (basicCell == null) continue;
                        Datas.CellData cellData = (new Datas.CellEnterableData(basicCell.canBeEntered(sensor.creature) ? Datas.CellEnterableData.EnterableStatus.ENTERABLE : Datas.CellEnterableData.EnterableStatus.BLOCKED));
                        dataAmalgamated.add(DataWrappers.wrapCellData(List.of(cellData), new CellPosition(basicCell.getWorldCenter(), basicCell.getFloor().getUUID()), d.getTimeElapsed(), d.getTicksElapsed()));
                    }

                    for (Entity entity : sensor.creature.getFloor().getEntities()) {

                        Vector2 entityPos = entity.getWorldCenter();

                        if(entity != sensor.creature) {
                            var results = WorldUtils.getIntersectedTilesWithWall(sensorPos, entityPos);
                            for (WorldUtils.IntersectionData result : results) {
                                BasicCell basicCell = sensor.creature.getFloor().getCellAt(result.getCellCoordinate());
                                if (basicCell != null && !basicCell.canBeSeenThroughBy(sensor.creature))
                                    break;
                            }
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

            if(debugNextImage) {
                debugNextImage=false;
                PRINT_DEBUG_IMAGE();
                Configurator.setLevel(logger, Level.WARN);
                Configurator.setLevel("logger.worldutils.name", Level.WARN);
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
            private final Boolean trueSight; //Magic sight that lets you see everything! (really just for debugging)
        }
    }
}
