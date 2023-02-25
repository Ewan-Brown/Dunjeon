package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.entities.ItemAsEntity;
import com.ewan.dunjeon.world.entities.creatures.Monster;
import com.ewan.dunjeon.world.entities.creatures.Player;
import com.ewan.dunjeon.world.items.Item;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.World;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final Random rand = new Random();
    static final long UPDATE_DELAY = 16;

    public static void main(String[] args) {
        long seed = rand.nextInt();
        seed = 575246739;
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        World w = World.getInstance();
        int floorCount = 1;
        List<Stair> prevStairs = new ArrayList<>();

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(30, 30);
            System.out.println("1");
            generator.generateLeafs(5,5);
            System.out.println("2");
            generator.generateDoors(1, 1, 2);
            System.out.println("3");
            generator.generateWeightMap();
            System.out.println("4");
            generator.generateHalls();
            System.out.println("5");
            prevStairs = generator.generateStairs(prevStairs, (i == floorCount-1)? 0:1);
            generator.buildCells();
            System.out.println("6");
            generator.addFurniture();
            System.out.println("7");
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            w.addLevel(newFloor);
        }

        LiveDisplay liveDisplay = new LiveDisplay();

        Player testPlayer = new Player("Player");
        w.addEntityRandomLoc(testPlayer, startFloor);
        w.setPlayer(testPlayer);

        ItemAsEntity simpleItemHammer = new ItemAsEntity(new Item("Hammer") {
            @Override
            public Shape getShape() {
                Polygon p = new Polygon();
                p.addPoint(-1, 1);
                p.addPoint(11, 1);
                p.addPoint(11, 3);
                p.addPoint(14, 3);
                p.addPoint(14, 1);
                p.addPoint(15, 1);
                p.addPoint(15, -1);
                p.addPoint(14, -1);
                p.addPoint(14, -3);
                p.addPoint(11, -3);
                p.addPoint(11, -1);
                p.addPoint(-1, -1);
                AffineTransform af = new AffineTransform();
                af.scale(0.1,0.1);
                return af.createTransformedShape(p);
            }

            @Override
            public Point getMaxExtensionPoint() {
                return null;
            }
        });

        ItemAsEntity simpleItemSword = new ItemAsEntity(new Item("Sword"){
            @Override
            public Shape getShape() {
                Polygon p = new Polygon();
                p.addPoint(-2, 1);
                p.addPoint(2, 1);
                p.addPoint(2, 3);
                p.addPoint(4, 3);
                p.addPoint(4, 1);
                p.addPoint(10, 1);
                p.addPoint(13, 0);
                p.addPoint(10, -1);
                p.addPoint(4, -1);
                p.addPoint(4, -3);
                p.addPoint(2, -3);
                p.addPoint(2, -1);
                p.addPoint(-2, -1);
                AffineTransform af = new AffineTransform();
                af.scale(0.1,0.1);
                return af.createTransformedShape(p);
            }

            @Override
            public Point getMaxExtensionPoint() {
                return new Point(-2, 0);
            }
        });

        ItemAsEntity simpleItemSpear = new ItemAsEntity(new Item("Spear"){
            @Override
            public Shape getShape() {
                Polygon p = new Polygon();
                p.addPoint(-9, 1);
                p.addPoint(8, 1);
                p.addPoint(7, 2);
                p.addPoint(12, 0);
                p.addPoint(7, -2);
                p.addPoint(8, -1);
                p.addPoint(-9, -1);
                AffineTransform af = new AffineTransform();
                af.scale(0.1,0.1);
                return af.createTransformedShape(p);
            }

            @Override
            public Point getMaxExtensionPoint() {
                return new Point(-8, 0);
            }
        });

//        w.addEntityRandomLoc(simpleItemSword, startFloor);
//        w.addEntityRandomLoc(simpleItemSpear, startFloor);
//        w.addEntityRandomLoc(simpleItemHammer, startFloor);
//
//        for (int i = 0; i < 1; i++) {
////            Monster testMonster = Monster.generateExploringMonster(Color.GREEN, "Monster");
//        Monster testMonster = Monster.generateChasingMonster(Color.GREEN, "Monster");
//            w.addEntityRandomLoc(testMonster, startFloor);
//        }


//        NPC testNPC = NPC.generateDumbNPC(Color.CYAN, "NPC");
//        w.addEntityRandomLoc(testNPC, startFloor);

        liveDisplay.startDrawing(w);
        while (true) {
            w.getPlayer().updateViewRange();
            boolean gameOver = w.update();
            if(gameOver){
                break;
            }
            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
