package com.ewan.dunjeon.game;

import com.ewan.dunjeon.generation.FloorGenerator;
import com.ewan.dunjeon.graphics.UsingJogl;
import com.ewan.dunjeon.input.KeyBank;
import com.ewan.dunjeon.world.entities.AI.TestSubjectAI;
import com.ewan.dunjeon.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.world.floor.Floor;
import com.ewan.dunjeon.world.Dunjeon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import java.util.*;


public class Main {

    public static final Random rand = new Random();
    static final long UPDATE_DELAY = 16;
    private static KeyBank keyBank = new KeyBank();

    public static void main(String[] args) {


        System.out.println("Begin");
        generateWorld();


        new Thread(() -> {
            while (true) {
                updateCurrentWorld();
            }
        }).start();

        UsingJogl jogl = new UsingJogl();
        jogl.getCanvas().addKeyListener(keyBank);
        jogl.setVisible(true);
        jogl.setFocusable(true);
        jogl.requestFocusInWindow();
        jogl.start();

    }

    private static void generate_X_world_test(int worlds){
        for (int i = 0; i < worlds; i++) {
            System.out.println(i + " / " + worlds);
            generateWorld();
            updateCurrentWorld();
        }
    }

    private static List<Long> durations = new ArrayList<>();

    private static void updateCurrentWorld(int dataSamples){
        Dunjeon w = Dunjeon.getInstance();
        try {
            Thread.sleep(UPDATE_DELAY);
            long t0 = System.nanoTime();
            w.update(1.0D);
            long t1 = System.nanoTime();
            durations.add(t1 - t0);

            if(durations.size() == dataSamples){
                DescriptiveStatistics stats = new DescriptiveStatistics();
                durations.forEach(v -> stats.addValue((double)v/1000000D));
                System.out.println("******* WORLD UPDATE DATA *******");
                System.out.println("Data sample size : " + dataSamples);
                System.out.println("Mean duration : " + stats.getMean());
                System.out.println("Standard Deviation : " + stats.getStandardDeviation());
                System.out.println("Skewness : " + stats.getSkewness());
                durations.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        TestSubject testSubject = new TestSubject("Player");
        testSubject.addFixture(new Rectangle(1,1));
        testSubject.setMass(MassType.NORMAL);
        startFloor.addEntityRandomLoc(testSubject);
        d.setPlayer(testSubject);

        startFloor.addCreatureController(new TestSubjectAI(testSubject, keyBank));

    }
}
