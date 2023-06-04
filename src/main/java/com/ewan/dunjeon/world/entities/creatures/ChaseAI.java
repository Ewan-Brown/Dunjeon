package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.generation.PathFinding;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChaseAI extends AIState {
    public ChaseAI(Creature e) {
        super(e);
    }

    @Override
    public void process() {

    }

    @Override
    public <T extends AIStateData> T getStateData() {
        return null;
    }
//
//
//    List<Point> currentPath = new ArrayList<>();
//    double desiredDistToTarget = 1;
//    long targetUUID;
//    boolean targetUnreachable = false;
//
//
//    public ChaseAI(Creature e, long targetUUID) {
//        super(e);
//        this.targetUUID = targetUUID;
//    }
//
//    public void process(){
//
//
//        CreatureKnowledge targetMemory = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);
//        Point targetPoint = new Point((int)Math.floor(targetMemory.getStateData().getX()), (int)Math.floor(targetMemory.getStateData().getY()));
//
//        List<Pair<Point, WorldUtils.Side>> intersectedCells = WorldUtils.getIntersectedTilesWithWall(hostEntity.getWorldCenter().x, hostEntity.getWorldCenter().y, targetMemory.getStateData().getX(), targetMemory.getStateData().getY());
//        List<CellMemory> cellMemories = new ArrayList<>();
//        for (Pair<Point, WorldUtils.Side> intersectedCell : intersectedCells) {
//            cellMemories.add(hostEntity.getCurrentFloorMemory().getDataAt(intersectedCell.getElement0()));
//        }
//        if(!targetMemory.isOldData() && cellMemories.stream().noneMatch(cellMemory -> cellMemory.enterable == CellMemory.EnterableStatus.CLOSED)){
//
//            double angle = (double)Math.atan2(targetMemory.getStateData().getY() - hostEntity.getWorldCenter().y,targetMemory.getStateData().getX() - hostEntity.getWorldCenter().x);
////            hostEntity.addVelocity((double)Math.cos(angle) * hostEntity.getWalkSpeed(), (double)Math.sin(angle) * hostEntity.getWalkSpeed());
//            //TODO Prepping for Dyn4J
//
//        }else {
//
//
//            boolean pathValid = true;
//
//            if (Objects.isNull(currentPath)) {
//                pathValid = false;
//            } else if (currentPath.isEmpty()) {
//                pathValid = false;
//            } else if (currentPath.stream().anyMatch(point -> hostEntity.getCurrentFloorMemory().getDataAt(point.x, point.y).enterable == CellMemory.EnterableStatus.CLOSED)) {
//                pathValid = false;
//            } else if (!WorldUtils.isAdjacent(currentPath.get(currentPath.size() - 1), targetPoint)) {
//                pathValid = false;
//            }
//
//            if (!pathValid) {
//                Point p = new Point((int) Math.floor(targetMemory.getStateData().getX()), (int) Math.floor(targetMemory.getStateData().getY()));
//                double[][] weights = getWeightMapFromMemory();
//                currentPath = PathFinding.getAStarPath(weights, new Point((int) hostEntity.getWorldCenter().x, (int) hostEntity.getWorldCenter().y), p, false, PathFinding.CornerInclusionRule.NON_CLIPPING_CORNERS, 0, true);
//                if (currentPath == null) {
//                    targetUnreachable = true;
//                } else if (!currentPath.isEmpty()) {
//                    Collections.reverse(currentPath);
//                }
//            } else {
//                Point nextNode = currentPath.get(0);
//                double targetX = nextNode.x + 0.5f;
//                double targetY = nextNode.y + 0.5f;
//                double distToNextTile = WorldUtils.getRawDistance(hostEntity.getWorldCenter().x, targetX, hostEntity.getWorldCenter().y, targetY);
//                double angleToNextTile = (double) Math.atan2(targetY - hostEntity.getWorldCenter().y, targetX - hostEntity.getWorldCenter().x);
//                if (distToNextTile > WorldUtils.ENTITY_WITHIN_TILE_THRESHOLD) {
//                    double speed = hostEntity.getWalkSpeed() * Math.min(1, distToNextTile * 2);
//                    double velX = speed * (double) Math.cos(angleToNextTile);
//                    double velY = speed * (double) Math.sin(angleToNextTile);
//                    //TODO Prepping for Dyn4J
////                    hostEntity.addVelocity(velX, velY);
//                } else {
//                    currentPath.remove(0);
//                }
//
//            }
//        }
//
//    }
//
//    public class ChaseAIStateData extends AIStateData{
//
//        @Override
//        public boolean canContinue() {
//
//            CreatureKnowledge targetMemory = hostEntity.getCurrentFloorMemory().getEntity(targetUUID);
//
//            if(targetMemory != null && !targetUnreachable) {
//                CellMemory targetLastLocationCellMemory = hostEntity.getCurrentFloorMemory().getCellMemoryOfEntityMemoryLocation(targetMemory);
//                if(!targetLastLocationCellMemory.isOldData() && targetMemory.isOldData()){
//                    //Found location of entity memory but no sign of entity nearby. Stop searching you fool
//                    //Also stop searching if the target's last location is no longer enterable
//                    if(targetLastLocationCellMemory.enterable == CellMemory.EnterableStatus.CLOSED ||((int)hostEntity.getWorldCenter().x == (int)targetMemory.getStateData().getX() && (int)hostEntity.getWorldCenter().y == (int)targetMemory.getStateData().getY())){
//                        return false;
//                    }
//                }
//               return (WorldUtils.getRawDistance(hostEntity.getWorldCenter().x, targetMemory.getStateData().getX(), hostEntity.getWorldCenter().y, targetMemory.getStateData().getY()) >= desiredDistToTarget);
//            }else{
//                return false;
//            }
//        }
//    }
//
//    @Override
//    public AIStateData getStateData() {
//        return new ChaseAIStateData();
//    }
//
//    /**
//     * Generate a map of double weights for pathfinding @PathFinding.Java
//     */
//    public double[][] getWeightMapFromMemory() {
//
//        double[][] weightMap = new double[hostEntity.getFloor().getHeight()][hostEntity.getFloor().getWidth()];
//
//        for (int y = 0; y < hostEntity.getFloor().getHeight(); y++) {
//            for (int x = 0; x < hostEntity.getFloor().getWidth(); x++) {
//
//                double weight;
//                CellMemory mem = hostEntity.getCurrentFloorMemory().getDataAt(x, y);
//
//                if (mem == null || mem.enterable == CellMemory.EnterableStatus.CLOSED) {
//                    weight = Float.POSITIVE_INFINITY;
//                } else {
//                    weight = 1;
//                }
//                weightMap[y][x] = weight;
//            }
//        }
//
//        return weightMap;
//    }

}
