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
    List<Hall> halls;
    List<Stair> stairs = new ArrayList<>();
    List<Split> splits;
    Floor floor = new Floor();

    float[][] weightMap;
    BasicCell[][] cells;

//    int[][] map;

    public void generateLeafs(int minSize, int maxRooms){
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

        //Remove leafs until we have desired number of sections if we have too many
        for (int i = splitSections.size() - maxRooms; i > 0;i--){
            splitSections.remove(rand.nextInt(splitSections.size()));
        }

        //Create the actual sizings of the sections themselves. Subleaf really just means 'the leaf within the leaf'
        List<Section> rooms = new ArrayList<>();
        for(Section f : splitSections){
            Section subSection = f.subLeaf();
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
    public void generateHalls(){
        //Create Paths. Connects one room to another in a linear path.
        //Ensures that each room connects to the next (so all sections are connected)
        List<Hall> halls = new ArrayList<>();
        for (int i = 0; i < sections.size()-1; i++) {

//            System.out.println("\t"+i + " / " + sections.size());
            Section l = sections.get(i);
            Section nextSection = sections.get(i+1);

//            System.out.println("\t\s"+"find door");
            //Try to find a door that is unused, if not just grab the first one
            Door d1 = l.doors.stream().filter(door -> door.directConnections.isEmpty()).findFirst().orElse(l.doors.get(0));
            //Grab the first door of the next leaf. Will always be unused.
            Door d2 = nextSection.doors.get(0);
//            System.out.println("\t\s"+"Get hall Path");
            Point startingPoint = d1.entryPoints.get(0);

            List<Point> hall = new ArrayList<>();
            Point previousPoint = startingPoint;
            Point hallEndPoint = d2.entryPoints.get(0); //Get opposing door entry point
            hall.add(previousPoint);
            pathIterator:
            while(true){
                List<Point> directPath1 = new ArrayList<>(); //X then Y
                List<Point> directPath2 = new ArrayList<>(); //Y then X

                //Calculate manhattan X/Y
                List<Integer> xVals = WorldUtils.listIntsBetween(previousPoint.x, hallEndPoint.x);
                List<Integer> yVals = WorldUtils.listIntsBetween(previousPoint.y, hallEndPoint.y);

                //Create the two possible manhattan paths
                for (Integer x : xVals) {
                        directPath1.add(new Point(x, previousPoint.y));
                }

                for (Integer y : yVals) {
                        directPath1.add(new Point(hallEndPoint.x, y));
                }

                for (Integer y : yVals) {
                        directPath2.add(new Point(previousPoint.x, y));
                }

                for (Integer x : xVals) {
                        directPath2.add(new Point(x, hallEndPoint.y));
                }

                //Calculate the path length in each direction till hitting a wall or ending

                int direct1UnobstructedLength = 0;
                int direct2UnobstructedLength = 0;
                Point direct1ObstructingPoint = null;
                Point direct2ObstructingPoint = null;

                for (Point point : directPath1) {
                    float w = weightMap[point.y][point.x];
                    if(point.equals(hallEndPoint)){
                        hall.addAll(directPath1);
                        break pathIterator;
                    }else{
                        if(w == POSITIVE_INFINITY){
                            direct1UnobstructedLength = directPath1.indexOf(point);
                            direct1ObstructingPoint = point;
                            break;
                        }
                    }
                }

                for (Point point : directPath2) {
                    float w = weightMap[point.y][point.x];
                    if(point.equals(hallEndPoint)){
                        hall.addAll(directPath2);
                        break pathIterator;
                    }else{
                        if(w == POSITIVE_INFINITY){
                            direct2UnobstructedLength = directPath2.indexOf(point);
                            direct2ObstructingPoint = point;
                            break;
                        }
                    }
                }

                List<Point> shorterDirectPath;
                List<Point> selectedDirectPath;
                Point obstructingPoint;

                if(previousPoint.x == hallEndPoint.x){
                    shorterDirectPath = directPath1.subList(0, direct1UnobstructedLength);
                    selectedDirectPath = directPath1;
                    obstructingPoint = direct1ObstructingPoint;
                }else if(previousPoint.y == hallEndPoint.y){
                    shorterDirectPath = directPath2.subList(0, direct2UnobstructedLength);
                    selectedDirectPath = directPath2;
                    obstructingPoint = direct2ObstructingPoint;
                }
                else if(direct1UnobstructedLength > direct2UnobstructedLength){
                    shorterDirectPath = directPath1.subList(0, direct1UnobstructedLength);
                    selectedDirectPath = directPath1;
                    obstructingPoint = direct1ObstructingPoint;
                }else{
                    shorterDirectPath = directPath2.subList(0, direct2UnobstructedLength);
                    selectedDirectPath = directPath2;
                    obstructingPoint = direct2ObstructingPoint;
                }

                Point endOfDirectPath;
                Direction directionOfEndOfPath;
                if(obstructingPoint == null){
                    throw new NullPointerException();
                }
                endOfDirectPath = shorterDirectPath.get(shorterDirectPath.size()-1);
                directionOfEndOfPath = Direction.getDirection(obstructingPoint.x - endOfDirectPath.x,
                        obstructingPoint.y - endOfDirectPath.y);
//                }else{
//                    //Weird edge case
//                    endOfDirectPath = previousPoint;
//                    directionOfEndOfPath = Direction.getDirection(endOfDirectPath.x - selectedDirectPath.get(0).x,
//                            endOfDirectPath.y - selectedDirectPath.get(0).y);
//                }

                if(endOfDirectPath.equals(hallEndPoint)){
                    hall.addAll(shorterDirectPath);
                    break;
                }

                //Attempt to manoeuvre around the current obstacle
                hall.addAll(shorterDirectPath);

                List<Pair<Direction, Integer>> availableManoeuvres = new ArrayList<>();
                for (Direction availableDirection : Arrays.stream(Direction.getSideDirections(directionOfEndOfPath)).toList()) {
                    Point p = new Point(endOfDirectPath.x + availableDirection.x, endOfDirectPath.y + availableDirection.y);
                    int manhattanDist =Math.abs(hallEndPoint.y - p.y) + Math.abs(hallEndPoint.x - p.x);
                    availableManoeuvres.add(new Pair<Direction, Integer>(availableDirection, manhattanDist));
                }

                Direction lowestDistDirection = availableManoeuvres.stream().min(Comparator.comparingInt(Pair::getElement1)).orElseThrow().getElement0();

                List<Point> manoeverPoints = new ArrayList<>();

                int m = 0;
                while(true){
                    m++;
                    Point p = new Point(endOfDirectPath.x + lowestDistDirection.x * m, endOfDirectPath.y + lowestDistDirection.y * m);
                    Point testPoint = new Point(p.x + directionOfEndOfPath.x, p.y + directionOfEndOfPath.y);
                    manoeverPoints.add(p);
                    if(weightMap[testPoint.y][testPoint.x] == 0){
                        break;
                    }
                }
                hall.addAll(manoeverPoints);
                previousPoint = manoeverPoints.get(manoeverPoints.size()-1);


            }

            //Create a hall from door1 to door2
            if( i == 1) {
                Hall p = new Hall(d1, d2, hall);
                halls.add(p);
            }


            //Create a hall from door1 to door2
////            List<Point> hall = PathFinding.getAStarPath(weightMap, d1.getPoint(), d2.getPoint(), true, PathFinding.CornerInclusionRule.NO_CORNERS, 1, false);
////            hall.remove(0);
////            Hall p = new Hall(d1, d2, hall);
////            halls.add(p);
////            for (Point point : hall) {
////                weightMap[point.y][point.x] = 0; //Decrease weight of cells that have already been pathed!
////            }
        }

        //Connect remaining unconnected doors
//        List<Door> unconnectedDoors = doors.stream().filter(door -> door.directConnections.isEmpty()).collect(Collectors.toList());
//        for (int i = 0; i < unconnectedDoors.size();i++) {
//            Door d1 = unconnectedDoors.get(i);
//            Door d2 = doors.get(rand.nextInt(doors.size()));
//            if(d1 == d2) continue;
//            unconnectedDoors.remove(d2);
//            List<Point> hall = PathFinding.getAStarPath(weightMap, d1.getPoint(), d2.getPoint(), false, PathFinding.CornerInclusionRule.NO_CORNERS, 1, false);
//            Hall p = new Hall(d1, d2, hall);
//            hall.remove(0);
//            halls.add(p);
//            for (Point point : hall) {
//                weightMap[point.y][point.x] = 0; //Decrease weight of cells that have already been pathed!
//            }
//        }

        this.halls = halls;
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
        for(Stair s : stairs){
            cells[s.getY()][s.getX()] = s;
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
        if(halls != null) {
            for (Hall hall : halls) {
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
        return halls;
    }

    public List<Split> getSplits() { return                                                                                                                                                                                                                                                                                                                                                                                                                                     splits;}
}
