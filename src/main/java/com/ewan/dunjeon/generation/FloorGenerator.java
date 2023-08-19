package com.ewan.dunjeon.generation;

import com.ewan.dunjeon.generation.GeneratorsMisc.*;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.floor.Floor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.game.Main.rand;
import static java.lang.Float.POSITIVE_INFINITY;

public class FloorGenerator {

    public FloorGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        floor = new Floor(width, height);
    }

    int width;
    int height;

    List<Section> sections;
    List<Door> doors;
    List<SplitLine> splitLineList;
    List<Junction> junctions;
    List<Hall> halls;
    Floor floor;

    double[][] weightMap;
    BasicCell[][] cells;


    public void generateLeafs(int minSize, int maxRooms, int roomPadding){
        ArrayList<Section> splitSections = new ArrayList<>(); //List of room 'splitSections', within which the sections will be create
        splitLineList = new ArrayList<>();
        splitSections.add(new Section(2,2,width-3,height-3)); //Generate first leaf as entire map, excluding 2-wide edge (Wall at edge of map + space for path)

        //Split leafs until all are at desired size
        do{
            boolean finished = true;
            ArrayList<Section> newSections = new ArrayList<>();
            for (Section section : splitSections) {
                Pair<Section[], SplitLine> splitResults = section.split(minSize);
                if(splitResults.getElement0() == null){ //If this section can't be split anymore due to sizing issues, skip it
                    newSections.add(section);
                }
                else {//Add the split leafs to the new leafs list
                    finished = false;
                    newSections.addAll(Arrays.asList(splitResults.getElement0()));
                    splitLineList.add(splitResults.getElement1());
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

    public void generateDoorways(){
        for (Door door : doors) {
            int dx = door.entryPoints.get(0).x - door.x;
            int dy = door.entryPoints.get(0).y - door.y;
            int currentX = door.entryPoints.get(0).x;
            int currentY = door.entryPoints.get(0).y;
            while(true){
                if(cells[currentY][currentX].isFilled()) {
                    cells[currentY][currentX] = new BasicCell(currentX, currentY, floor, Color.GRAY);
                    cells[currentY][currentX].setFilled(false);
                    currentX += dx;
                    currentY += dy;
                }else{
                    break;
                }
            }
        }
    }

    public void generateWeightMap(){
        weightMap = new double[height][width];
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

        static Direction getCardinalDirection(int x, int y) {
            Optional<Direction> directionOptional = Arrays.stream(values()).filter(direction -> direction.x == (int)Math.signum(x) && direction.y == (int)Math.signum(y)).findFirst();
            return directionOptional.orElseThrow();
        }

    }

    private class SplitPath {
        List<Point> pathPoints = new ArrayList<>();
        List<Junction> associatedJunctions = new ArrayList<>();

        Point startPoint(){return pathPoints.get(0);}
        Point endPoint(){return pathPoints.get(pathPoints.size()-1);}
    }


    //TODO Clean this up a bit. Maybe make a nice queue of doors, and make sure that the queue is ordered such that it starts with one door from each leaf.
    public void generateHalls(int hallWidth){
        junctions = new ArrayList<>();
        halls = new ArrayList<>();
        int halfHallWidth = hallWidth/2;

        if(hallWidth %2 != 0){
            throw new IllegalArgumentException("Cannot use hallwidth that isn't even for now.");
        }

        List<SplitPath> splitPathList = new ArrayList<>();
        HashMap<Point, List<SplitPath>> intersectionPoints = new HashMap<>();

        //Create each 'splitData' object, containing all of its points
        for (SplitLine splitLine : getSplitLineList()) {
            SplitPath splitPath = new SplitPath();
            Direction d = Direction.getCardinalDirection(splitLine.getX2() - splitLine.getX1(), splitLine.getY2() - splitLine.getY1());
            Color c = new Color(rand.nextInt());
            int currentX = splitLine.getX1();
            int currentY = splitLine.getY1();
            do {
                Point p = new Point(currentX, currentY);
//                LiveDisplay.debugCells.put(p, c);
                splitPath.pathPoints.add(p);
                currentX += d.x;
                currentY += d.y;
            } while (currentX <= splitLine.getX2() && currentY <= splitLine.getY2());
            splitPathList.add(splitPath);
        }


        System.out.println("splitLineList = " + splitLineList.size());
        System.out.println("splitPathList = " + splitPathList.size());



        //Identify points of intersection
        for (SplitPath splitPath : splitPathList) {
            for (Point pathPoint : splitPath.pathPoints) {
                if (!intersectionPoints.containsKey(pathPoint)){
                    intersectionPoints.put(pathPoint, new ArrayList<>(List.of(splitPath)));
                }else{
                    intersectionPoints.get(pathPoint).add(splitPath);

                }
            }
        }
        HashMap<Point, Junction> createdJunctions = new HashMap<>();
        for (SplitPath splitPath : splitPathList) {
            Junction previousJunction = null;
            Junction firstJunction = null;
            for (Point intersectionPoint : splitPath.pathPoints.stream().filter(point -> intersectionPoints.get(point).size() > 1).collect(Collectors.toList())) {
                Junction junction;
                if(createdJunctions.containsKey(intersectionPoint)){
                    junction = createdJunctions.get(intersectionPoint);
                }
                else{
                    junction = new Junction(intersectionPoint.x-halfHallWidth, intersectionPoint.y-halfHallWidth, intersectionPoint.x+halfHallWidth - 1, intersectionPoint.y+halfHallWidth - 1);
                    junctions.add(junction);
                    createdJunctions.put(intersectionPoint, junction);
                }

                if(firstJunction == null){
                    firstJunction = junction;
                }
                if(previousJunction != null){
                    Hall h = new Hall(previousJunction, junction);
                    halls.add(h);
                }
                previousJunction = junction;
            }


            if(firstJunction != null && !firstJunction.containsPoint(splitPath.startPoint())){
                Point p = splitPath.startPoint();
                if (!createdJunctions.containsKey(p)) {
                    Hall h = new Hall(firstJunction, p.x - halfHallWidth, p.y - halfHallWidth, p.x + halfHallWidth - 1, p.y + halfHallWidth - 1, width-1, height-1);
                    halls.add(h);
                }
            }
            if(previousJunction != null && !previousJunction.containsPoint(splitPath.endPoint())){
                Point p = splitPath.endPoint();
                if(!createdJunctions.containsKey(p)) {
                    Hall h = new Hall(previousJunction, p.x - halfHallWidth, p.y - halfHallWidth, p.x + halfHallWidth - 1, p.y + halfHallWidth - 1, width-1, height-1);
                    halls.add(h);
                }
            }

            //Add dead ends if necessary
        }

        //Count # of intersection points
        System.out.println("# of intersections = " + intersectionPoints.values().stream().filter(paths -> paths.size() > 1).count());

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
//                cells[j][i].setFilled(true);
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

        //Draw doors
        if(doors != null) {
            for (Door door : doors) {
                cells[door.y][door.x] = new BasicCell(door.x, door.y, floor, Color.GRAY);
                cells[door.y][door.x].setFilled(false);
//                cells[door.y][door.x].setColor(new Color(165,42,42, 255));
            }
        }


        if (halls != null){
            System.out.println(halls.size());
            for (Hall hall : halls) {
//                for (int i = x1; i <= x2; i++) {
//                    for (int j = y1; j <= y2; j++) {
//                        cells[j][i] = new BasicCell(i, j, floor, Color.GRAY.darker());
//                        cells[j][i].setFilled(false);
//                    }
//                }
            }
        }

        if(junctions != null){
            for (Junction junction : junctions) {
                for (int i = junction.x1; i <= junction.x2; i++) {
                    for (int j = junction.y1; j <= junction.y2; j++) {
                        cells[j][i] = new BasicCell(i, j, floor, Color.GRAY.darker());
                        cells[j][i].setFilled(false);
                    }
                }
            }
        }

        if(halls != null){
            for (Hall h : halls) {
                for (int i = h.x1; i <= h.x2; i++) {
                    for (int j = h.y1; j <= h.y2; j++) {
                        cells[j][i] = new BasicCell(i, j, floor, Color.GRAY.lightGray);
                        cells[j][i].setFilled(false);
                    }
                }
            }
        }

        generateDoorways();

        //Draw paths to map
//        if(totalHalls != null) {
//            for (Hall hall : totalHalls) {
//                for (Point point : hall.points) {
//                    cells[point.y][point.x] = new BasicCell(point.x, point.y, floor, Color.GRAY);
//                    cells[point.y][point.x].setFilled(false);
//                }
//            }
//        }
        floor.setCells(cells);
        for (BasicCell[] c : cells) {
            for (BasicCell cell : c) {
                floor.getWorld().addBody(cell);
            }
        }
        System.out.println("# of cells: " + cells.length);
//        Arrays.stream(cells).forEach(basicCells -> Arrays.stream(basicCells).forEach(basicCell -> floor.getWorld().addBody(basicCell)));
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

    public List<SplitLine> getSplitLineList() {
        return splitLineList;
    }

    public List<Junction> getJunctions(){
        return junctions;
    }
}