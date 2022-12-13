package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ChaseAI extends AIState {


    List<Point> currentPath = new ArrayList<>();
    float desiredDistToTarget = 1;
    long targetUUID;
    boolean targetUnreachable = false;


    public ChaseAI(Creature e, long targetUUID) {
        super(e);
    }

    //TODO Make this smarter by pathfinding with adjusted weights based on whether a cell has been explored or not,
    // i.e add a discount to weights on unexplored cells
    public void process(){

        EntityMemory targetMemory = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);
        Point targetPoint = new Point((int)Math.floor(targetMemory.getX()), (int)Math.floor(targetMemory.getY()));

        boolean pathValid = true;

        if(Objects.isNull(currentPath)){
            pathValid = false;
        }
        else if(currentPath.isEmpty()){
            pathValid = false;
        }
        else if(currentPath.stream().anyMatch(point -> hostEntity.getCurrentFloorMemory().getDataAt(point.x, point.y).enterable == CellMemory.EnterableStatus.CLOSED)){
            pathValid = false;
        }else if(!WorldUtils.isAdjacent(currentPath.get(currentPath.size()-1), targetPoint) ){
            pathValid = false;
        }

        if(!pathValid){
            Point p = new Point((int)Math.floor(targetMemory.getX()), (int)Math.floor(targetMemory.getY()));
            float[][] weights = getWeightMapFromMemory();
            currentPath = PathFinding.getAStarPath(weights, new Point((int)hostEntity.getPosX(), (int)hostEntity.getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
            if(currentPath == null){
                targetUnreachable = true;
            }
            else if(!currentPath.isEmpty()){
                Collections.reverse(currentPath);
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

    public class ChaseAIStateData extends AIStateData{

        @Override
        public boolean canContinue() {

            if(hostEntity.getCurrentFloorMemory().getEntity(targetUUID) != null && !targetUnreachable) {
                EntityMemory targetMem = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);
                return (WorldUtils.getRawDistance(hostEntity.getPosX(), targetMem.getX(), hostEntity.getPosY(), targetMem.getY()) >= desiredDistToTarget);
            }else{
                return false;
            }
        }
    }

    @Override
    public @NonNull AIStateData getStateData() {
        return new ChaseAIStateData();
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
