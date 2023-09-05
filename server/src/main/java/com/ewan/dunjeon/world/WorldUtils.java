package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.lang.Math.abs;


public class WorldUtils {

    public static final double ENTITY_WITHIN_TILE_THRESHOLD = 0.5f;
    private static final double INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD = 0.0001f;

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

    public enum Side{
        NORTH,
        EAST,
        SOUTH,
        WEST,
        WITHIN
    }


    public enum AxisAlignment {
        VERTICAL,
        HORIZONTAL,
        DIAGONAL
    }

    public static List<Pair<Vector2, Side>> getIntersectedTilesWithWall(Vector2 pos1, Vector2 pos2){
        return getIntersectedTilesWithWall(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    public static List<Pair<Vector2, Side>> getIntersectedTilesWithWall(double x1, double y1, double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;



        double slope = dy / dx;
        double b = y1 - slope * x1;

        double currentX = x1;
        double currentY = y1;

        List<Pair<Vector2, Side>> intersectedTiles = new ArrayList<>();
        Vector2 currentPoint = new Vector2((int) Math.floor(x1), (int) Math.floor(y1));

        if(dx == 0){
            int tileX = (int)Math.floor(x1);
            Side intersectSide = dy > 0 ? Side.NORTH : Side.SOUTH;
            int y1Floored = (int)Math.floor(y1);
            int y2Ceil = (int)Math.floor(y2)+1;

            boolean reverse = false;
            int minY = y1Floored;
            int maxY = y2Ceil;
            if(y2Ceil < y1Floored){
                maxY = y1Floored;
                minY = y2Ceil;
                reverse = true;
            }
            IntStream.range(minY, maxY).forEach(value -> intersectedTiles.add(new Pair<>(new Vector2(tileX, value), intersectSide)));
            if(reverse){
                Collections.reverse(intersectedTiles);
            }
            return intersectedTiles;
        }else if(dy == 0){
            int tileY = (int)Math.floor(y1);
            Side intersectSide = dx > 0 ? Side.WEST : Side.EAST;
            int x1Floored = (int)Math.floor(x1);
            int x2Ceil = (int)Math.floor(x2)+1;

            boolean reverse = false;
            int minX = x1Floored;
            int maxX = x2Ceil;
            if(x2Ceil < x1Floored){
                maxX = x1Floored;
                minX = x2Ceil;
                reverse = true;
            }

            IntStream.range(minX, maxX).forEach(value -> intersectedTiles.add(new Pair<>(new Vector2(value, tileY), intersectSide)));
            if(reverse){
                Collections.reverse(intersectedTiles);
            }
            return intersectedTiles;
        }

        intersectedTiles.add(new Pair<>(currentPoint, Side.WITHIN));



        //Iterate across intersects and find tiles
        while (true) {

            double nextVerticalIntersect = 0;
            double distToNextVerticalIntersect = Float.MAX_VALUE;
            double nextHorizontalIntersect = 0;
            double distToNextHorizontalIntersect = Float.MAX_VALUE;

            Side side;

            if (dx != 0) {
                if (currentX == Math.round(currentX)) {
                    nextVerticalIntersect = currentX + Math.signum(dx);

                } else {
                    nextVerticalIntersect = (double) ((dx > 0) ? Math.ceil(currentX) : Math.floor(currentX));
                }
                distToNextVerticalIntersect = (nextVerticalIntersect - currentX) / dx;
            }
            if (dy != 0) {
                if (currentY == Math.round(currentY)) {
                    nextHorizontalIntersect = currentY + Math.signum(dy);
                } else {
                    nextHorizontalIntersect = (double) ((dy > 0) ? Math.ceil(currentY) : Math.floor(currentY));
                }
                distToNextHorizontalIntersect = (nextHorizontalIntersect - currentY) / dy;
            }

            double nextInterceptX, nextInterceptY;

            AxisAlignment intersectAlignment;

            if (distToNextVerticalIntersect < distToNextHorizontalIntersect) {
                nextInterceptX = nextVerticalIntersect;
                nextInterceptY = slope * nextInterceptX + b;

                if(distToNextVerticalIntersect < INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD){
                    double delta = INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD * Math.signum(dy);
                    nextInterceptY = Math.round(nextInterceptY) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that doubling point errors come into play in the result of y = mx+b
                }
                intersectAlignment = AxisAlignment.VERTICAL;
                side = (dx > 0) ? Side.WEST : Side.EAST;

            } else {
                nextInterceptY = nextHorizontalIntersect;
                nextInterceptX = (nextInterceptY - b) / slope;

                if(distToNextHorizontalIntersect < INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD){
                    double delta = INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD * Math.signum(dx);
                    nextInterceptX = Math.round(nextInterceptX) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that doubling point errors come into play in the result of y = mx+b
                }
                intersectAlignment = AxisAlignment.HORIZONTAL;
                side = (dy > 0) ? Side.NORTH :Side.SOUTH;
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
                Pair<Vector2, Side> pair = new Pair<>(new Vector2(nextTileX, nextTileY), side);
                intersectedTiles.add(pair);
                currentX = nextInterceptX;
                currentY = nextInterceptY;
            }
        }
        return intersectedTiles;
    }

    @Getter
    public static class CellPosition{
        private final Vector2 position;
        private final long floorID;

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof CellPosition) && ((CellPosition) obj).floorID == this.floorID && position.equals(((CellPosition) obj).getPosition());
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, floorID);
        }

        public CellPosition(Vector2 position, long floorID) {
            this.position = position;
            this.floorID = floorID;
        }

        public CellPosition(BasicCell cell) {
            this.position = cell.getWorldCenter();
            this.floorID = cell.getFloor().getUUID();
        }
    }
}
