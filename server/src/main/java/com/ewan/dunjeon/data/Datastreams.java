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



        @Override
        public void update(Dunjeon d) {
            if(debugNextImage){
                Configurator.setLevel(logger, Level.TRACE);
                Configurator.setLevel("logger.worldutils.name", Level.TRACE);
            }else{
                Configurator.setLevel(logger, Level.WARN);
                Configurator.setLevel("logger.worldutils.name", Level.WARN);
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
                    double fov = params.getSightFieldOfView();
                    double currentEntityAngle = params.getCurrentSightAngle();

                    double startingAngle = currentEntityAngle - fov / 2;
                    double endingAngle = currentEntityAngle + fov / 2;
                    double tinyAngle = 0.00001d;
                    double minRelativeAngle = 0.0001d; //Used to cut off some floating point errors... May need reinspection
                    int rayCounter = 0;

                    logger.trace("pos: " + params.sightSourceLocation + " fov: " + fov + ", range: " + range + ", starting angle: " + startingAngle + " endingAngle: " + endingAngle);

                    Vector2 currentPoint = new Vector2( Math.floor(sensorPos.x), Math.floor(sensorPos.y));

                    IntersectionData innerPoint = new IntersectionData(sensorPos, currentPoint, Side.WITHIN);
                    Set<Side> t = new HashSet<>();
                    t.add(innerPoint.getSide());
                    tilesMap.put(currentPoint, t);

                    //Iterate across rays. Written to ensure that the first and last angles are casted, to avoid any funny business
                    double currentAngle = startingAngle;
                    boolean finalLap = false;
                    do {
                        if (currentAngle > endingAngle) {
                            if(logger.isTraceEnabled())
                                logger.trace("Last ray in this loop, trimming angle from : " + currentAngle + ", to: " + endingAngle);
                            finalLap = true;
                            currentAngle = endingAngle;
                        }

                        if(logger.isTraceEnabled())
                            logger.trace(String.format("============================== Ray # : %d, angle : %.6f", rayCounter, currentAngle));

                        Vector2 rayEnd = sensorPos.copy().add(Vector2.create(range, currentAngle));

                        // ************************************* GET INTERSECTIONS **********************************************

                        double x1 = sensorPos.x;
                        double y1 = sensorPos.y;

                        double x2 = rayEnd.x;
                        double y2 = rayEnd.y;

                        if(logger.isTraceEnabled())
                            logger.trace(String.format("(%f, %f) -> (%f, %f)", x1, y1, x2, y2));

                        double dx = x2 - x1;
                        double dy = y2 - y1;

                        if(dx == 0 && dy == 0){
                            throw new IllegalArgumentException("cannot do raymarch if dx and dy both equal zero!"); //TODO check if we don't need this?
                        }

                        double slope = dy / dx;
                        double b = y1 - slope * x1;

                        double currentX = x1;
                        double currentY = y1;

                        if(logger.isTraceEnabled())
                            logger.trace(String.format("dx: %.10f, dy: %.10f", dx, dy));

                        Vector2 nextPointToMarchTo = null;
                        double nextRayAngle = 0;
                        boolean didCollide = false;

                        //Iterate across intersects and find tiles
                        while (true) /**/{

                            double nextVerticalIntersect = 0;
                            double distToNextVerticalIntersect = Float.MAX_VALUE;
                            double nextHorizontalIntersect = 0;
                            double distToNextHorizontalIntersect = Float.MAX_VALUE;



                            Side side;
                            if (dx != 0) {
                                if (currentX == Math.round(currentX)) {
                                    nextVerticalIntersect = currentX + Math.signum(dx);

                                } else {
                                    nextVerticalIntersect = (dx > 0) ? Math.ceil(currentX) : Math.floor(currentX);
                                }
                                distToNextVerticalIntersect = (nextVerticalIntersect - currentX) / dx;
                                if(logger.isTraceEnabled())
                                    logger.trace(String.format("nextVerticalIntersect: %f\ndist: %f", nextVerticalIntersect, distToNextVerticalIntersect));
                            }
                            if (dy != 0) {
                                if (currentY == Math.round(currentY)) {
                                    nextHorizontalIntersect = currentY + Math.signum(dy);
                                } else {
                                    nextHorizontalIntersect = (dy > 0) ? Math.ceil(currentY) : Math.floor(currentY);
                                }
                                distToNextHorizontalIntersect = (nextHorizontalIntersect - currentY) / dy;
                                if(logger.isTraceEnabled())
                                    logger.trace(String.format("nextHorizontalIntersect: %f\ndist: %f", nextHorizontalIntersect, distToNextHorizontalIntersect));
                            }

                            double nextInterceptX, nextInterceptY;

                            AxisAlignment intersectAlignment;

                            if (distToNextVerticalIntersect < distToNextHorizontalIntersect) {
                                nextInterceptX = nextVerticalIntersect;
                                nextInterceptY = slope * nextInterceptX + b;

                                intersectAlignment = AxisAlignment.VERTICAL;
                                side = (dx > 0) ? Side.WEST : Side.EAST;
                                if(logger.isTraceEnabled())
                                    logger.trace("Going with vertical intersection");

                            } else {
                                nextInterceptY = nextHorizontalIntersect;
                                nextInterceptX = (nextInterceptY - b) / slope;

                                if(logger.isTraceEnabled()) {
                                    logger.trace(String.format("y = %f, m = %f, b = %f", nextInterceptY, b, slope));
                                    logger.trace(String.format("x calculated as = %f", nextInterceptX));
                                }

                                intersectAlignment = AxisAlignment.HORIZONTAL;
                                side = (dy < 0) ? Side.NORTH :Side.SOUTH;
                                if(logger.isTraceEnabled())
                                    logger.trace("Going with horizontal intersection: ");
                            }

                            if(dx == 0){
                                nextInterceptX = currentX;
                            }
                            if(dy == 0){
                                nextInterceptY = currentY;
                            }

                            if(logger.isTraceEnabled())
                                logger.trace(String.format(" on %s side at (%f, %f)", side, nextInterceptX, nextInterceptY));

                            if (Math.abs(nextInterceptX - x1) > Math.abs(dx) || Math.abs(nextInterceptY - y1) > Math.abs(dy) ) {
                                break;
                            } else {
                                int nextTileX, nextTileY;

                                if (intersectAlignment == AxisAlignment.VERTICAL) {
                                    if (dx > 0) {
                                        nextTileX = (int) nextInterceptX;
                                    } else {
                                        nextTileX = (int) (nextInterceptX - 1);
                                    }
                                    nextTileY = (int) Math.floor(nextInterceptY);
                                } else {
                                    if (dy > 0) {
                                        nextTileY = (int) nextInterceptY;
                                    } else {
                                        nextTileY = (int) (nextInterceptY - 1);
                                    }
                                    nextTileX = (int) Math.floor(nextInterceptX);
                                }
                                IntersectionData intersectionData = new IntersectionData(new Vector2(nextInterceptX, nextInterceptY), new Vector2(nextTileX, nextTileY), side);
                                // INSPECTING AN INTERSECTION
                                if(logger.isTraceEnabled())
                                    logger.trace("inspecting intersection: " + intersectionData);

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

                                if(intersectionData.getSide() == Side.WITHIN){
                                    if(logger.isTraceEnabled())
                                        logger.trace("Identified as WITHIN, skipping next endpoint calculation");
                                    continue;
                                }

                                for (Vector2 potentialEndPoint : intersectionData.getAdjacentSideEndPoints()) {
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
                                            logger.trace(" ignoring endpoint: " + StringUtils.formatVector(potentialEndPoint) + ", relativeAngle: "+relativeAngle);
                                    }
                                }

                                if(logger.isTraceEnabled())
                                    logger.trace("endpoint chosen: " + StringUtils.formatVector(nextPointToMarchTo)+", with angle: " + nextRayAngle);

                                if (!basicCell.canBeSeenThroughBy(sensor.creature)) {
                                    rayTracingLines.add(convertVectorsToLine(sensorPos, intersectionData.getIntersectionPoint()));
                                    didCollide = true;
                                    if(logger.isTraceEnabled())
                                        logger.trace("Ray collided with cell: " + StringUtils.formatVector(intersectionData.getCellCoordinate()) + ", at " + StringUtils.formatVectorFullPrecision(intersectionData.getIntersectionPoint()));
                                    break;
                                }
                                currentX = nextInterceptX;
                                currentY = nextInterceptY;
                            }
                        }

                        if(didCollide)
                            rayTracingLines.add(convertVectorsToLine(sensorPos, rayEnd));
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
