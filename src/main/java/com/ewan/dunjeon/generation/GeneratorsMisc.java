package com.ewan.dunjeon.generation;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static com.ewan.dunjeon.game.Main.rand;

public class GeneratorsMisc {

    public static final int BLOCK = 0;
    public static final int OPEN = 1;
    public static final int DOOR = 2;
    public static final int WALL = 3;
    public static final int HALL = 4;

    public static Path crashDataLocation = Paths.get("C:\\Users\\Ewan\\Documents\\Programming\\Dunjeon\\crashFiles");

    public static final int doorSpacing = 2;

    public static int[][] generateRandomMap(int width, int height, double percentOpen){
        int[][] map = generateFullMap(height, width, BLOCK);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[j][i] = (rand.nextFloat() < percentOpen) ? OPEN : BLOCK;
            }
        }
        return map;
    }

    public static int[][] generateFullMap(int width, int height, int value){
        int[][] map = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[j][i] = value;
            }
        }

        return map;
    }

    public static class Hall implements Serializable{
        List<Junction> junctions;

        public int x1, y1, x2, y2;

        public Hall(Junction j1, Junction j2) {
            junctions = new ArrayList<>();

            junctions.add(j1);
            j1.connectedHalls.add(this);
            junctions.add(j2);
            j2.connectedHalls.add(this);
            j1.connectedJunctions.add(j2);
            j2.connectedJunctions.add(j1);

            x1 = junctions.stream().mapToInt(value -> value.x1).min().getAsInt();
            x2 = junctions.stream().mapToInt(value -> value.x2).max().getAsInt();

            y1 = junctions.stream().mapToInt(value -> value.y1).min().getAsInt();
            y2 = junctions.stream().mapToInt(value -> value.y2).max().getAsInt();
        }

        public Hall(Junction j1, int Tx1, int Ty1, int Tx2, int Ty2, int xLimit, int yLimit) {
            junctions = new ArrayList<>();

            junctions.add(j1);
            j1.connectedHalls.add(this);
            int minX = Stream.of(j1.x1,Tx1,Tx2).min(Comparator.comparingInt(o -> o)).get();
            int maxX = Stream.of(j1.x2,Tx1,Tx2).max(Comparator.comparingInt(o -> o)).get();
            int minY = Stream.of(j1.y1,Ty1,Ty2).min(Comparator.comparingInt(o -> o)).get();
            int maxY = Stream.of(j1.y2,Ty1,Ty2).max(Comparator.comparingInt(o -> o)).get();

            minX = Integer.max(0, minX);
            maxX = Integer.min(maxX, xLimit);
            minY = Integer.max(0, minY);
            maxY = Integer.min(maxY, yLimit);


            x1 = minX;
            x2 = maxX;
            y1 = minY;
            y2 = maxY;

        }
    }

    public static class Junction{
        public int x1, x2, y1, y2;
        List<Junction> connectedJunctions;
        List<Hall> connectedHalls;

        public Junction(int x1, int y1, int x2, int y2) {
            this.x1 = Integer.min(x1, x2);
            this.x2 = Integer.max(x1, x2);
            this.y1 = Integer.min(y1, y2);
            this.y2 = Integer.max(y1, y2);
            connectedJunctions = new ArrayList<>();
            connectedHalls = new ArrayList<>();
        }

        public boolean containsPoint(Point p){
            return (p.x <= x2 && p.x >= x2 && p.y <= y2 && p.y >= y1);
        }
    }

    public static class Door implements Serializable{
        int x;
        int y;
        List<Door> directConnections;
        List<Hall> hallsConnected;
        List<Point> entryPoints;
        Section parentSection;

        public Door(Point p, Section l, List<Point> e){
            this.x = p.x;
            this.y = p.y;
            parentSection = l;
            entryPoints = e;
            hallsConnected = new ArrayList<>();
            directConnections = new ArrayList<>();
        }

        public Point getPoint(){
            return new Point(x, y);
        }
    }

    public static class SplitLine {
        int x1,x2,y1,y2;

        public SplitLine(int x1, int x2, int y1, int y2){
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        @Override
        public String toString() {
            return String.format("%f, %f -> %f, %f", x1, y1, x2, y2);
        }

        public Line2D.Double getLine2D(){
            return new Line2D.Double(x1, y1, x2, y2);
        }

        public int getX1() {
            return x1;
        }

        public int getX2() {
            return x2;
        }

        public int getY1() {
            return y1;
        }

        public int getY2() {
            return y2;
        }
    }

}
