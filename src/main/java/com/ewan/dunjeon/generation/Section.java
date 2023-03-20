package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ewan.dunjeon.game.Main.rand;

public class Section implements Serializable {
    List<Point> availableCells;
    List<WorldUtils.Side> wallBoundedSides;

    //Coords of leaf boundaries, including walls.
    int x1;
    int x2;
    int y1;
    int y2;

    List<GeneratorsMisc.Door> doors = new ArrayList<>();

    Section(int x1, int y1, int x2, int y2, List<WorldUtils.Side> boundedSides) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        availableCells = new ArrayList<>();
        for (int x = x1+1; x < x2; x++) {
            for (int y = y1+1; y < y2; y++){
                availableCells.add(new Point(x,y));
            }
        }
        Collections.shuffle(availableCells, rand);
        wallBoundedSides = boundedSides;
    }

    Section(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        availableCells = new ArrayList<>();
        wallBoundedSides = new ArrayList<>();
        for (int x = x1+1; x < x2; x++) {
            for (int y = y1+1; y < y2; y++){
                availableCells.add(new Point(x,y));
            }
        }
        Collections.shuffle(availableCells, rand);
        wallBoundedSides = new ArrayList<>();
        wallBoundedSides.add(WorldUtils.Side.EAST);
        wallBoundedSides.add(WorldUtils.Side.WEST);
        wallBoundedSides.add(WorldUtils.Side.SOUTH);
        wallBoundedSides.add(WorldUtils.Side.NORTH);
    }

    public Point getRandomEmptyPoint(){
        return availableCells.stream().findFirst().orElse(null);
    }

    public void remove(Point p){
        availableCells.remove(p);
    }

    //TODO Test me! I don't think this works...
//    public List<Pair<Integer, Integer>> getAvailableLocations(){
//        List<Pair<Integer, Integer>> locations = new ArrayList<>();
//        for (int x = x1+1; x < x2; x++) {
//            for (int y = y1+1; y < y2; y++) {
//                locations.add(new Pair<>(x, y));
//            }
//        }
//        for (Furniture furniture : assignedFurniture.keySet()) {
//            Pair<Integer, Integer> loc = assignedFurniture.get(furniture);
//            locations.remove(loc); //Specifically here seems sketchy...
//        }
//        return locations;
//    }

    int getWidth() {
        return x2 - x1;
    }

    int getHeight() {
        return y2 - y1;
    }

    /**
     * Returns a list of the points defining the walls of this section, each with an associated list of 'entry points' - adjacent tiles that are not walls. <p>
     * Will (probably) not work if two sections' walls are collinear or even touching
     * @param includeCorners whether this should return the corner walls or just 'side' walls
     * @return returns a list of pairings : Edge/CornerPiece <-> Adjacent entry points
     */
    List<Pair<Point, List<Point>>> getNonWallBoundedEdgePieces(boolean includeCorners) {
        List<Pair<Point, List<Point>>> points = new ArrayList<>();
        int cornerAdj = (includeCorners) ? 1 : 0;
        if(!wallBoundedSides.contains(WorldUtils.Side.NORTH)) {
            for (int i = x1 + 1; i < x2 + cornerAdj; i++) {
                List<Point> adjacentPoints = new ArrayList<>();
                adjacentPoints.add(new Point(i, y1 - 1));
                points.add(new Pair<>(new Point(i, y1), adjacentPoints));
            }
        }
        if(!wallBoundedSides.contains(WorldUtils.Side.EAST)) {
            for (int i = y1 + 1; i < y2 + cornerAdj; i++) {
                List<Point> adjacentPoints = new ArrayList<>();
                adjacentPoints.add(new Point(x2 + 1, i));
                points.add(new Pair<>(new Point(x2, i), adjacentPoints));
            }
        }
        if(!wallBoundedSides.contains(WorldUtils.Side.SOUTH)) {
            for (int i = x2 - 1; i > x1 - cornerAdj; i--) {
                List<Point> adjacentPoints = new ArrayList<>();
                adjacentPoints.add(new Point(i, y2 + 1));
                points.add(new Pair<>(new Point(i, y2), adjacentPoints));
            }
        }
        if(!wallBoundedSides.contains(WorldUtils.Side.WEST)) {
            for (int i = y2 - 1; i > y1 - cornerAdj; i--) {
                List<Point> adjacentPoints = new ArrayList<>();
                adjacentPoints.add(new Point(x1 - 1, i));
                points.add(new Pair<>(new Point(x1, i), adjacentPoints));
            }
        }
        return points;
    }


    //Generate a leaf that exists within this leaf, with some certain restrictions
    Section subLeaf() {

        //Calculating useful values
        int xPadding = 1;
        int yPadding = 1;
        int xBoundedRange = getWidth() - xPadding * 2;
        int yBoundedRange = getHeight() - yPadding * 2;

        //Local - Relative to the bounded range
        //Absolute - relative to global coords
        int newX1Local = (int) ((float) rand.nextInt(xBoundedRange) * 0.2f);
        int newY1Local = (int) ((float) rand.nextInt(yBoundedRange) * 0.2f);
        int newX1Abs = x1 + xPadding + newX1Local;
        int newY1Abs = y1 + yPadding + newY1Local;

        int minWidth = (int) (xBoundedRange * 0.8f);
        int minHeight = (int) (yBoundedRange * 0.8f);

        int newX2Local = (int) (newX1Local + minWidth + rand.nextInt(xBoundedRange - minWidth - newX1Local));
        int newY2Local = (int) (newY1Local + minHeight + rand.nextInt(yBoundedRange - minHeight - newY1Local));
        int newX2Abs = x1 + xPadding + newX2Local;
        int newY2Abs = y1 + yPadding + newY2Local;

        return new Section(newX1Abs, newY1Abs, newX2Abs, newY2Abs, wallBoundedSides);
    }

    Pair<Section[], GeneratorsMisc.Split> split(int minSize) {
        Section[] retSections = null;
        GeneratorsMisc.Split retSplit = null;
        float ratio = (float) getHeight() / (float) getWidth();

        //Force the split direction based on leaf ratio
        boolean horizontal = (ratio > 1);

        if (horizontal) {
            //Generate a y value between y1 and y2, but only possible in centered 30% (avoids tiny leafs)
            int ySplit = (int) (rand.nextInt((getHeight())) / 3f + y1 + getHeight() * (1f / 3f));
            List<WorldUtils.Side> section1Sides = new ArrayList(this.wallBoundedSides);
            List<WorldUtils.Side> section2Sides = new ArrayList(this.wallBoundedSides);
            section1Sides.remove(WorldUtils.Side.SOUTH);
            section2Sides.remove(WorldUtils.Side.NORTH);
            Section section1 = new Section(x1, y1, x2, ySplit - 1, section1Sides);
            Section section2 = new Section(x1, ySplit, x2, y2, section2Sides);

            retSplit = new GeneratorsMisc.Split(x1, x2, ySplit, ySplit);

            //Check that leaf is valid
            if (section1.getWidth() > minSize
                    && section2.getWidth() > minSize
                    && section1.getHeight() > minSize
                    && section2.getHeight() > minSize) {
                retSections = new Section[]{section1, section2};
            }
        } else {
            //Generate a x value between x1 and x2, but only possible in centered 30% (avoids tiny leafs)
            int xSplit = (int) (rand.nextInt((getWidth())) / 3f + x1 + getWidth() * (1f / 3f));

            List<WorldUtils.Side> section1Sides = new ArrayList(this.wallBoundedSides);
            List<WorldUtils.Side> section2Sides = new ArrayList(this.wallBoundedSides);
            section1Sides.remove(WorldUtils.Side.EAST);
            section2Sides.remove(WorldUtils.Side.WEST);

            Section section1 = new Section(x1, y1, xSplit - 1, y2, section1Sides);
            Section section2 = new Section(xSplit, y1, x2, y2, section2Sides);

            retSplit = new GeneratorsMisc.Split(xSplit, xSplit, y1, y2);

            //Check that leaf is valid
            if (section1.getWidth() > minSize
                    && section2.getWidth() > minSize
                    && section1.getHeight() > minSize
                    && section2.getHeight() > minSize) {
                retSections = new Section[]{section1, section2};
            }
        }
        return new Pair<Section[], GeneratorsMisc.Split>(retSections, retSplit);
    }

    @Override
    public String toString() {
        return "Section{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                '}' + "\n\t" + wallBoundedSides+"\n";
    }
}
