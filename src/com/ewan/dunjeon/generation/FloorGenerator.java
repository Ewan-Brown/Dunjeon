package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.generation.GeneratorsMisc.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.generation.Main.rand;

public class FloorGenerator {

    public FloorGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    int width;
    int height;

    public static final int BLOCK = 0;
    public static final int OPEN = 1;
    public static final int DOOR = 2;
    public static final int WALL = 3;
    public static final int HALL = 4;

    List<Leaf> rooms;
    List<Door> doors;
    List<Hall> halls;

    float[][] weightMap;

    public void generateLeafs(int minSize, int maxRooms){
        ArrayList<Leaf> splits = new ArrayList<>(); //List of room 'splits', within which the rooms will be created
        splits.add(new Leaf(2,2,width-3,height-3)); //Generate first leaf as entire map, excluding 2-wide edge (Wall at edge of map + space for path)

        //Split leafs until all are at desired size
        do{
            boolean finished = true;
            ArrayList<Leaf> newLeafs = new ArrayList<>();
            for (Leaf leaf : splits) {
                Leaf[] splitLeafs = leaf.split(minSize);
                if(splitLeafs == null){ //If this leaf can't be split anymore due to sizing issues, skip it
                    newLeafs.add(leaf);
                }
                else {//Add the split leafs to the new leafs list
                    finished = false;
                    newLeafs.addAll(Arrays.asList(splitLeafs));
                }
            }
            splits = newLeafs;
//            Display.drawMap(drawLeafsToMap(leafs, width, height, 1));
            if(finished) break;
        }while(true);

        //Remove leafs until we have desired number of rooms if we have too many
        for (int i = splits.size() - maxRooms; i > 0;i--){
            splits.remove(rand.nextInt(splits.size()));
        }

        //Create the actual sizings of the rooms themselves. Subleaf really just means 'the leaf within the leaf'
        List<Leaf> rooms = new ArrayList<>();
        for(Leaf f : splits){
            Leaf subLeaf = f.subLeaf();
            rooms.add(subLeaf);
        }

        this.rooms = rooms;

    }

    public void generateDoors(int minDoors, int maxDoors, int doorSpacing){
        //List of total doors. They are also stored under Leaf objects.
        List<Door> totalDoors = new ArrayList<Door>();

        //Create doors.
        for (Leaf currentLeaf : rooms) {
            int doorNum = (maxDoors > minDoors) ? rand.nextInt(maxDoors-minDoors) + minDoors : maxDoors;
            //List of edge pieces eligible to become doors
            List<Point> edgePieces = currentLeaf.getEdgePieces(false);

            //Keep adding doors until either we have enough or there isn't enough space to create non-adjacent doors
            while(currentLeaf.doors.size() < doorNum && edgePieces.size() > 1 + doorSpacing*2){
                int possibleDoorIndex = rand.nextInt(edgePieces.size());

                //Remove this edge piece and neighbors from potential door list;
                List<Point> neighbors = new ArrayList<>();
                for (int i = possibleDoorIndex - doorSpacing; i <= possibleDoorIndex+doorSpacing; i++) {
                    int calculatedIndex = i % edgePieces.size();
                    if(calculatedIndex < 0) calculatedIndex = edgePieces.size() + calculatedIndex;
                    neighbors.add(edgePieces.get(calculatedIndex));
                }
                currentLeaf.doors.add(new Door(edgePieces.get(possibleDoorIndex), currentLeaf));
                edgePieces.removeAll(neighbors);
            }
            totalDoors.addAll(currentLeaf.doors);
        }
        this.doors = totalDoors;
    }
    
    public void generateWeightMap(){
        weightMap = new float[height][width];
        //Setup weight map for pathfinding
        for (int i = 0; i < weightMap.length; i++) {
            for (int j = 0; j < weightMap[0].length; j++) {
                //Set edges of map as inf. weight
                if(i == 0 || i == weightMap.length - 1 || j == 0  || j == weightMap[0].length - 1){
                    weightMap[j][i] = Float.POSITIVE_INFINITY;
                }else {
//                    weightMap[j][i] = 0;
                    weightMap[j][i] = rand.nextFloat();
                }
            }
        }

        //Set insides and walls of leafs as inf. weight
        //Also set the cells beside the walls to have higher weight
        for (Leaf rooms : rooms) {
            for (int i = rooms.x1 -1; i <= rooms.x2+1; i++) {
                for (int j = rooms.y1-1; j <= rooms.y2+1; j++) {

                    if(i == rooms.x1 -1 || i == rooms.x2 +1 || j == rooms.y1 -1 || j == rooms.y2 + 1) weightMap[j][i] += 100;
                    else weightMap[j][i] = Float.POSITIVE_INFINITY;
                }
            }
        }
    }

    //TODO Clean this up a bit. Maybe make a nice queue of doors, and make sure that the queue is ordered such that it starts with one door from each leaf.
    public void generateHalls(){
        //Create Paths. Connects one room to another in a linear path.
        //Ensures that each room connects to the next (so all rooms are connected)
        List<Hall> halls = new ArrayList<>();
        for (int i = 0; i < rooms.size()-1; i++) {
            Leaf l = rooms.get(i);
            Leaf nextLeaf = rooms.get(i+1);

            //Try to find a door that is unused, if not just grab the first one
            Door d1 = l.doors.stream().filter(door -> door.directConnections.isEmpty()).findFirst().orElse(l.doors.get(0));
            //Grab the first door of the next leaf. Will always be unused as of yet.
            Door d2 = nextLeaf.doors.get(0);

            //Create a hall from door1 to door2
            List<Point> hall = PathFinding.getAStarPath(weightMap, d1.getPoint(), d2.getPoint(), false);
            hall.remove(0);
            Hall p = new Hall(d1, d2, hall);
            halls.add(p);
            for (Point point : hall) {
                weightMap[point.y][point.x] = 0; //Decrease weight of cells that have already been pathed!
            }
        }

        //Connect remaining unconnected doors
        List<Door> unconnectedDoors = doors.stream().filter(door -> door.directConnections.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i < unconnectedDoors.size();i++) {
            Door d1 = unconnectedDoors.get(i);
            Door d2 = doors.get(rand.nextInt(doors.size()));
            if(d1 == d2) continue;
            unconnectedDoors.remove(d2);
            List<Point> hall = PathFinding.getAStarPath(weightMap, d1.getPoint(), d2.getPoint(), false);
            Hall p = new Hall(d1, d2, hall);
            hall.remove(0);
            halls.add(p);
            for (Point point : hall) {
                weightMap[point.y][point.x] = 0; //Decrease weight of cells that have already been pathed!
            }
        }

        this.halls = halls;
    }

    public int[][] getGrid(){
        int[][] map = new int[height][width];

        //Draw background
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[j][i] = BLOCK;
            }
        }

        //Draw rooms
        if(rooms != null){
            for (Leaf leaf : rooms) {
                for (int i = leaf.x1; i <= leaf.x2; i++) {
                    for (int j = leaf.y1; j <= leaf.y2; j++) {;
                        if(i == leaf.x1 || i == leaf.x2 || j == leaf.y1 || j == leaf.y2) {
                            map[j][i] = WALL;
                        }else {
                            map[j][i] = OPEN;
                        }
                    }
                }
            }
        }


        //Draw doors
        if(doors != null) {
            for (Door door : doors) {
                map[door.y][door.x] = DOOR;
            }
        }

        //Draw paths to map
        if(halls != null) {
            for (Hall hall : halls) {
                for (Point point : hall.points) {
                    map[point.y][point.x] = HALL;
                }
            }
        }

        return map;
    }

    public List<Leaf> getRooms() {
        return rooms;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Hall> getHalls() {
        return halls;
    }
}
