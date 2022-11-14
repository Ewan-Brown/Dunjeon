package com.ewan.dunjeon.generation;


import com.ewan.dunjeon.world.Pair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PathFinding {


    public static List<Point> getAStarPath(float[][] primitiveWeightMap, Point startNode, Point targetNode, boolean print, CornerInclusionRule cornerRule, float weightAgainstTurning, boolean includeStartNodeInPath){

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
            openNodes.sort((t1, t2) -> {
                Float f1 = getVal(fMap, t1);
                Float f2 = getVal(fMap, t2);
                return f1.compareTo(f2);
            });
            Point currentNode = openNodes.get(0);

            if(print) System.out.println("[Node Loop] Current node : " + currentNode.toString() );
            openNodes.remove(0);
            closedNodes.add(currentNode);
            List<Pair<Point, Boolean>>  neighbors = getAdjacent(currentNode, width, height, cornerRule, weightMap);
            for (Pair<Point, Boolean> successorPair : neighbors) {
                Point successor = successorPair.getElement0();
                if(print) System.out.printf("Checking Neighbor (%d, %d)\n", successor.x, successor.y);
                if (successor.equals(targetNode)) {
                    setVal(prevNodeMap, successor, currentNode);
                    if(print) System.out.println("FOUND THE END!");
                    break outerLoop;
                }

                if (closedNodes.stream().anyMatch(successor::equals)) {
                    if (print) System.out.println("This neighbor is already on the closed list - skipping");
                    continue; //Skip this node if it's on the closed list
                }


                //Calculate next G. equal to last cell's G + cost to move to this cell.
                //Note that cost is multiplied by root of 2 (1.41 if diagonal movement!)
                float successorG = getVal(gMap, currentNode) +
                        getVal(weightMap, successor) * ((successorPair.getElement1()) ? 1.41f : 1f);
                float successorH = (Math.abs(targetNode.x - successor.x) + Math.abs(targetNode.y - successor.y)) / 10f; //FIXME Why is this ALWAYS Manhattan distance? FOr AI Too?


                Float currentAngleObj = getVal(prevDirMap, currentNode);
                float successorAngle = (float)Math.atan2(successor.y - currentNode.y, successor.x - currentNode.x);
                if(currentAngleObj != null) {
                    if(Math.abs(currentAngleObj-successorAngle) > 0.001){ //FIXME Check if rounded floats are equivalent... Sketchy but will work
                        successorG+=weightAgainstTurning;
                    }
                }

                float successorF = successorG + successorH;
                if(print) System.out.println("successorG = " + successorG);
                if(successorG == Float.POSITIVE_INFINITY) continue; //Skip cells that are infinite weight

                //If this successor point is NOT on the open list
                //                           OR it IS on the open list but the new F is lower than the existing F
                //Then proceed
                if (openNodes.stream().noneMatch(successor::equals) || successorF < getVal(fMap, currentNode)) {
                    openNodes.removeIf(successor::equals); //Remove this point if it exists on the open list
                    openNodes.add(successor);
                    setVal(prevNodeMap, successor, currentNode);
                    setVal(prevDirMap, successor, successorAngle);
                    if (print) System.out.println("Added to open list!");
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
            Point currentPoint = targetNode;
            while(true){

                foundPath.add(currentPoint);
                currentPoint = getVal(prevNodeMap, currentPoint);
                if(startNode.equals(currentPoint)){
                    if(includeStartNodeInPath){
                        foundPath.add(currentPoint);
                    }
                    break;
                }
            }
            return foundPath;
        }
    }

    public enum CornerInclusionRule{
        NO_CORNERS(0),
        NON_CLIPPING_CORNERS(0),
        SEMI_CLIPPING_CORNERS(1),
        ALL_CORNERS(2);

        private final int clippingCornersAllowed;

        CornerInclusionRule(int c){
            clippingCornersAllowed = c;
        }
    }


    private static <T> T getVal(T[][] map, Point p){
        return map[p.y][p.x];
    }

    private static <T> void setVal(T[][] map, Point p, T val){
        map[p.y][p.x] = val;
    }

    public static List<Pair<Point, Boolean>> getAdjacent(Point currentNode, int width, int height, CornerInclusionRule cornerRule, Float[][] weightMap){
        List<Pair<Point, Boolean>> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; //
                int x = currentNode.x + j;
                int y = currentNode.y + i;
                if (x < 0 || y < 0 || x >= width || y >= height)  continue;
                if((i != 0 && j != 0)) {
                    if(cornerRule == CornerInclusionRule.NO_CORNERS) continue;
                    //Is corner
                    //Points representing the tiles that are clipping, need to check if these are 'blocked'
                    boolean side1 = weightMap[y][currentNode.x] == Float.POSITIVE_INFINITY;
                    boolean side2 = weightMap[currentNode.y][x] == Float.POSITIVE_INFINITY;
                    int clippingCount = ((side1) ? 1 : 0) + ((side2) ? 1 : 0);
                    if(clippingCount <= cornerRule.clippingCornersAllowed){
                        neighbors.add(new Pair<>(new Point(x, y), true));
                    }
                }else{
                    //Is not corner
                    neighbors.add(new Pair<>(new Point(x, y), false));
                }
            }
        }
        return neighbors;
    }
}//            List<Pair<Point, Boolean>>  neighbors = getAdjacent(currentNode, width, height, diagonalsForAI, diagonalsForAI);
