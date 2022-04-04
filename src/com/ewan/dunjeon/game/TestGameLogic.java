package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Monster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.ewan.dunjeon.generation.Main.rand;

public class TestGameLogic {

    static final long UPDATE_DELAY = 2;

    public static void main(String[] args) {
        long seed = rand.nextInt();
//        seed = -1427703176;
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        World w = World.getInstance();
        int floorCount = 5;
        List<Stair> prevStairs = new ArrayList<>();

        Floor startFloor = null;

        for (int i = 0; i < 5; i++) {
            FloorGenerator generator = new FloorGenerator(20, 20);
            generator.generateLeafs(5, 3);
            generator.generateDoors(2, 3, 2);
            generator.generateWeightMap();
            generator.generateHalls();
            prevStairs = generator.generateStairs(prevStairs, (i ==4)? 0:1);
            generator.buildCells();
            generator.addFurniture();
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            w.addLevel(newFloor);
        }

        LiveDisplay liveDisplay = new LiveDisplay();

        Entity testPlayer = new Entity(Color.BLUE, 0, 10);
        w.addEntityRandomLoc(testPlayer, startFloor);
        w.setPlayer(testPlayer);

        liveDisplay.startDrawing(w);
        while (true) {
            w.getPlayer().updateViewRange();
            w.update();
            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
