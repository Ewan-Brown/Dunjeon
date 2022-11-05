package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.entities.creatures.Monster;
import com.ewan.dunjeon.world.entities.creatures.NPC;
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
        long seed = rand.nextInt();
        seed = -1380589433;
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        World w = World.getInstance();
        int floorCount = 5;
        List<Stair> prevStairs = new ArrayList<>();

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(40, 40);
            generator.generateLeafs(5, 10);
            generator.generateDoors(2, 3, 2);
            generator.generateWeightMap();
            generator.generateHalls();
            prevStairs = generator.generateStairs(prevStairs, (i == floorCount-1)? 0:1);
            generator.buildCells();
            generator.addFurniture();
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            w.addLevel(newFloor);
        }

        LiveDisplay liveDisplay = new LiveDisplay();

        Player testPlayer = new Player(Color.BLUE, "Player");
        w.addEntityRandomLoc(testPlayer, startFloor);
        w.setPlayer(testPlayer);

        Monster testMonster = new Monster(Color.GREEN, "Monster");
        w.addEntityRandomLoc(testMonster, startFloor);

        NPC testNPC = new NPC(Color.CYAN, "NPC");
        w.addEntityRandomLoc(testNPC, startFloor);

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
