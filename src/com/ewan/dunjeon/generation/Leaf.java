package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ewan.dunjeon.generation.Main.rand;

public class Leaf implements Serializable {
    public void printDetails() {
        System.out.printf("(%d, %d) - (%d, %d)\n", x1, y1, x2, y2);
    }

    //Coords of leaf boundaries, including walls.
    int x1;
    int x2;
    int y1;
    int y2;

    List<GeneratorsMisc.Door> doors = new ArrayList<>();

    Leaf(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    int getWidth() {
        return x2 - x1;
    }

    int getHeight() {
        return y2 - y1;
    }

    List<Point> getEdgePieces(boolean includeCorners) {
        List<Point> points = new ArrayList<>();
        int cornerAdj = (includeCorners) ? 1 : 0;
        for (int i = x1 + 1; i < x2 + cornerAdj; i++) {
            points.add(new Point(i, y1));
        }
        for (int i = y1 + 1; i < y2 + cornerAdj; i++) {
            points.add(new Point(x2, i));
        }
        for (int i = x2 - 1; i > x1 - cornerAdj; i--) {
            points.add(new Point(i, y2));
        }
        for (int i = y2 - 1; i > y1 - cornerAdj; i--) {
            points.add(new Point(x1, i));
        }
        return points;
    }

    //Generate a leaf that exists within this leaf, with some certain restrictions
    Leaf subLeaf() {

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

        return new Leaf(newX1Abs, newY1Abs, newX2Abs, newY2Abs);
    }

    Leaf[] split(int minSize) {
        Leaf[] retVal = null;
        float ratio = (float) getHeight() / (float) getWidth();

        //Force the split direction based on leaf ratio
        boolean horizontal = (ratio > 1);

        if (horizontal) {
            //Generate a y value between y1 and y2, but only possible in centered 30% (avoids tiny leafs)
            int ySplit = (int) (rand.nextInt((getHeight())) / 3f + y1 + getHeight() * (1f / 3f));
            Leaf leaf1 = new Leaf(x1, y1, x2, ySplit);
            Leaf leaf2 = new Leaf(x1, ySplit + 1, x2, y2);

            //Check that leaf is valid
            if (leaf1.getWidth() > minSize
                    && leaf2.getWidth() > minSize
                    && leaf1.getHeight() > minSize
                    && leaf2.getHeight() > minSize) {
                retVal = new Leaf[]{leaf1, leaf2};
            }
        } else {
            //Generate a x value between x1 and x2, but only possible in centered 30% (avoids tiny leafs)
            int xSplit = (int) (rand.nextInt((getWidth())) / 3f + x1 + getWidth() * (1f / 3f));
            Leaf leaf1 = new Leaf(x1, y1, xSplit, y2);
            Leaf leaf2 = new Leaf(xSplit + 1, y1, x2, y2);

            //Check that leaf is valid
            if (leaf1.getWidth() > minSize
                    && leaf2.getWidth() > minSize
                    && leaf1.getHeight() > minSize
                    && leaf2.getHeight() > minSize) {
                retVal = new Leaf[]{leaf1, leaf2};
            }
        }
        return retVal;
    }

}
