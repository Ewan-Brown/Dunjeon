package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.generation.GeneratorsMisc.*;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.furniture.Container;
import com.ewan.dunjeon.world.level.Floor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.ewan.dunjeon.game.Main.rand;
import static java.lang.Float.POSITIVE_INFINITY;

public class FloorGenerator {

    public FloorGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    int width;
    int height;

    List<Section> sections;
    List<Door> doors;
    List<Hall> totalHalls;
    List<Stair> stairs;
    List<Split> splits;
    List<Junction> junctions;
    Floor floor = new Floor();

    float[][] weightMap;
    BasicCell[][] cells;

//    int[][] map;

    public void generateLeafs(int minSize, int maxRooms, int roomPadding){
        ArrayList<Section> splitSections = new ArrayList<>(); //List of room 'splitSections', within which the sections will be create
        splits = new ArrayList<>();
        splitSections.add(new Section(2,2,width-3,height-3)); //Generate first leaf as entire map, excluding 2-wide edge (Wall at edge of map + space for path)

        //Split leafs until all are at desired size
        do{
            boolean finished = true;
            ArrayList<Section> newSections = new ArrayList<>();
            for (Section section : splitSections) {
                Pair<Section[], Split> splitResults = section.split(minSize);
                if(splitResults.getElement0() == null){ //If this section can't be split anymore due to sizing issues, skip it
                    newSections.add(section);
                }
                else {//Add the split leafs to the new leafs list
                    finished = false;
                    newSections.addAll(Arrays.asList(splitResults.getElement0()));
                    splits.add(splitResults.getElement1());
                }
            }
            //Discards old sections that have been split! Final splitSectionsArray will only contain the final un-split "Descendants".
            splitSections = newSections;
//            Display.drawMap(drawLeafsToMap(leafs, width, height, 1));
            if(finished) break;
        }while(true);

        //Remove leafs at random until we have desired number of sections if we have too many
        Collections.shuffle(splitSections, rand);
        if(maxRooms != -1) {
            for (int i = splitSections.size() - maxRooms; i > 0; i--) {
                splitSections.remove(rand.nextInt(splitSections.size()));
            }
        }
        //Create the actual sizings of the sections themselves. Subleaf really just means 'the leaf within the leaf'
        List<Section> rooms = new ArrayList<>();
        for(Section f : splitSections){
            Section subSection = f.subLeaf(roomPadding, -1, null);
            rooms.add(subSection);
        }
        //Shuffle these up, so there's no bias when continuing generation.
        Collections.shuffle(rooms, rand);
        this.sections = rooms;

    }

    public void generateDoors(int minDoors, int maxDoors, int doorSpacing){
        //List of total doors. They are also stored under Section objects.
        List<Door> totalDoors = new ArrayList<Door>();

        //Create doors.
        for (Section currentSection : sections) {
            int doorNum = (maxDoors > minDoors) ? rand.nextInt(maxDoors-minDoors) + minDoors : maxDoors;
            //List of edge pieces eligible to become doors
            List<Pair<Point, List<Point>>> edgePieces = currentSection.getNonWallBoundedEdgePieces(false);

            //Keep adding doors until either we have enough or there isn't enough space to create non-adjacent doors
            while(currentSection.doors.size() < doorNum && edgePieces.size() > 1 + doorSpacing*2){
                int possibleDoorIndex = rand.nextInt(edgePieces.size());

                //Remove this edge piece and neighbors from potential door list;
                List<Point> neighbors = new ArrayList<>();
                for (int i = possibleDoorIndex - doorSpacing; i <= possibleDoorIndex+doorSpacing; i++) {
                    int calculatedIndex = i % edgePieces.size();
                    if(calculatedIndex < 0) calculatedIndex = edgePieces.size() + calculatedIndex;
                    neighbors.add(edgePieces.get(calculatedIndex).getElement0());
                }
                Point doorLocation = edgePieces.get(possibleDoorIndex).getElement0();
                List<Point> doorEntryPoints = edgePieces.get(possibleDoorIndex).getElement1();
                currentSection.doors.add(new Door(doorLocation, currentSection, doorEntryPoints));
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
                    weightMap[j][i] = POSITIVE_INFINITY;
                }else {
                    weightMap[j][i] = 0;
//                    weightMap[j][i] = rand.nextFloat();
                }
            }
        }

        //Set insides and walls of leafs as inf. weight
        //Also set the cells beside the walls to have higher weight
        for (Section rooms : sections) {
            for (int i = rooms.x1 -1; i <= rooms.x2+1; i++) {
                for (int j = rooms.y1-1; j <= rooms.y2+1; j++) {
                    if(i == rooms.x1 -1 || i == rooms.x2 +1 || j == rooms.y1 -1 || j == rooms.y2 + 1) weightMap[j][i] += 100;
                    else weightMap[j][i] = POSITIVE_INFINITY;
                }
            }
        }
    }

    enum Direction{
        NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);
        int x, y;

        Direction(int x, int y){
            this.x = x;
            this.y = y;
        }

        static Direction[] getSideDirections(Direction D){
            int index = D.ordinal();
            return new Direction[]{
                Direction.values()[Math.floorMod(index - 1, Direction.values().length)],
                Direction.values()[(index + 1) % Direction.values().length]
        };}

        static Direction getDirection(int x, int y){
            Optional<Direction> directionOptional = Arrays.stream(values()).filter(direction -> direction.x == x && direction.y == y).findFirst();

            if(directionOptional.isPresent()){
                return directionOptional.get();
            }else{
                throw new RuntimeException("called getDirection with " + x + ", " + y);
            }
        }

    }

    //TODO Clean this up a bit. Maybe make a nice queue of doors, and make sure that the queue is ordered such that it starts with one door from each leaf.
    public void generateHalls(int hallWidth){
        System.out.println("generateHalls");
        junctions = new ArrayList<>();
        if(hallWidth %2 != 0){
            throw new IllegalArgumentException("Cannot use hallwidth that isn't even for now.");
        }
        //Create Paths. Connects one room to another in a linear path.
        //Ensures that each room connects to the next (so all sections are connected)
        List<Hall> halls = new ArrayList<>();

        HashMap<Point, List<Split>> hallPoints2SplitMap = new HashMap<>();

        getSplits().forEach(split -> WorldUtils.getIntersectedTilesWithWall(split.getX1(), split.getY1(), split.getX2(), split.getY2()).stream().map(pointSidePair -> pointSidePair.getElement0()).forEach(point -> {

        if(!hallPoints2SplitMap.containsKey(point)){
            hallPoints2SplitMap.put(point, new ArrayList<>());
        }else{
            if(hallPoints2SplitMap.get(point).contains(point)){
                throw new RuntimeException("HallPoint associated with same split twice. Impossible...");
            }
            hallPoints2SplitMap.get(point).add(split);
//            junctionPoints.add(point);
            int halfHallWidth = hallWidth/2;
            Junction j = new Junction(point.x-halfHallWidth, point.y-halfHallWidth, point.x+halfHallWidth - 1, point.y+halfHallWidth - 1);
            System.out.println(junctions.size());
            junctions.add(j);
        }
        }));

        this.totalHalls = halls;
    }

    //Add stairs to the floor according to how many are required
    public List<Stair> generateStairs(List<Stair> connections, int downsRequired){
        List<Stair> downs = new ArrayList<>();

        //Add connecting upwards stairs
        for(Stair stair : connections){
            Point p = getRandomUnusedCell();
            Stair newStair = Stair.createConnectingUpwardsStair(p.x, p.y, floor, stair);
            stairs.add(newStair);
        }

        //Add new downwards stairs
        for(int i = 0; i < downsRequired; i++){
            Point p = getRandomUnusedCell();
            Stair newStair = Stair.createDownwardsStair(p.x, p.y, floor);
            stairs.add(newStair);
            downs.add(newStair);
        }

        return downs;
    }

    public void addFurniture(){
        int chests = rand.nextInt((int)Math.ceil(sections.size()/5.0)) + 1;
        for (int i = 0; i < chests; i++) {
            Container c = new Container();
            Point p = getRandomUnusedCell();
            cells[p.y][p.x].setFurniture(c);
        }
    }

    //TODO Add some handling for when there are no usable cells left?
    public Point getRandomUnusedCell(){
        //Sections are already shuffled so you don't have to worry about bias here
        Point cellCoords = null;
        Section selected = null;
        for (Section s : sections){
            cellCoords = s.getRandomEmptyPoint();
            if(cellCoords == null){
                continue;
            }
            else{
                selected = s;
            }
        }

        if(cellCoords == null){
            throw new RuntimeException("Floor has no space empty space left!");
        }else{
            selected.remove(cellCoords);
        }

        return cellCoords;
    }

    public void buildCells(){
        //Draw background
        cells = new BasicCell[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//                map[j][i] = BLOCK;
                cells[j][i] = new BasicCell(i, j, floor, Color.BLACK);
                cells[j][i].setFilled(true);
                cells[j][i].color = new Color (30,30,30);

            }
        }

        //Draw sections
        if(sections != null){
            for (Section section : sections) {
                for (int i = section.x1; i <= section.x2; i++) {
                    for (int j = section.y1; j <= section.y2; j++) {;
                        if(i == section.x1 || i == section.x2 || j == section.y1 || j == section.y2) {
//                            map[j][i] = WALL;
                            cells[j][i] = new BasicCell(i, j, floor, Color.DARK_GRAY);
                            cells[j][i].setFilled(true);
//                            cells[j][i].setColor(Color.DARK_GRAY);

                        }else {
                            cells[j][i] = new BasicCell(i, j, floor, Color.GRAY);
                            cells[j][i].setFilled(false);
//                            cells[j][i].setColor(Color.GRAY);
                        }
                    }
                }
            }
        }

        //Draw stairs
        if(stairs != null) {
            for (Stair s : stairs) {
                cells[s.getY()][s.getX()] = s;
            }
        }

        //Draw doors
        if(doors != null) {
            for (Door door : doors) {
                cells[door.y][door.x] = new BasicCell(door.x, door.y, floor, Color.GRAY);
                cells[door.y][door.x].setFilled(false);
                cells[door.y][door.x].setFurniture(new com.ewan.dunjeon.world.furniture.Door(false));
//                cells[door.y][door.x].setColor(new Color(165,42,42, 255));
            }
        }

        //Draw paths to map
        if(totalHalls != null) {
            for (Hall hall : totalHalls) {
                for (Point point : hall.points) {
                    cells[point.y][point.x] = new BasicCell(point.x, point.y, floor, Color.GRAY);
                    cells[point.y][point.x].setFilled(false);
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
        return totalHalls;
    }
    public List<Split> getSplits() {
        return splits;
    }

    public List<Junction> getJunctions(){
        return junctions;
    }
}