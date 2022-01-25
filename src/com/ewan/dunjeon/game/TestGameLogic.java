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
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        World w = World.getInstance();

//        FloorGeneratora generator = new FloorGenerator(30, 30);
//        generator.generateLeafs(10,1);
//        generator.generateDoors(2,3, 2);
//        generator.generateWeightMap();
//        generator.generateHalls();

//        Level testLevel = LevelGenerator.createLevel(generator.getGrid());
        Level testLevel = LevelGenerator.createLevel(GeneratorsMisc.generateRandomMap(30, 30, 0.8f));
        w.addLevel(testLevel);
        LiveDisplay liveDisplay = new LiveDisplay();

        Entity testPlayer = new Entity(Color.BLUE, 10);
        w.addEntityRandomLoc(testPlayer, testLevel);
        w.setPlayer(testPlayer);

        Monster testMonster = new Monster(new Color(0, 141, 0));
        w.addEntityRandomLoc(testMonster, testLevel);

        liveDisplay.startDrawing(testLevel, w);
        while (true) {
            w.update();
        }
    }
}
