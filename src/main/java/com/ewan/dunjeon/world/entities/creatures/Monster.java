package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.level.Floor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Monster extends Creature{
    public Monster(Color c, String name) {
        super(c, name);
    }

    private float walkSpeed = 0.03f;

    List<Point> currentPath = new ArrayList<>();

    @Override
    protected void processAI() {
        System.out.println("Doin it!");
        boolean pathValid = true;

        if(Objects.isNull(currentPath)){
            System.out.println("Path null!");
            pathValid = false;
        }
        else if(currentPath.isEmpty()){
            System.out.println("Path empty!");
            pathValid = false;
        }
        else if(currentPath.stream().anyMatch(point -> {
            CellMemory cell = getCurrentFloorMemory().getDataAt(point.x, point.y);
            return cell.enterable == CellMemory.EnterableStatus.CLOSED;
        })){
            System.out.println("Path inaccessible!");
            pathValid = false;
        }
//        else if(!currentPath.get(0).equals(this.getContainingCell().getPoint2D())){
//            System.out.println("Path doesnt begin from entity!");
//            pathValid = false;
//        }

        if(!pathValid){
            System.out.println("Recalculating path!");
            ArrayList<Point> possibleNodes = getListOfAccessibleNodesFromMemory();
            if(!possibleNodes.isEmpty()){
                float[][] weights = getWeightMapFromMemory();
                Point p = possibleNodes.get(Main.rand.nextInt(possibleNodes.size()));
                currentPath = PathFinding.getAStarPath(weights, new Point((int)getPosX(), (int)getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
                Collections.reverse(currentPath);
                System.out.println("New Path : " + currentPath);
            }
        }

        else{
            Point nextNode = currentPath.get(0);
            float targetX = nextNode.x + 0.5f;
            float targetY = nextNode.y + 0.5f;
            float distToNextTile = WorldUtils.getRawDistance(getPosX(), targetX, getPosY(), targetY);
            float angleToNextTile = (float) Math.atan2(targetY - getPosY(), targetX - getPosX());
            System.out.println("Dist to next tile : " + distToNextTile);
            if(distToNextTile > WorldUtils.ENTITY_WITHIN_TILE_THRESHOLD){
                System.out.println("Keep goin!");
                float speed = walkSpeed * Math.min(1, distToNextTile*2);
                float velX = speed * (float)Math.cos(angleToNextTile);
                float velY = speed * (float)Math.sin(angleToNextTile);
                addVelocity(velX, velY);
            }else{
                System.out.println("Reached tile!");
                currentPath.remove(0);
            }

        }

    }

    public ArrayList<Point> getListOfAccessibleNodesFromMemory() {
        Point startNode = new Point((int) Math.floor(getPosX()), (int) Math.floor(getPosY()));
        ArrayList<Point> toExplore = new ArrayList<>();
        ArrayList<Point> accessibleNodes = new ArrayList<>();
        toExplore.add(startNode);

        FloorMemory currentFloorMemory = getCurrentFloorMemory();

        if(currentFloorMemory != null){
            while (!toExplore.isEmpty()) {
                Point currentNode = toExplore.get(0);
                accessibleNodes.add(currentNode);
                toExplore.remove(currentNode);
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (i == 0 && j == 0) continue;
                        Point neighbor = new Point((int) currentNode.getX() + i, (int) currentNode.getY() + j);
                        CellMemory cellMemory = currentFloorMemory.getDataAt((int) neighbor.getX(), (int) neighbor.getY());
                        if (cellMemory != null && cellMemory.enterable == CellMemory.EnterableStatus.OPEN && !accessibleNodes.contains(neighbor) && !toExplore.contains(neighbor)) {
                            toExplore.add(neighbor);
                        }
                    }
                }
            }
        }

        return accessibleNodes;
    }

    /**
     * Generate a map of float weights for pathfinding @PathFinding.Java
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
