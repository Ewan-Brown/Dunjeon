package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.lang.Math.abs;


public class WorldUtils {

    public static final double ENTITY_WITHIN_TILE_THRESHOLD = 0.5f;
    private static final double INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD = 0.0001f;

    //TODO Reduce these after by making a 'HasPosition' interface?
    public static double getRawDistance(Entity e1, Entity e2){
        return getRawDistance(e1.getWorldCenter().x, e2.getWorldCenter().x, e1.getWorldCenter().y, e2.getWorldCenter().y);
    }
    public static double getRawDistance(BasicCell c1, BasicCell c2){
        return getRawDistance(c1.getX(), c2.getX(), c1.getY(), c2.getY());
    }

    public static double getRawDistance(BasicCell c, Entity e){
        return getRawDistance(c.getX(), e.getWorldCenter().x, c.getY(), e.getWorldCenter().y);
    }
//    public static double getRawDistance(Interactable f, Entity e){
//        return getRawDistance(f.getWorldCenter().x, e.getWorldCenter().x, f.getWorldCenter().y, e.getWorldCenter().y);
//    }

    public static double getRawDistance(double x1, double x2, double y1, double y2){
        return (double)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
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


    public enum AxisAlignment {
        VERTICAL,
        HORIZONTAL,
        DIAGONAL
    }

    public static List<Pair<Point, Side>> getIntersectedTilesWithWall(double x1, double y1, double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;



        double slope = dy / dx;
        double b = y1 - slope * x1;

        double currentX = x1;
        double currentY = y1;

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
                    nextInterceptY = Math.round(nextInterceptY) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that doubleing point errors come into play in the result of y = mx+b
                }
                intersectAlignment = AxisAlignment.VERTICAL;
                side = (dx > 0) ? Side.WEST : Side.EAST;

            } else {
                nextInterceptY = nextHorizontalIntersect;
                nextInterceptX = (nextInterceptY - b) / slope;

                if(distToNextHorizontalIntersect < INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD){
                    double delta = INTERSECTION_FLOATING_POINT_NUDGE_THRESHOLD * Math.signum(dx);
                    nextInterceptX = Math.round(nextInterceptX) + delta; //The reason for this is to 'nudge' the intersection point if its so close to zero that doubleing point errors come into play in the result of y = mx+b
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
                Pair<Point, Side> pair = new Pair<>(new Point(nextTileX, nextTileY), side);
                intersectedTiles.add(pair);
                currentX = nextInterceptX;
                currentY = nextInterceptY;
            }
        }
        return intersectedTiles;
    }

}
