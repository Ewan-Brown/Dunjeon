package com.ewan.dunjeon.world;

import com.ewan.dunjeon.generation.Main;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;


public class WorldUtils {
    public static float getRawDistance(BasicCell c1, BasicCell c2){
        return (float)Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2) + Math.pow(c1.getY() - c2.getY(), 2));
    }
    public static boolean raytrace(BasicCell c1, BasicCell c2)
    {
        return true;
    }

    /**
     * Taken from http://playtechs.blogspot.com/2007/03/raytracing-on-grid.html
//     */
//    public static List<Point> raytraceCalc(int x0, int y0, int x1, int y1)
//    {
//        List<Point> points = new ArrayList<>();
//        int dx = abs(x1 - x0);
//        int dy = abs(y1 - y0);
//        int x = x0;
//        int y = y0;
//        int n = 1 + dx + dy;
//        int x_inc = (x1 > x0) ? 1 : -1;
//        int y_inc = (y1 > y0) ? 1 : -1;
//        int error = dx - dy;
//        dx *= 2;
//        dy *= 2;
//
//        for (; n > 0; --n)
//        {
//            points.add(new Point(x, y));
//            if (error > 0)
//            {
//                x += x_inc;
//                error -= dy;
//            }
//            else
//            {
//                y += y_inc;
//                error += dx;
//            }
//        }
//        return points;
//    }

    public static List<BasicCell> getAStarPath(Floor l, BasicCell source, BasicCell target, Entity mover, boolean print){
        if(print) {
            System.out.println("\tStarting pathfinding...");
            System.out.printf("\tGoing from (%d, %d) to (%d, %d)\n", source.getX(), source.getY(), target.getX(), target.getY());
        }
        BasicCell[][] map = l.getCells();
        int height = map.length;
        int width = map[0].length;

        Float[][] weightMap = new Float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BasicCell c = map[y][x];
                float weight = 0;
                if(!c.canBeEntered(mover)){
                    if(print) {
                        System.out.printf("\tCan't be entered : (%d, %d)\n", x, y);
                    }
                    weight = Float.POSITIVE_INFINITY;
                }else{
                    weight = 1 + Main.rand.nextFloat();
                }
                weightMap[y][x] = weight;
            }
        }

        List<BasicCell> openNodes = new ArrayList<>();
        List<BasicCell> closedNodes = new ArrayList<>();

        //Heuristic, G and F from A* algorithm
        Float[][] hMap = new Float[height][width];
        Float[][] gMap = new Float[height][width];
        Float[][] fMap = new Float[height][width];

        //Map for each node showing where the 'previous' node was if it is on the Closed List
        BasicCell[][] prevNodeMap = new BasicCell[height][width];

        //Map for each node on the closed list showing the angle that was used to enter it.
        Float[][] prevDirMap = new Float[height][width];


        int shortestDistSoFar = Integer.MAX_VALUE;

        //Set the starting heuristic to 0
        setVal(gMap, source, 0.0f);
        setVal(hMap, source, 0.0f);
        setVal(fMap, source, 0.0f);
        setVal(prevDirMap, source, null);
        openNodes.add(source);

        if(print) System.out.println("\tStarting node looping");
        outerLoop:
        while (!openNodes.isEmpty()){
            if(print) System.out.println("\tNode Loop");
            openNodes.sort((t1, t2) -> (getVal(fMap, t1) > getVal(fMap, t2)) ? 1 : -1);
            BasicCell currentNode = openNodes.get(0);
            openNodes.remove(0);
            closedNodes.add(currentNode);
            List<Pair<BasicCell, Boolean>> neighbors = getAdjacent(currentNode, map, true);

            for (Pair<BasicCell, Boolean> successorPair : neighbors) {
                BasicCell successor = successorPair.getElement0();
                if(print) System.out.printf("\tChecking Neighbor (%d, %d)\n", successor.getX(), successor.getY());
                if (successor.equals(target)) {
                    //WE FOUND THE END!
                    setVal(prevNodeMap, successor, currentNode);
                    if(print) System.out.println("\tFOUND THE END!");
                    break outerLoop;
                }

                if (closedNodes.stream().anyMatch(successor::equals)) {
//                    System.out.println("\tThis neighbor is already on the closed list - skipping");
                    continue; //Skip this node if it's on the closed list
                }


                //Calculate next G. equal to last cell's G + cost to move to this cell.
                //Note that cost is multiplied by root of 2 (1.41 if diagonal movement!)
                float successorG = getVal(gMap, currentNode) +
                        getVal(weightMap, successor) * ((successorPair.getElement1()) ? (float)StrictMath.sqrt(2) : 1f);
                float successorH = Math.abs(target.getX() - successor.getX()) + Math.abs(target.getY() - successor.getY()); //Manhattan distance
                float successorF = successorG + successorH;
                if(print) System.out.println("\tsuccessorG = " + successorG);
                if(successorG == Float.POSITIVE_INFINITY) continue; //Skip cells that are infinite weight

                //Check if moving to this cell results in a change in direction, if so add some weight to this path.
                Object tempObj = getVal(prevDirMap, currentNode);
                float successorAngle = (float)Math.atan2(successor.getY() - currentNode.getY(), successor.getX() - currentNode.getX());
                if(tempObj != null) {
                    float currentAngle = (Float) tempObj;
                    if(Math.abs(currentAngle-successorAngle) > 0.001){ //FIXME Check if rounded floats are equivalent... Sketchy but will work
                        successorG+=1;
                    }
                }


                //If this successor point is NOT on the open list
                //OR it IS on the open list but the new F is lower than the existing F
                //Then proceed
                if (openNodes.stream().noneMatch(successor::equals) || successorF < getVal(fMap, currentNode)) {
                    openNodes.removeIf(successor::equals); //Remove this point if it exists on the open list
                    openNodes.add(successor);
                    setVal(prevNodeMap, successor, currentNode);
                    setVal(prevDirMap, successor, successorAngle);
//                    System.out.println("\tAdded to open list!");
                    setVal(hMap, successor, successorH);
                    setVal(gMap, successor, successorG);
                    setVal(fMap, successor, successorF);
                }

            }
        }

        if(getVal(prevNodeMap, target) == null){
            return null;
        }else{
            List<BasicCell> foundPath = new ArrayList<>();
            BasicCell nextBasicCell = target;
            while(!source.equals(nextBasicCell)){
                foundPath.add(nextBasicCell);
                nextBasicCell = getVal(prevNodeMap, nextBasicCell);
            }
            Collections.reverse(foundPath);
            return foundPath;
        }
    }

    private static List<Pair<BasicCell, Boolean>> getAdjacent(BasicCell p, BasicCell[][] map, boolean includeCorners){
        List<Pair<BasicCell, Boolean>> neighbors = new ArrayList<>();
        int height = map.length;
        int width = map[0].length;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; //
                if(includeCorners  || (i == 0 || j == 0)) { //Only do side pieces, ignore corners
                    int x = p.getX() + j;
                    int y = p.getY() + i;
                    if (x < 0 || y < 0 || x >= width || y >= height) {
                        continue;
                    } else {
                        neighbors.add(new Pair<>(map[y][x], i != 0 && j != 0));
                    }
                }
            }
        }

        return neighbors;
    }

    private static <T> T getVal(T[][] map, BasicCell p){
        return map[p.getY()][p.getX()];
    }

    private static <T> void setVal(T[][] map, BasicCell p, T val){
        map[p.getY()][p.getX()] = val;
    }

}
