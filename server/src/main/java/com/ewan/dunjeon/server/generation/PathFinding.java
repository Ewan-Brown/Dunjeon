package com.ewan.dunjeon.server.generation;


import com.ewan.dunjeon.server.world.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * I'm afraid to delete this... a bit of an artifact
 */
public class PathFinding {

    static Logger logger = LogManager.getLogger();

    public static List<Point> getAStarPath(Function<Vector2, Double> weightProvider, Point startNode, Point targetNode, Point lowPoint, Point highPoint, boolean print, CornerInclusionRule cornerRule, double weightAgainstTurning, boolean includeStartNodeInPath){

        int outerCount = 0;
        int innerCount = 0;
        int secondCount = 0;

        int height = highPoint.y - lowPoint.y;
        int width = highPoint.x - lowPoint.x;

//        Double[][] weightMap = new Double[height][width];
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                weightMap[y][x] = primitiveWeightMap[y][x];
//            }
//        }

        List<Point> openNodes = new ArrayList<>();
        List<Point> closedNodes = new ArrayList<>();

        //Heuristic, G and F from A* algorithm
        Double[][] hMap = new Double[height][width];
        Double[][] gMap = new Double[height][width];
        Double[][] fMap = new Double[height][width];

        //Map for each node showing where the 'previous' node was if it is on the Closed List
        Point[][] prevNodeMap = new Point[height][width];

        //Map for each node on the closed list showing the angle that was used to enter it.
        Double[][] prevDirMap = new Double[height][width];


        int shortestDistSoFar = Integer.MAX_VALUE;

        //Set the starting heuristic to 0
        setVal(gMap, startNode, 0.0d);
        setVal(hMap, startNode, 0.0d);
        setVal(fMap, startNode, 0.0d);
        setVal(prevDirMap, startNode, null);
        openNodes.add(startNode);

        if(print) logger.info("Starting node looping");
        outerLoop:
        while (!openNodes.isEmpty()){
            outerCount++;
            openNodes.sort((t1, t2) -> {
                Double f1 = getVal(fMap, t1);
                Double f2 = getVal(fMap, t2);
                return f1.compareTo(f2);
            });
            Point currentNode = openNodes.get(0);

            openNodes.remove(0);
            closedNodes.add(currentNode);
            List<Pair<Point, Boolean>>  neighbors = getAdjacent(currentNode, width, height, cornerRule, weightProvider);
            for (Pair<Point, Boolean> successorPair : neighbors) {
                innerCount++;
                Point successor = successorPair.getElement0();
                if (successor.equals(targetNode)) {
                    setVal(prevNodeMap, successor, currentNode);
                    break outerLoop;
                }

                if (closedNodes.stream().anyMatch(successor::equals)) {
                    continue; //Skip this node if it's on the closed list
                }


                //Calculate next G. equal to last cell's G + cost to move to this cell.
                //Note that cost is multiplied by sqrt of 2 if diagonal movement!
                double successorG = getVal(gMap, currentNode) +
                        weightProvider.apply(new Vector2(successor.x, successor.y)) * ((successorPair.getElement1()) ? 1.41f : 1f);
                double successorH = (Math.abs(targetNode.x - successor.x) + Math.abs(targetNode.y - successor.y)) / 10f; //FIXME Is manhattan distance really appropriate here


                Double currentAngleObj = getVal(prevDirMap, currentNode);
                double successorAngle = Math.atan2(successor.y - currentNode.y, successor.x - currentNode.x);
                if(currentAngleObj != null) {
                    if(Math.abs(currentAngleObj-successorAngle) > 0.001){ //FIXME Check if rounded doubles are equivalent... Sketchy but will work
                        successorG+=weightAgainstTurning;
                    }
                }

                double successorF = successorG + successorH;
                if(successorG == Float.POSITIVE_INFINITY) continue; //Skip cells that are infinite weight

                //If this successor point is NOT on the open list
                //                           OR it IS on the open list but the new F is lower than the existing F
                //Then proceed
                if (openNodes.stream().noneMatch(successor::equals) || successorF < getVal(fMap, currentNode)) {
                    openNodes.removeIf(successor::equals); //Remove this point if it exists on the open list
                    openNodes.add(successor);
                    setVal(prevNodeMap, successor, currentNode);
                    setVal(prevDirMap, successor, successorAngle);
                    setVal(hMap, successor, successorH);
                    setVal(gMap, successor, successorG);
                    setVal(fMap, successor, successorF);
                }

            }
        }

        if(getVal(prevNodeMap, targetNode) == null){
            return null;
        }else{
            secondCount++;
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

            if(print) {
                logger.info("\t\tinnerCount " + innerCount);
                logger.info("\t\touterCount " + outerCount);
                logger.info("\t\tsecondCount " + secondCount);
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

    public static List<Pair<Point, Boolean>> getAdjacent(Point currentNode, int width, int height, CornerInclusionRule cornerRule, Function<Vector2, Double> weightProvider){
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
                    boolean side1 = weightProvider.apply(new Vector2(currentNode.x, y)) == Double.POSITIVE_INFINITY;
                    boolean side2 = weightProvider.apply(new Vector2(currentNode.y, x)) == Double.POSITIVE_INFINITY;
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
