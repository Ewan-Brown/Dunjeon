package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.generation.GeneratorsMisc;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Monster;
import com.ewan.dunjeon.world.level.LevelGenerator;

import java.awt.*;

import static com.ewan.dunjeon.generation.Main.rand;

public class TestGameLogic {
    public static void main(String[] args) {
        long seed = rand.nextInt();
        seed = -921506715L;
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        World w = World.getInstance();

        FloorGenerator generator = new FloorGenerator(100, 100);
        generator.generateLeafs(8,100);
        generator.generateDoors(2,3, 2);
        generator.generateWeightMap();
        generator.generateHalls();

//        Level testLevel = LevelGenerator.createLevel(generator.getGrid());
        Level testLevel = LevelGenerator.createLevel(GeneratorsMisc.generateRandomMap(20, 20, 0.95f));
        w.addLevel(testLevel);
        LiveDisplay liveDisplay = new LiveDisplay();

        Entity testPlayer = new Entity(Color.BLUE, 1, 10);
        w.addEntityRandomLoc(testPlayer, testLevel);
        w.setPlayer(testPlayer);

        Monster testMonster = new Monster(new Color(0, 255, 0));
        w.addEntityRandomLoc(testMonster, testLevel);

        liveDisplay.startDrawing(testLevel, w);
        while (true) {
            w.getPlayer().updateViewRange();
            w.update();
        }
    }
}
