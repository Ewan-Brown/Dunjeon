package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class Monster extends Creature{
    public Monster(Color c, String name) {
        super(c, name);
    }

    private float walkSpeed = 0.03f;

    List<Point> currentPath = new ArrayList<>();

    @Override
    protected void processAI() {

        LiveDisplay.debugCells.clear();
        if(getCurrentFloorMemory() != null) {
            getCurrentFloorMemory().streamCellData().forEach(cellMemory -> {
                if (cellMemory != null && cellMemory.hasBeenExplored()) {
                    LiveDisplay.debugCells.put(cellMemory.getPoint(), Color.PINK);
                }
            });
        }

        boolean pathValid = true;

        if(Objects.isNull(currentPath)){
            pathValid = false;
        }
        else if(currentPath.isEmpty()){
            pathValid = false;
        }
        else if(currentPath.stream().anyMatch(point -> {
            CellMemory cell = getCurrentFloorMemory().getDataAt(point.x, point.y);
            return cell.enterable == CellMemory.EnterableStatus.CLOSED;
        })){
            pathValid = false;
        }

        if(!pathValid){
            //TODO IF you want to change the exploratory nature of mosnter - go here
            ArrayList<CellMemory> possibleNodes = getListOfPotentialDestinations(cellMemory -> !cellMemory.hasBeenExplored());
            possibleNodes.sort((o1, o2) -> Float.compare(o1.getTimeStamp(), o2.getTimeStamp()));
            if(!possibleNodes.isEmpty()){
                float[][] weights = getWeightMapFromMemory();
                Point p = possibleNodes.get(possibleNodes.size()-1).getPoint();
                currentPath = PathFinding.getAStarPath(weights, new Point((int)getPosX(), (int)getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
                Collections.reverse(currentPath);
            }
        }

        else{
            Point nextNode = currentPath.get(0);
            float targetX = nextNode.x + 0.5f;
            float targetY = nextNode.y + 0.5f;
            float distToNextTile = WorldUtils.getRawDistance(getPosX(), targetX, getPosY(), targetY);
            float angleToNextTile = (float) Math.atan2(targetY - getPosY(), targetX - getPosX());
            if(distToNextTile > WorldUtils.ENTITY_WITHIN_TILE_THRESHOLD){
                float speed = walkSpeed * Math.min(1, distToNextTile*2);
                float velX = speed * (float)Math.cos(angleToNextTile);
                float velY = speed * (float)Math.sin(angleToNextTile);
                addVelocity(velX, velY);
            }else{
                currentPath.remove(0);
            }

        }

    }

    /**
     * Ordered most to least interesting to AI...
     * @return
     */
    public ArrayList<CellMemory> getListOfPotentialDestinations(Predicate<CellMemory> criteria) {
        Point startNode = new Point((int) Math.floor(getPosX()), (int) Math.floor(getPosY()));
        ArrayList<Point> toExplore = new ArrayList<>();
        ArrayList<Point> alreadyExplored = new ArrayList<>();
        ArrayList<CellMemory> potentialDestinations = new ArrayList<>();
        toExplore.add(startNode);

        FloorMemory currentFloorMemory = getCurrentFloorMemory();

        if(currentFloorMemory != null){
            while (!toExplore.isEmpty()) {
                Point currentNode = toExplore.get(0);
                CellMemory currentCell = currentFloorMemory.getDataAt(currentNode.x, currentNode.y);
                if(criteria.test(currentCell)) {
                    potentialDestinations.add(currentCell);
                }
                toExplore.remove(currentNode);
                alreadyExplored.add(currentNode);
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (i == 0 && j == 0) continue;
                        Point neighbor = new Point((int) currentNode.getX() + i, (int) currentNode.getY() + j);
                        CellMemory cellMemory = currentFloorMemory.getDataAt((int) neighbor.getX(), (int) neighbor.getY());
                        if (cellMemory != null && cellMemory.enterable == CellMemory.EnterableStatus.OPEN
                                && !potentialDestinations.contains(neighbor) && !toExplore.contains(neighbor)
                                && !alreadyExplored.contains(neighbor)) {
                            toExplore.add(neighbor);
                        }
                    }
                }
            }
        }

        return potentialDestinations;
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
