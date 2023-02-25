package com.ewan.dunjeon.generation;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static com.ewan.dunjeon.game.Main.rand;

public class GeneratorsMisc {

    public static final int BLOCK = 0;
    public static final int OPEN = 1;
    public static final int DOOR = 2;
    public static final int WALL = 3;
    public static final int HALL = 4;

    public static Path crashDataLocation = Paths.get("C:\\Users\\Ewan\\Documents\\Programming\\Dunjeon\\crashFiles");

    public static final int doorSpacing = 2;

    public static int[][] generateRandomMap(int width, int height, float percentOpen){
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
        Door d1;
        Door d2;
        List<Point> points;

        public Hall(Door d1, Door d2, List<Point> points) {
            this.d1 = d1;
            this.d2 = d2;
            d1.directConnections.add(d2);
            d2.directConnections.add(d1);
            d1.hallsConnected.add(this);
            d2.hallsConnected.add(this);
            this.points = points;
        }
    }

    public static class Door implements Serializable{
        int x;
        int y;
        List<Door> directConnections = new ArrayList<>();
        List<Hall> hallsConnected = new ArrayList<>();
        List<Point> entryPoints = new ArrayList<>();
        Section parentSection;

        public Door(Point p, Section l, List<Point> e){
            this.x = p.x;
            this.y = p.y;
            parentSection = l;
            entryPoints = e;
        }

        public Point getPoint(){
            return new Point(x, y);
        }
    }

}
