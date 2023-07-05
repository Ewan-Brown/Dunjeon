package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.Graphics2DDisplay;
import com.ewan.dunjeon.graphics.UsingJogl;
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


        System.out.println("Begin");
        generateWorld();


        new Thread(() -> {
            while (true) {
                updateCurrentWorld();
            }
        }).start();

        UsingJogl jogl = new UsingJogl();
        jogl.setVisible(true);
        jogl.start();

    }

    private static void generate_X_world_test(int worlds){
        for (int i = 0; i < worlds; i++) {
            System.out.println(i + " / " + worlds);
            generateWorld();
            updateCurrentWorld();
        }
    }

    private static void updateCurrentWorld(){
        Dunjeon w = Dunjeon.getInstance();
        try {
            Thread.sleep(UPDATE_DELAY);
            w.update(1.0D);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void generateWorld(){
        long seed = rand.nextInt();
        System.out.println("SEED USED : " + seed);
        rand.setSeed(1082347570);

        Dunjeon.resetDunjeon();
        Dunjeon d = Dunjeon.getInstance();
        int floorCount = 1;

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(20, 20);
            int hallWidth = 2; //Width of hallways
            int roomPadding = 0; //Extra walls between the room walls and the hallways

            generator.generateLeafs(5,-1, (hallWidth/2)+roomPadding);
            generator.generateDoors(1, 1, 2);
            generator.generateWeightMap();
            generator.generateHalls(hallWidth);
            generator.buildCells();
            Floor newFloor = generator.getFloor();
            if(startFloor == null){
                startFloor = newFloor;
            }
            d.addLevel(newFloor);

        }


        Player testPlayer = new Player("Player");
        testPlayer.addFixture(new Circle(0.3d));
        d.addEntityRandomLoc(testPlayer, startFloor);
        d.setPlayer(testPlayer);

//        Monster testMonster = Monster.generateExploringMonster(Color.GREEN, "Monster");
//        w.addEntityRandomLoc(testMonster, startFloor);


//        NPC testNPC = NPC.generateDumbNPC(Color.CYAN, "NPC");
//        w.addEntityRandomLoc(testNPC, startFloor);m


    }
}
