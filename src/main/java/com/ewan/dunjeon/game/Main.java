package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.Stair;
import com.ewan.dunjeon.world.entities.creatures.Player;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.Dunjeon;
import org.dyn4j.geometry.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final Random rand = new Random();
    static final long UPDATE_DELAY = 16;

    public static void main(String[] args) {

        generateWorld();

        LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.startDrawing(Dunjeon.getInstance());

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
        Dunjeon w = Dunjeon.getInstance();
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

        Dunjeon.resetDunjeon();
        Dunjeon w = Dunjeon.getInstance();
        int floorCount = 1;
        List<Stair> prevStairs = new ArrayList<>();

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(100, 100);
            int hallWidth = 2; //Width of hallways
            int roomPadding = 2; //Extra walls between the room walls and the hallways


            generator.generateLeafs(15,-1, (hallWidth/2)+roomPadding);
            generator.generateDoors(1, 1, 2);
            generator.generateWeightMap();
            generator.generateHalls(hallWidth);
            generator.buildCells();
            generator.addFurniture();
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            w.addLevel(newFloor);

        }


        Player testPlayer = new Player("Player");
        testPlayer.addFixture(new Circle(0.3d));
        w.addEntityRandomLoc(testPlayer, startFloor);
        System.out.println(testPlayer.getWorldCenter());

//        Monster testMonster = Monster.generateExploringMonster(Color.GREEN, "Monster");
//        w.addEntityRandomLoc(testMonster, startFloor);


//        NPC testNPC = NPC.generateDumbNPC(Color.CYAN, "NPC");
//        w.addEntityRandomLoc(testNPC, startFloor);m


    }
}
