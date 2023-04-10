package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.generation.GeneratorsMisc;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.entities.creatures.Monster;
import com.ewan.dunjeon.world.entities.creatures.Player;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final Random rand = new Random();
    static final long UPDATE_DELAY = 16;

    public static void main(String[] args) {

        generateWorld();

        LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.startDrawing(World.getInstance());

        new Thread(() -> {
            while (true) {
                if(updateCurrentWorld()){
                    break;
                }
            }
        }).start();

    }

    private static void generate_X_world_test(int worlds){
        for (int i = 0; i < worlds; i++) {
            System.out.println(i + " / " + worlds);
            generateWorld();
            updateCurrentWorld();
        }
    }

    private static boolean updateCurrentWorld(){
        World w = World.getInstance();
        w.getPlayer().updateViewRange();
        boolean gameOver = w.update();
        if(gameOver){
            return true;
        }
        try {
            Thread.sleep(UPDATE_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void generateWorld(){
        long seed = rand.nextInt();
        System.out.println("SEED USED : " + seed);
//        rand.setSeed(seed);
        rand.setSeed(1082347570);

        World.resetWorld();
        World w = World.getInstance();
        int floorCount = 1;
        List<Stair> prevStairs = new ArrayList<>();

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(100, 100);
            int hallWidth = 6; //Width of hallways
            int roomPadding = 0; //Extra walls between the room walls and the hallways


            generator.generateLeafs(15,-1, (hallWidth/2)+roomPadding);
            generator.generateDoors(1, 1, 2);
            generator.generateWeightMap();
            generator.generateHalls(hallWidth);
            System.out.println("Halls : " + generator.getHalls().size());
            for (GeneratorsMisc.Hall hall : generator.getHalls()) {
                System.out.printf("Hall : %d, %d -> %d,%d\n", hall.x1, hall.y1, hall.x2, hall.y2);
            }
            generator.buildCells();
            generator.addFurniture();
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            w.addLevel(newFloor);

            generator.getSplitLineList().forEach(split -> LiveDisplay.debugLines.put(split.getLine2D(), Color.RED));
            generator.getSplitLineList().forEach(split -> {
//                List<Pair<Point, WorldUtils.Side>> var = WorldUtils.getIntersectedTilesWithWall(split.getX1(), split.getY1(), split.getX2(), split.getY2());
//                boolean horizontal = split.getY1() == split.bgetY2();
//                for (Pair<Point, WorldUtils.Side> pointSidePair : var) {
//                    Point p = pointSidePair.getElement0();
//                    if (LiveDisplay.debugCells.containsKey(p)) {
//                        Color currentColor = LiveDisplay.debugCells.get(p);
//                        LiveDisplay.debugCells.put(p, currentColor.darker());
//                    } else {
//                        LiveDisplay.debugCells.put(p, Color.BLUE);
//                        if(horizontal){
//                            LiveDisplay.debugCells.put(new Point(p.x, p.y-1), Color.BLUE);
//                        }else{
//                            LiveDisplay.debugCells.put(new Point(p.x-1, p.y), Color.BLUE);
//                        }
//                    }
//                }
                LiveDisplay.debugLines.put(split.getLine2D(), Color.RED);
            });

            System.out.println("Junctions : " + generator.getJunctions().size());
            for (GeneratorsMisc.Junction junction : generator.getJunctions()) {
                System.out.printf("Junction : %d, %d -> %d,%d\n", junction.x1, junction.y1, junction.x2, junction.y2);
            }


        }


        Player testPlayer = new Player("Player");
        w.addEntityRandomLoc(testPlayer, startFloor);
        w.setPlayer(testPlayer);
//
//        ItemAsEntity simpleItemHammer = new ItemAsEntity(new Item("Hammer") {
//            @Override
//            public Shape getShape() {
//                Polygon p = new Polygon();
//                p.addPoint(-1, 1);
//                p.addPoint(11, 1);
//                p.addPoint(11, 3);
//                p.addPoint(14, 3);
//                p.addPoint(14, 1);
//                p.addPoint(15, 1);
//                p.addPoint(15, -1);
//                p.addPoint(14, -1);
//                p.addPoint(14, -3);
//                p.addPoint(11, -3);
//                p.addPoint(11, -1);
//                p.addPoint(-1, -1);
//                AffineTransform af = new AffineTransform();
//                af.scale(0.1,0.1);
//                return af.createTransformedShape(p);
//            }
//
//            @Override
//            public Point getMaxExtensionPoint() {
//                return null;
//            }
//        });
//
//        ItemAsEntity simpleItemSword = new ItemAsEntity(new Item("Sword"){
//            @Override
//            public Shape getShape() {
//                Polygon p = new Polygon();
//                p.addPoint(-2, 1);
//                p.addPoint(2, 1);
//                p.addPoint(2, 3);
//                p.addPoint(4, 3);
//                p.addPoint(4, 1);
//                p.addPoint(10, 1);
//                p.addPoint(13, 0);
//                p.addPoint(10, -1);
//                p.addPoint(4, -1);
//                p.addPoint(4, -3);
//                p.addPoint(2, -3);
//                p.addPoint(2, -1);
//                p.addPoint(-2, -1);
//                AffineTransform af = new AffineTransform();
//                af.scale(0.1,0.1);
//                return af.createTransformedShape(p);
//            }
//
//            @Override
//            public Point getMaxExtensionPoint() {
//                return new Point(-2, 0);
//            }
//        });
//
//        ItemAsEntity simpleItemSpear = new ItemAsEntity(new Item("Spear"){
//            @Override
//            public Shape getShape() {
//                Polygon p = new Polygon();
//                p.addPoint(-9, 1);
//                p.addPoint(8, 1);
//                p.addPoint(7, 2);
//                p.addPoint(12, 0);
//                p.addPoint(7, -2);
//                p.addPoint(8, -1);
//                p.addPoint(-9, -1);
//                AffineTransform af = new AffineTransform();
//                af.scale(0.1,0.1);
//                return af.createTransformedShape(p);
//            }
//
//            @Override
//            public Point getMaxExtensionPoint() {
//                return new Point(-8, 0);
//            }
//        });

//        w.addEntityRandomLoc(simpleItemSword, startFloor);
//        w.addEntityRandomLoc(simpleItemSpear, startFloor);
//        w.addEntityRandomLoc(simpleItemHammer, startFloor);
//
        for (int i = 0; i < 1; i++) {
            Monster testMonster = Monster.generateExploringMonster(Color.GREEN, "Monster");
//        Monster testMonster = Monster.generateChasingMonster(Color.GREEN, "Monster");
            w.addEntityRandomLoc(testMonster, startFloor);
        }


//        NPC testNPC = NPC.generateDumbNPC(Color.CYAN, "NPC");
//        w.addEntityRandomLoc(testNPC, startFloor);m


    }
}
