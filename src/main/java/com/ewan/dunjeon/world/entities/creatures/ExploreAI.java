package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ExploreAI extends AIState {


    List<Point> currentPath = new ArrayList<>();
    boolean noAccessableUnexploredCells = false;

    public ExploreAI(Creature e) {
        super(e);
    }

    //TODO Make this smarter by pathfinding with adjusted weights based on whether a cell has been explored or not,
    // i.e add a discount to weights on unexplored cells
    public void process(){
        noAccessableUnexploredCells = false;
        boolean pathValid = true;

        if(Objects.isNull(currentPath)){
            pathValid = false;
        }
        else if(currentPath.isEmpty()){
            pathValid = false;
        }
        else if(currentPath.stream().anyMatch(point -> {
            CellMemory cell = hostEntity.getCurrentFloorMemory().getDataAt(point.x, point.y);
            return cell.enterable == CellMemory.EnterableStatus.CLOSED;
        })){
            pathValid = false;
        }

        if(!pathValid){
            //If you want to change the exploratory nature of monster - look here
            ArrayList<CellMemory> possibleNodes = getListOfPotentialDestinations(cellMemory -> cellMemory.getExploredStatus() != CellMemory.ExploredStatus.EXPLORED_UNCHANGED);
            possibleNodes.sort((o1, o2) -> Float.compare(o1.getTimeStamp(), o2.getTimeStamp()));
            if(!possibleNodes.isEmpty()){
                float[][] weights = getWeightMapFromMemory();
                Point p = possibleNodes.get(possibleNodes.size()-1).getPoint();
                currentPath = PathFinding.getAStarPath(weights, new Point((int)hostEntity.getPosX(), (int)hostEntity.getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
                if(currentPath == null){
                    throw new IllegalStateException("Path is null!");
                }
                Collections.reverse(currentPath);
            }
            else{
                noAccessableUnexploredCells = true;
            }
        }

        else{
            Point nextNode = currentPath.get(0);
            float targetX = nextNode.x + 0.5f;
            float targetY = nextNode.y + 0.5f;
            float distToNextTile = WorldUtils.getRawDistance(hostEntity.getPosX(), targetX, hostEntity.getPosY(), targetY);
            float angleToNextTile = (float) Math.atan2(targetY - hostEntity.getPosY(), targetX - hostEntity.getPosX());
            if(distToNextTile > WorldUtils.ENTITY_WITHIN_TILE_THRESHOLD){
                float speed = hostEntity.getWalkSpeed() * Math.min(1, distToNextTile*2);
                float velX = speed * (float)Math.cos(angleToNextTile);
                float velY = speed * (float)Math.sin(angleToNextTile);
                hostEntity.addVelocity(velX, velY);
            }else{
                currentPath.remove(0);
            }

        }

    }

    public class ExploreAIStateData extends AIStateData{

        @Override
        public boolean canContinue() {
            return CreatureUtils.countUnexploredCells(hostEntity) > 0 && !noAccessableUnexploredCells;
        }
    }

    @Override
    public @NonNull AIStateData getStateData() {
        return new ExploreAIStateData();
    }

    public ArrayList<CellMemory> getListOfPotentialDestinations(Predicate<CellMemory> criteria) {
        Point startNode = new Point((int) Math.floor(hostEntity.getPosX()), (int) Math.floor(hostEntity.getPosY()));
        ArrayList<Point> toExplore = new ArrayList<>();
        ArrayList<Point> alreadyExplored = new ArrayList<>();
        ArrayList<CellMemory> potentialDestinations = new ArrayList<>();
        toExplore.add(startNode);

        FloorMemory currentFloorMemory = hostEntity.getCurrentFloorMemory();

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

        float[][] weightMap = new float[hostEntity.getFloor().getHeight()][hostEntity.getFloor().getWidth()];

        for (int y = 0; y < hostEntity.getFloor().getHeight(); y++) {
            for (int x = 0; x < hostEntity.getFloor().getWidth(); x++) {

                float weight;
                CellMemory mem = hostEntity.getCurrentFloorMemory().getDataAt(x, y);

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
