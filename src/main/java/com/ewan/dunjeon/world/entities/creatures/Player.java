package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Player extends Creature {
    public Player(Color c, String name) {
        super(c, name);
        true_sight_debug = true;
    }

    @Override
    protected void processAI() {
        //Thinky thinky


    }

    @Override
    public void processSound(RelativeSoundEvent event) {
        super.processSound(event);
        int sourceX = (int) event.abs().sourceLocation().getX();
        int sourceY = (int) event.abs().sourceLocation().getY();
        boolean isVisible = !getFloorMemory(event.abs().sourceFloor()).getDataAt(sourceX, sourceY).isOldData();
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if (!message.isEmpty()) {
            System.out.println("[" + message + "]");
        }
    }

    //******* AI TESTING **********//
    ArrayList<Point> path = new ArrayList<>();

    public void triggerAIPathFind(){
//        System.out.println("triggering pathfind");
//        path.clear();
//        ArrayList<Point> possibleNodes = getListOfAccessibleNodesFromMemory();
//        System.out.printf("Found %d possible nodes to explore%n", possibleNodes.size());
//        Point p = possibleNodes.get(Main.rand.nextInt(possibleNodes.size()-1)); //Get last possible node - hopefully decently far from entity

//        Point p = possibleNodes.get(Main.rand.nextInt(possibleNodes.size()));
//        float[][] weights = getWeightMapFromMemory();
//        List<Point> pathPoints = PathFinding.getAStarPath(weights, new Point((int)getPosX(), (int)getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
//        System.out.printf("Path to node is %d tiles long\n", pathPoints.size());
//        LiveDisplay.debugCells.clear();
//        for (Point point : possibleNodes) {
//            LiveDisplay.debugCells.put(point, Color.CYAN);
//        }
//        for (Point point : pathPoints) {
//            LiveDisplay.debugCells.put(point, Color.RED);
//        }
//        LiveDisplay.debugCells.put(p, Color.BLUE);
//
//        LiveDisplay.debugCells.put(pathPoints.get(0), Color.PINK);

//        Point pathStart = pathPoints.get(pathPoints.size()-1);

//        System.out.printf("%f,%f => %f, %f\n",getPosX(), getPosY(), pathStart.getX(), pathStart.getY());
//        System.out.printf("\t dist to starting square : %f\n", WorldUtils.getRawDistance(getPosX(), (float)pathStart.getX() + 0.5f, getPosY(), (float)pathStart.getY() + 0.5f));

    }

    /**
     * Retrieves a list of accessible cells as described in memory.
     * Reasonably performant
     */
    public ArrayList<Point> getListOfAccessibleNodesFromMemory() {
        Point startNode = new Point((int) Math.floor(getPosX()), (int) Math.floor(getPosY()));
        ArrayList<Point> toExplore = new ArrayList<>();
        ArrayList<Point> accessibleNodes = new ArrayList<>();
        toExplore.add(startNode);

        while (!toExplore.isEmpty()) {
            Point currentNode = toExplore.get(0);
            accessibleNodes.add(currentNode);
            toExplore.remove(currentNode);
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) continue;
                    Point neighbor = new Point((int) currentNode.getX() + i, (int) currentNode.getY() + j);
                    CellMemory cellMemory = getCurrentFloorMemory().getDataAt((int) neighbor.getX(), (int) neighbor.getY());
                    if (cellMemory != null && cellMemory.enterable == CellMemory.EnterableStatus.OPEN && !accessibleNodes.contains(neighbor) && !toExplore.contains(neighbor)) {
                        toExplore.add(neighbor);
                    }
                }
            }
        }
        return accessibleNodes;
    }

    /**
     * Generate a map of float weights for pathfinding @PathFinding.Java
     *
     * @return
     */
    public float[][] getWeightMapFromMemory() {

        float[][] weightMap = new float[getFloor().getHeight()][getFloor().getWidth()];

        for (int y = 0; y < getFloor().getHeight(); y++) {
            for (int x = 0; x < getFloor().getWidth(); x++) {

                float weight;
                CellMemory mem = getCurrentFloorMemory().getDataAt(x, y);

                if (mem == null || mem.enterable == CellMemory.EnterableStatus.CLOSED) {
                    weight = Float.POSITIVE_INFINITY;
                } else {
                    weight = 1;
                }
                weightMap[y][x] = weight;
            }
        }

        return weightMap;
    }
}
