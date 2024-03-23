package com.ewan.dunjeon.server.world;

import com.ewan.dunjeon.server.world.cells.BasicCell;
import com.ewan.util.StringUtils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.abs;


public class WorldUtils {

    public static final double ENTITY_WITHIN_TILE_THRESHOLD = 0.5f;
    private static final double INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD = 0.0001f;
    static Logger logger = LogManager.getLogger();

    public static boolean isAdjacent(BasicCell b1, BasicCell b2){
        if(b1 == b2){
            throw new IllegalArgumentException();
        }else {
            return isAdjacent(b1.getIntegerX(), b1.getIntegerY(), b2.getIntegerX(), b2.getIntegerY());
        }
    }
    public static boolean isAdjacent(Point b1, Point b2){
        return isAdjacent((int)b1.getX(), (int)b1.getY(), (int)b2.getX(), (int)b2.getY());
    }

    public static boolean isAdjacent(int x1, int y1, int x2, int y2){


        int xDiff = x2 - x1;
        int yDiff = y2 - y1;

        return Math.abs(yDiff) < 2 && Math.abs(xDiff) < 2;
    }

    /**
     * Lists integers between i1 and i2, exclusive.
     * @param i1
     * @param i2
     * @return
     */
    public static List<Integer> listIntsBetween(int i1, int i2){
        List<Integer> integers = new ArrayList<>();
        int i = i1;
        while(i != i2){
            i += (int)Math.signum(i2-i1);
            integers.add(i);
        }

        return integers;
    }

    @Getter
    public enum Corner {
        SOUTH_WEST(new Vector2(0, 0)),
        NORTH_WEST(new Vector2(0, 1)),
        NORTH_EAST(new Vector2(1, 1)),
        SOUTH_EAST(new Vector2(1, 0));

        //The vector spanning from a cell's local origin (bottom left corner) to this corner
        final Vector2 localCoord;

        //The vector spanning from a cell's local origin (bottom left corner) to the midline of this Side

        Corner(Vector2 localCoord) {
            this.localCoord = localCoord;
        }
    }

    @Getter
    public enum Side {
        NORTH(0.5, 1, AxisAlignment.HORIZONTAL, Corner.NORTH_WEST, Corner.NORTH_EAST),
        EAST(1, 0.5, AxisAlignment.VERTICAL,    Corner.SOUTH_EAST, Corner.NORTH_EAST),
        SOUTH(0.5, 0, AxisAlignment.HORIZONTAL, Corner.SOUTH_WEST, Corner.SOUTH_EAST),
        WEST(0, 0.5, AxisAlignment.VERTICAL,    Corner.SOUTH_WEST, Corner.NORTH_WEST);

        //The vector spanning from a cell's local origin (bottom left corner) to the midline of this Side
        final Vector2 localMidpointCoord;
        //The vectors spanning from the cells local origin to the edges of this side
        final List<Corner> corners;
        final AxisAlignment axis;

        Side(double x, double y, AxisAlignment a, Corner c1, Corner c2){
            localMidpointCoord = new Vector2(x,y);
            axis = a;
            corners = List.of(c1, c2);
        }
    }


    public enum AxisAlignment {
        VERTICAL(new Vector2(0, 1)),
        HORIZONTAL(new Vector2(1, 0));

        AxisAlignment(Vector2 unit){
            this.unitVector = unit;
        }

        final Vector2 unitVector;
    }


    @Getter
    public static class IntersectionData{

        final Vector2 intersectionPoint;
        final Vector2 cellCoordinate;
        final Side side;
        public IntersectionData(Vector2 intersectionPoint, Vector2 cellCoordinate, Side side) {
            this.intersectionPoint = intersectionPoint;
            this.cellCoordinate = cellCoordinate;
            this.side = side;

            Vector2 sideMidPoint = new Vector2(cellCoordinate).add(side.localMidpointCoord);

            //TODO This vector code is not necessary, we can cache this?
//            Vector2 p1 = sideMidPoint.copy().add(side.axis.unitVector.copy().multiply(0.5));
//            Vector2 p2 = sideMidPoint.copy().add(side.axis.unitVector.copy().multiply(-0.5));
//            adjacentSideEndPoints = List.of(p1, p2);
        }

        @Override
        public String toString() {
            return String.format("%s, side: %s, exact intersection at %s", StringUtils.formatVector(cellCoordinate), side, StringUtils.formatVectorFullPrecision(intersectionPoint));
        }
    }

    public static List<Vector2> getIntersectsBetweenCircleAndTileSide(Vector2 circleCenter, double circleRadiusSquared, Vector2 tileCoord, Side side){
        List<Vector2> data = new ArrayList<>();

        Vector2 relativeTileCoord = tileCoord.difference(circleCenter);

        if(side.getAxis() == AxisAlignment.HORIZONTAL){
            // y == y
            double relativeSideY = relativeTileCoord.y + side.getLocalMidpointCoord().y;
            double relativeSideYSquared = relativeSideY * relativeSideY;
            if(relativeSideYSquared > circleRadiusSquared){
                return new ArrayList<>();
            }
            double x = Math.sqrt(circleRadiusSquared - relativeSideYSquared);
            data.add(new Vector2(x, relativeSideY).add(circleCenter));
            data.add(new Vector2(-x, relativeSideY).add(circleCenter));

            data.removeIf(vector2 -> vector2.x > tileCoord.x + 1 || vector2.x < tileCoord.x);
        }
        else if(side.getAxis() == AxisAlignment.VERTICAL){
            // x == x
            double relativeSideX = relativeTileCoord.x + side.getLocalMidpointCoord().x;
            double relativeSideXSquared = relativeSideX * relativeSideX;
            if(relativeSideXSquared > circleRadiusSquared){
                return new ArrayList<>();
            }
            double y = Math.sqrt(circleRadiusSquared - relativeSideXSquared);
            data.add(new Vector2(relativeSideX, y).add(circleCenter));
            data.add(new Vector2(relativeSideX, -y).add(circleCenter));

            data.removeIf(vector2 -> vector2.y > tileCoord.y + 1 || vector2.y < tileCoord.y);
        }

        return data;
    }

    /**
     * Returns the first point where the vector formed from pos1 -> pos2 intersects with any integer 'line' on a cartesian grid,
     *  or empty if none (e.g the vector from (0.4, 0.2) -> (0.9, 0.8) does not intersect any grid lines
     * @param pos1
     * @param pos2
     * @return
     */
    public static Optional<IntersectionData> getNextGridIntersect(Vector2 pos1, Vector2 pos2){

        double x1 = pos1.x;
        double y1 = pos1.y;

        double x2 = pos2.x;
        double y2 = pos2.y;

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
                logger.trace(String.format("nextVerticalIntersect: %f, dist: %f", nextVerticalIntersect, distToNextVerticalIntersect));
        }
        if (dy != 0) {
            if (currentY == Math.round(currentY)) {
                nextHorizontalIntersect = currentY + Math.signum(dy);
            } else {
                nextHorizontalIntersect = (dy > 0) ? Math.ceil(currentY) : Math.floor(currentY);
            }
            distToNextHorizontalIntersect = (nextHorizontalIntersect - currentY) / dy;
            if(logger.isTraceEnabled())
                logger.trace(String.format("nextHorizontalIntersect: %f, dist: %f", nextHorizontalIntersect, distToNextHorizontalIntersect));
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

        if (Math.abs(nextInterceptX - x1) > Math.abs(dx) || Math.abs(nextInterceptY - y1) > Math.abs(dy) )
            return Optional.empty();
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
        return Optional.of(new IntersectionData(new Vector2(nextInterceptX, nextInterceptY), new Vector2(nextTileX, nextTileY), side));
    }

    public static List<IntersectionData> getIntersectedTilesWithWall(Vector2 pos1, Vector2 pos2){
        return getIntersectedTilesWithWall(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    /**
     * Returns the difference between two angles and fixes it to ensure it remains on the [-π, π] domain
     */
    public static double getAngleDiffInAtan2Domain(double angle1, double angle2){
        double diff = angle1 - angle2;
        if(diff > Math.PI){
            diff -= Math.PI;
            diff = -Math.PI + diff;
        }else if(diff < -Math.PI){
            diff += Math.PI;
            diff = Math.PI + diff;
        }
        return diff;
    }

    public static List<IntersectionData> getIntersectedTilesWithWall(double x1, double y1, double x2, double y2) {

        List<IntersectionData> intersectionDatas = new ArrayList<>();

        double dx = x2 - x1;
        double dy = y2 - y1;

        if(dx == 0 && dy == 0){
            throw new IllegalArgumentException("cannot do raymarch if dx and dy both equal zero!"); //TODO check if we don't need this?
        }

        double slope = dy / dx;
        double b = y1 - slope * x1;

        double currentX = x1;
        double currentY = y1;

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
            }
            if (dy != 0) {
                if (currentY == Math.round(currentY)) {
                    nextHorizontalIntersect = currentY + Math.signum(dy);
                } else {
                    nextHorizontalIntersect = (dy > 0) ? Math.ceil(currentY) : Math.floor(currentY);
                }
                distToNextHorizontalIntersect = (nextHorizontalIntersect - currentY) / dy;
            }

            double nextInterceptX, nextInterceptY;

            AxisAlignment intersectAlignment;

            if (distToNextVerticalIntersect < distToNextHorizontalIntersect) {
                nextInterceptX = nextVerticalIntersect;
                nextInterceptY = slope * nextInterceptX + b;

                intersectAlignment = AxisAlignment.VERTICAL;
                side = (dx > 0) ? Side.WEST : Side.EAST;

            } else {
                nextInterceptY = nextHorizontalIntersect;
                nextInterceptX = (nextInterceptY - b) / slope;

                intersectAlignment = AxisAlignment.HORIZONTAL;
                side = (dy < 0) ? Side.NORTH :Side.SOUTH;
            }

            if(dx == 0){
                nextInterceptX = currentX;
            }
            if(dy == 0){
                nextInterceptY = currentY;
            }

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
                intersectionDatas.add(new IntersectionData(new Vector2(nextInterceptX, nextInterceptY), new Vector2(nextTileX, nextTileY), side));
                currentX = nextInterceptX;
                currentY = nextInterceptY;
            }
        }
        return intersectionDatas;
    }

}
