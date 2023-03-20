package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.lang.Math.abs;


public class WorldUtils {

    public static final float ENTITY_WITHIN_TILE_THRESHOLD = 0.5f;
    private static final float INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD = 0.0001f;

    //TODO Reduce these after by making a 'HasPosition' interface?
    public static float getRawDistance(Entity e1, Entity e2){
        return getRawDistance(e1.getPosX(), e2.getPosX(), e1.getPosY(), e2.getPosY());
    }
    public static float getRawDistance(BasicCell c1, BasicCell c2){
        return getRawDistance(c1.getX(), c2.getX(), c1.getY(), c2.getY());
    }

    public static float getRawDistance(BasicCell c, Entity e){
        return getRawDistance(c.getX(), e.getPosX(), c.getY(), e.getPosY());
    }
    public static float getRawDistance(Furniture f, Entity e){
        return getRawDistance(f.getPosX(), e.getPosX(), f.getPosY(), e.getPosY());
    }
    public static float getRawDistance(Interactable f, Entity e){
        return getRawDistance(f.getPosX(), e.getPosX(), f.getPosY(), e.getPosY());
    }

    public static float getRawDistance(float x1, float x2, float y1, float y2){
        return (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    public static boolean isAdjacent(BasicCell b1, BasicCell b2){
        if(b1 == b2){
            throw new IllegalArgumentException();
        }else {
            return isAdjacent(b1.getX(), b1.getY(), b2.getX(), b2.getY());
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
    public static List<Pair<Point, Side>> getIntersectedTilesWithWall(float x1, float y1, float x2, float y2) {

        float dx = x2 - x1;
        float dy = y2 - y1;



        float slope = dy / dx;
        float b = y1 - slope * x1;

        float currentX = x1;
        float currentY = y1;

        List<Pair<Point, Side>> intersectedTiles = new ArrayList<>();
        Point currentPoint = new Point((int) Math.floor(x1), (int) Math.floor(y1));

        if(dx == 0){
//            System.out.println("Here we vertically go");
            int tileX = (int)Math.floor(x1);
//            System.out.println("tileX = " + tileX);
            Side intersectSide = dy > 0 ? Side.NORTH : Side.SOUTH;
//            System.out.println("intersectSide = " + intersectSide);
            int y1Floored = (int)Math.floor(y1);
            int y2Ceil = (int)Math.floor(y2)+1;
//            System.out.printf("y1(floor) : %f(%d), y2(ceil) : %f(%d)\n", y1, y1Floored, y2, y2Ceil);
            IntStream.range(y1Floored, y2Ceil).forEach(new IntConsumer() {
                @Override
                public void accept(int value) {
                    intersectedTiles.add(new Pair<>(new Point(tileX, value), intersectSide));
                }
            });
//            System.out.println(intersectedTiles.size());
            return intersectedTiles;
        }else if(dy == 0){
//            System.out.println();
//            System.out.println("Here we horizontally go");
            int tileY = (int)Math.floor(y1);
//            System.out.println("tileY = " + tileY);
            Side intersectSide = dx > 0 ? Side.WEST : Side.EAST;
//            System.out.println("intersectSide = " + intersectSide);
            int x1Floored = (int)Math.floor(x1);
            int x2Ceil = (int)Math.floor(x2)+1;
//            System.out.printf("x1(floor) : %f(%d), x2(ceil) : %f(%d)\n", x1, x1Floored, x2, x2Ceil);
            IntStream.range(x1Floored, x2Ceil).forEach(new IntConsumer() {
                @Override
                public void accept(int value) {
                    intersectedTiles.add(new Pair<>(new Point(value, tileY), intersectSide));
                }
            });
//            System.out.println(intersectedTiles.size());
            return intersectedTiles;
        }

        intersectedTiles.add(new Pair<>(currentPoint, Side.WITHIN));



        //Iterate across intersects and find tiles
        while (true) {

            float nextVerticalIntersect = 0;
            float distToNextVerticalIntersect = Float.MAX_VALUE;
            float nextHorizontalIntersect = 0;
            float distToNextHorizontalIntersect = Float.MAX_VALUE;

            Side side;

            if (dx != 0) {
                if (currentX == Math.round(currentX)) {
                    nextVerticalIntersect = currentX + Math.signum(dx);

                } else {
                    nextVerticalIntersect = (float) ((dx > 0) ? Math.ceil(currentX) : Math.floor(currentX));
                }
                distToNextVerticalIntersect = (nextVerticalIntersect - currentX) / dx;
            }
            if (dy != 0) {
                if (currentY == Math.round(currentY)) {
                    nextHorizontalIntersect = currentY + Math.signum(dy);
                } else {
                    nextHorizontalIntersect = (float) ((dy > 0) ? Math.ceil(currentY) : Math.floor(currentY));
                }
                distToNextHorizontalIntersect = (nextHorizontalIntersect - currentY) / dy;
            }

            float nextInterceptX, nextInterceptY;

            Creature.AxisAlignment intersectAlignment;

            if (distToNextVerticalIntersect < distToNextHorizontalIntersect) {
                nextInterceptX = nextVerticalIntersect;
                nextInterceptY = slope * nextInterceptX + b;

                if(distToNextVerticalIntersect < INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD){
                    float delta = INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD * Math.signum(dy);
                    nextInterceptY = Math.round(nextInterceptY) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that floating point errors come into play in the result of y = mx+b
                }
                intersectAlignment = Creature.AxisAlignment.VERTICAL;
                side = (dx > 0) ? Side.WEST : Side.EAST;

            } else {
                nextInterceptY = nextHorizontalIntersect;
                nextInterceptX = (nextInterceptY - b) / slope;

                if(distToNextHorizontalIntersect < INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD){
                    float delta = INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD * Math.signum(dx);
                    nextInterceptX = Math.round(nextInterceptX) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that floating point errors come into play in the result of y = mx+b
                }
                intersectAlignment = Creature.AxisAlignment.HORIZONTAL;
                side = (dy > 0) ? Side.NORTH :Side.SOUTH;
            }
            if (Math.abs(nextInterceptX - x1) > Math.abs(dx) || Math.abs(nextInterceptY - y1) > Math.abs(dy) ) {
                break;
            } else {
                int nextTileX, nextTileY;

                if (intersectAlignment == Creature.AxisAlignment.VERTICAL) {
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
                Pair<Point, Side> pair = new Pair<>(new Point(nextTileX, nextTileY), side);
                intersectedTiles.add(pair);
                currentX = nextInterceptX;
                currentY = nextInterceptY;
            }
        }
        return intersectedTiles;
    }

}
