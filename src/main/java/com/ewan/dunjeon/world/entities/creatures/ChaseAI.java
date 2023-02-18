package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChaseAI extends AIState {


    List<Point> currentPath = new ArrayList<>();
    float desiredDistToTarget = 1;
    long targetUUID;
    boolean targetUnreachable = false;


    public ChaseAI(Creature e, long targetUUID) {
        super(e);
        this.targetUUID = targetUUID;
    }

    public void process(){


        EntityMemory targetMemory = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);
        Point targetPoint = new Point((int)Math.floor(targetMemory.getStateData().getX()), (int)Math.floor(targetMemory.getStateData().getY()));

        List<Pair<Point, WorldUtils.Side>> intersectedCells = WorldUtils.getIntersectedTilesWithWall(hostEntity.getPosX(), hostEntity.getPosY(), targetMemory.getStateData().getX(), targetMemory.getStateData().getY());
        List<CellMemory> cellMemories = new ArrayList<>();
        for (Pair<Point, WorldUtils.Side> intersectedCell : intersectedCells) {
            cellMemories.add(hostEntity.getCurrentFloorMemory().getDataAt(intersectedCell.getElement0()));
        }
        if(!targetMemory.isOldData() && cellMemories.stream().noneMatch(cellMemory -> cellMemory.enterable == CellMemory.EnterableStatus.CLOSED)){

            float angle = (float)Math.atan2(targetMemory.getStateData().getY() - hostEntity.getPosY(),targetMemory.getStateData().getX() - hostEntity.getPosX());

            hostEntity.addVelocity((float)Math.cos(angle) * hostEntity.getWalkSpeed(), (float)Math.sin(angle) * hostEntity.getWalkSpeed());

        }else {


            boolean pathValid = true;

            if (Objects.isNull(currentPath)) {
                pathValid = false;
            } else if (currentPath.isEmpty()) {
                pathValid = false;
            } else if (currentPath.stream().anyMatch(point -> hostEntity.getCurrentFloorMemory().getDataAt(point.x, point.y).enterable == CellMemory.EnterableStatus.CLOSED)) {
                pathValid = false;
            } else if (!WorldUtils.isAdjacent(currentPath.get(currentPath.size() - 1), targetPoint)) {
                pathValid = false;
            }

            if (!pathValid) {
                Point p = new Point((int) Math.floor(targetMemory.getStateData().getX()), (int) Math.floor(targetMemory.getStateData().getY()));
                float[][] weights = getWeightMapFromMemory();
                currentPath = PathFinding.getAStarPath(weights, new Point((int) hostEntity.getPosX(), (int) hostEntity.getPosY()), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
                if (currentPath == null) {
                    targetUnreachable = true;
                } else if (!currentPath.isEmpty()) {
                    Collections.reverse(currentPath);
                }
            } else {
                Point nextNode = currentPath.get(0);
                float targetX = nextNode.x + 0.5f;
                float targetY = nextNode.y + 0.5f;
                float distToNextTile = WorldUtils.getRawDistance(hostEntity.getPosX(), targetX, hostEntity.getPosY(), targetY);
                float angleToNextTile = (float) Math.atan2(targetY - hostEntity.getPosY(), targetX - hostEntity.getPosX());
                if (distToNextTile > WorldUtils.ENTITY_WITHIN_TILE_THRESHOLD) {
                    float speed = hostEntity.getWalkSpeed() * Math.min(1, distToNextTile * 2);
                    float velX = speed * (float) Math.cos(angleToNextTile);
                    float velY = speed * (float) Math.sin(angleToNextTile);
                    hostEntity.addVelocity(velX, velY);
                } else {
                    currentPath.remove(0);
                }

            }
        }

    }

    public class ChaseAIStateData extends AIStateData{

        @Override
        public boolean canContinue() {

            EntityMemory targetMemory = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);

            if(targetMemory != null && !targetUnreachable) {
                CellMemory targetLastLocationCellMemory = hostEntity.getCurrentFloorMemory().getCellMemoryOfEntityMemoryLocation(targetMemory);
                if(!targetLastLocationCellMemory.isOldData() && targetMemory.isOldData()){
                    //Found location of entity memory but no sign of entity nearby. Stop searching you fool
                    //Also stop searching if the target's last location is no longer enterable
                    if(targetLastLocationCellMemory.enterable == CellMemory.EnterableStatus.CLOSED ||((int)hostEntity.getPosX() == (int)targetMemory.getStateData().getX() && (int)hostEntity.getPosY() == (int)targetMemory.getStateData().getY())){
                        return false;
                    }
                }
               return (WorldUtils.getRawDistance(hostEntity.getPosX(), targetMemory.getStateData().getX(), hostEntity.getPosY(), targetMemory.getStateData().getY()) >= desiredDistToTarget);
            }else{
                return false;
            }
        }
    }

    @Override
    public AIStateData getStateData() {
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
