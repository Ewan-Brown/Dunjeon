package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.generation.GeneratorsMisc.*;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.furniture.Container;
import com.ewan.dunjeon.world.furniture.Furniture;
import com.ewan.dunjeon.world.level.Floor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    List<Section> sections;
    List<Door> doors;
    List<Hall> halls;
    Floor floor;

    float[][] weightMap;
    BasicCell[][] cells;

//    int[][] map;

    public void generateLeafs(int minSize, int maxRooms){
        ArrayList<Section> splits = new ArrayList<>(); //List of room 'splits', within which the sections will be created
        splits.add(new Section(2,2,width-3,height-3)); //Generate first leaf as entire map, excluding 2-wide edge (Wall at edge of map + space for path)

        //Split leafs until all are at desired size
        do{
            boolean finished = true;
            ArrayList<Section> newSections = new ArrayList<>();
            for (Section section : splits) {
                Section[] splitSections = section.split(minSize);
                if(splitSections == null){ //If this section can't be split anymore due to sizing issues, skip it
                    newSections.add(section);
                }
                else {//Add the split leafs to the new leafs list
                    finished = false;
                    newSections.addAll(Arrays.asList(splitSections));
                }
            }
            splits = newSections;
//            Display.drawMap(drawLeafsToMap(leafs, width, height, 1));
            if(finished) break;
        }while(true);

        //Remove leafs until we have desired number of sections if we have too many
        for (int i = splits.size() - maxRooms; i > 0;i--){
            splits.remove(rand.nextInt(splits.size()));
        }

        //Create the actual sizings of the sections themselves. Subleaf really just means 'the leaf within the leaf'
        List<Section> rooms = new ArrayList<>();
        for(Section f : splits){
            Section subSection = f.subLeaf();
            rooms.add(subSection);
        }
        if(rooms.size() < 2){
            throw new Error("Floor generated with less than 2 sections! Oh no!");
        }
        this.sections = rooms;

    }

    public void generateDoors(int minDoors, int maxDoors, int doorSpacing){
        //List of total doors. They are also stored under Section objects.
        List<Door> totalDoors = new ArrayList<Door>();

        //Create doors.
        for (Section currentSection : sections) {
            int doorNum = (maxDoors > minDoors) ? rand.nextInt(maxDoors-minDoors) + minDoors : maxDoors;
            //List of edge pieces eligible to become doors
            List<Point> edgePieces = currentSection.getEdgePieces(false);

            //Keep adding doors until either we have enough or there isn't enough space to create non-adjacent doors
            while(currentSection.doors.size() < doorNum && edgePieces.size() > 1 + doorSpacing*2){
                int possibleDoorIndex = rand.nextInt(edgePieces.size());

                //Remove this edge piece and neighbors from potential door list;
                List<Point> neighbors = new ArrayList<>();
                for (int i = possibleDoorIndex - doorSpacing; i <= possibleDoorIndex+doorSpacing; i++) {
                    int calculatedIndex = i % edgePieces.size();
                    if(calculatedIndex < 0) calculatedIndex = edgePieces.size() + calculatedIndex;
                    neighbors.add(edgePieces.get(calculatedIndex));
                }
                currentSection.doors.add(new Door(edgePieces.get(possibleDoorIndex), currentSection));
                edgePieces.removeAll(neighbors);
            }
            totalDoors.addAll(currentSection.doors);
        }
        this.doors = totalDoors;
    }
    
    public void generateWeightMap(){
        weightMap = new float[height][width];
        //Setup weight map for pathfinding
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //Set edges of map as inf. weight
                if(i == 0 || i == width - 1 || j == 0  || j == height - 1){
                    weightMap[j][i] = Float.POSITIVE_INFINITY;
                }else {
//                    weightMap[j][i] = 0;
                    weightMap[j][i] = rand.nextFloat();
                }
            }
        }

        //Set insides and walls of leafs as inf. weight
        //Also set the cells beside the walls to have higher weight
        for (Section rooms : sections) {
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
        //Ensures that each room connects to the next (so all sections are connected)
        List<Hall> halls = new ArrayList<>();
        for (int i = 0; i < sections.size()-1; i++) {
            Section l = sections.get(i);
            Section nextSection = sections.get(i+1);

            //Try to find a door that is unused, if not just grab the first one
            Door d1 = l.doors.stream().filter(door -> door.directConnections.isEmpty()).findFirst().orElse(l.doors.get(0));
            //Grab the first door of the next leaf. Will always be unused as of yet.
            Door d2 = nextSection.doors.get(0);

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

    //Add stairs to the floor according to how many are required
    public List<Stair> addStairs(List<Stair> connections, int downsRequired){

        List<Stair> downs = new ArrayList<>();

        //Add connecting upwards stairs
        for(Stair stair : connections){
            //TODO If the chosen section has no space this will crash.
            Section s = sections.get(rand.nextInt(sections.size()));
            int x = rand.nextInt(s.x2 - s.x1) + s.x1;
            int y = rand.nextInt(s.y2 - s.y1) + s.y1;
            Stair newStair = Stair.createConnectingUpwardsStair(x, y, floor, stair);
            cells[y][x] = newStair;
        }

        //Add new downwards stairs
        for(int i = 0; i < connections.size(); i++){
            //TODO If the chosen section has no space this will crash.
            Section s = sections.get(rand.nextInt(sections.size()));
            int x = rand.nextInt(s.x2 - s.x1) + s.x1;
            int y = rand.nextInt(s.y2 - s.y1) + s.y1;
            Stair newStair = Stair.createDownwardsStair(x, y, floor);
            cells[y][x] = newStair;
            downs.add(newStair);
        }

        return downs;
    }

    public void addFurniture(){
        //Generate up stairs

        int chests = rand.nextInt((int)Math.ceil(sections.size()/5)) + 1;
        for (int i = 0; i < chests; i++) {
            randomlyPlaceFurniture(new Container());
        }
    }

    public void randomlyPlaceFurniture(Furniture f){
        //TODO If the chosen section has no space this will crash.
        Section s = sections.get(rand.nextInt(sections.size()));
        List<Pair<Integer, Integer>> availableLocations = s.getAvailableLocations();
        Collections.shuffle(availableLocations, rand);
        Pair<Integer, Integer> location = availableLocations.get(0);
        BasicCell cell = floor.getCellAt(location.getElement0(), location.getElement1());
        cell.setFurniture(f);
        s.assignedFurniture.put(f, location);
    }


    public void buildCells(){
        floor = new Floor();
        //Draw background
        cells = new BasicCell[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//                map[j][i] = BLOCK;
                cells[j][i] = new BasicCell(i, j, floor);
                cells[j][i].setFilled(true);
                cells[j][i].setColor(Color.BLACK);

            }
        }

        //Draw sections
        if(sections != null){
            for (Section section : sections) {
                for (int i = section.x1; i <= section.x2; i++) {
                    for (int j = section.y1; j <= section.y2; j++) {;
                        if(i == section.x1 || i == section.x2 || j == section.y1 || j == section.y2) {
//                            map[j][i] = WALL;
                            cells[j][i] = new BasicCell(i, j, floor);
                            cells[j][i].setFilled(true);
                            cells[j][i].setColor(Color.DARK_GRAY);

                        }else {
                            cells[j][i] = new BasicCell(i, j, floor);
                            cells[j][i].setFilled(false);
                            cells[j][i].setColor(Color.GRAY);
                        }
                    }
                }
            }
        }

        //Draw doors
        if(doors != null) {
            for (Door door : doors) {
                cells[door.y][door.x] = new BasicCell(door.x, door.y, floor);
                cells[door.y][door.x].setFilled(false);
                cells[door.y][door.x].setColor(Color.GRAY);
                cells[door.y][door.x].setFurniture(new com.ewan.dunjeon.world.furniture.Door(false));
//                cells[door.y][door.x].setColor(new Color(165,42,42, 255));
            }
        }

        //Draw paths to map
        if(halls != null) {
            for (Hall hall : halls) {
                for (Point point : hall.points) {
                    cells[point.y][point.x] = new BasicCell(point.x, point.y, floor);
                    cells[point.y][point.x].setFilled(false);
                    cells[point.y][point.x].setColor(Color.GRAY);
                }
            }
        }
        floor.setCells(cells);
    }

    public Floor getFloor(){
        return this.floor;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Hall> getHalls() {
        return halls;
    }
}
