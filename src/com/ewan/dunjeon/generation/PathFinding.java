package com.ewan.dunjeon.generation;


import com.ewan.dunjeon.world.Pair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PathFinding {

    //Just a visual test
//    public static void main(String[] args) {
//        int width = 10;
//        int height = 10;
//        float[][] floatArray = new float[height][width];
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                floatArray[i][j] = (float)Math.random();
//                System.out.print((int)(floatArray[i][j] * 10));
//            }
//            System.out.println();
//        }
//
//
//        List<Point> shortPath = getAStarPath(floatArray, new Point(0,0), new Point(width-1,height-1), false);
//        System.out.println(shortPath);
//    }

    public static List<Point> getAStarPath(float[][] primitiveWeightMap, Point startNode, Point targetNode, boolean print){

        if(print) {
            System.out.println("Starting pathfinding...");
            System.out.printf("Going from (%d, %d) to (%d, %d)\n", startNode.x, startNode.y, targetNode.x, targetNode.y);
        }
        int height = primitiveWeightMap.length;
        int width = primitiveWeightMap[0].length;

        Float[][] weightMap = new Float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                weightMap[y][x] = primitiveWeightMap[y][x];
            }
        }

        List<Point> openNodes = new ArrayList<>();
        List<Point> closedNodes = new ArrayList<>();

        //Heuristic, G and F from A* algorithm
        Float[][] hMap = new Float[height][width];
        Float[][] gMap = new Float[height][width];
        Float[][] fMap = new Float[height][width];

        //Map for each node showing where the 'previous' node was if it is on the Closed List
        Point[][] prevNodeMap = new Point[height][width];

        //Map for each node on the closed list showing the angle that was used to enter it.
        Float[][] prevDirMap = new Float[height][width];


        int shortestDistSoFar = Integer.MAX_VALUE;

        //Set the starting heuristic to 0
        setVal(gMap, startNode, 0.0f);
        setVal(hMap, startNode, 0.0f);
        setVal(fMap, startNode, 0.0f);
        setVal(prevDirMap, startNode, null);
        openNodes.add(startNode);

        if(print) System.out.println("Starting node looping");
        outerLoop:
        while (!openNodes.isEmpty()){
            if(print) System.out.println("Node Loop");
            openNodes.sort((t1, t2) -> {
                Float f1 = getVal(fMap, t1);
                Float f2 = getVal(fMap, t2);
                return f1.compareTo(f2);
            });
            Point currentNode = openNodes.get(0);
            openNodes.remove(0);
            closedNodes.add(currentNode);
            List<Pair<Point, Boolean>>  neighbors = getAdjacent(currentNode, width, height, false);

            for (Pair<Point, Boolean> successorPair : neighbors) {
                Point successor = successorPair.getElement0();
                if(print) System.out.printf("Checking Neighbor (%d, %d)\n", successor.x, successor.y);
                if (successor.equals(targetNode)) {
                    //WE FOUND THE END!
                    setVal(prevNodeMap, successor, currentNode);
                   if(print) System.out.println("FOUND THE END!");
                    break outerLoop;
                }

                if (closedNodes.stream().anyMatch(successor::equals)) {
//                    System.out.println("This neighbor is already on the closed list - skipping");
                    continue; //Skip this node if it's on the closed list
                }


                //Calculate next G. equal to last cell's G + cost to move to this cell.
                //Note that cost is multiplied by root of 2 (1.41 if diagonal movement!)
                float successorG = getVal(gMap, currentNode) +
                        getVal(weightMap, successor) * ((successorPair.getElement1()) ? 1.41f : 1f);
                float successorH = Math.abs(targetNode.x - successor.x) + Math.abs(targetNode.y - successor.y); //Manhattan distance
                float successorF = successorG + successorH;
                if(print) System.out.println("successorG = " + successorG);
                if(successorG == Float.POSITIVE_INFINITY) continue; //Skip cells that are infinite weight

                Object tempObj = getVal(prevDirMap, currentNode);
                float successorAngle = (float)Math.atan2(successor.y - currentNode.y, successor.x - currentNode.x);
                if(tempObj != null) {
                    float currentAngle = (Float) tempObj;
                    if(Math.abs(currentAngle-successorAngle) > 0.001){ //FIXME Check if rounded floats are equivalent... Sketchy but will work
                        successorG+=1;
                    }
                }


//                if (!openNodes.stream().anyMatch(successor::equals)) System.out.println("Neighbor Not on open list");
//                System.out.println("Neighbor F - " + successorF);
//                System.out.println("Existing F - " + getVal(fMap, currentNode));
                //If this successor point is NOT on the open list
                //                           OR it IS on the open list but the new F is lower than the existing F
                //Then proceed
                if (!openNodes.stream().anyMatch(successor::equals) || successorF < getVal(fMap, currentNode)) {
                    openNodes.removeIf(successor::equals); //Remove this point if it exists on the open list
                    openNodes.add(successor);
                    setVal(prevNodeMap, successor, currentNode);
                    setVal(prevDirMap, successor, successorAngle);
//                    System.out.println("Added to open list!");
                    setVal(hMap, successor, successorH);
                    setVal(gMap, successor, successorG);
                    setVal(fMap, successor, successorF);
                }

            }
        }

        if(getVal(prevNodeMap, targetNode) == null){
            System.err.println("No path found :(");
            return null;
        }else{
            List<Point> foundPath = new ArrayList<>();
            Point nextPoint = targetNode;
            while(!startNode.equals(nextPoint)){
                foundPath.add(nextPoint);
                nextPoint = getVal(prevNodeMap, nextPoint);
            }
            return foundPath;
        }
    }

    /**
     * Returns a list of Pairs, containing Points and Booleans.
     * Boolean represents whether that point was attained as a diagonal or not
     * @param p
     * @param width
     * @param height
     * @param includeCorners
     * @return
     */
    private static List<Pair<Point, Boolean>> getAdjacent(Point p, int width, int height, boolean includeCorners){
        List<Pair<Point, Boolean>> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; //
                if(includeCorners  || (i == 0 || j == 0)) { //Only do side pieces, ignore corners
                    int x = p.x + j;
                    int y = p.y + i;
                    if (x < 0 || y < 0 || x >= width || y >= height) {
                        continue;
                    } else {
                        neighbors.add(new Pair<>(new Point(x, y), i == 0 && j == 0));
                    }
                }
            }
        }

        return neighbors;
    }

    private static <T> T getVal(T[][] map, Point p){
        return map[p.y][p.x];
    }

    private static <T> void setVal(T[][] map, Point p, T val){
        map[p.y][p.x] = val;
    }
}
