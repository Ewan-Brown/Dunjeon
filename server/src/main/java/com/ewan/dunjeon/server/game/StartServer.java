package com.ewan.dunjeon.server.game;

import com.ewan.dunjeon.server.networking.ServerManager;
import com.ewan.dunjeon.server.generation.FloorGenerator;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.ClientBasedTestSubjectController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.server.world.floor.Floor;
import com.ewan.dunjeon.server.world.Dunjeon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.dyn4j.geometry.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class StartServer {

    public static final Random rand = new Random();
    static final long UPDATE_DELAY = 16;
    private static final int entityCount = 0;

    public static void main(String[] args) {

        System.out.println("Starting server code");
        generateWorld();
        new Thread(ServerManager::runServer).start();

        new Thread(() -> {
            while (true) {
                updateCurrentWorld();
                if(all_durations.size() == 1000){

                    try {
                        PrintWriter outFile = new PrintWriter(new FileWriter("data_" + entityCount+"entities" + ".csv"));
                        all_durations.forEach(i-> outFile.print(i + ", "));
                        outFile.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }

    //For debugging
    private static final List<Long> durations = new ArrayList<>();
    private static final List<Long> all_durations = new ArrayList<>();

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
                all_durations.addAll(durations);
                durations.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final long DESIRED_FRAMETIME_NS = 16000000;

    private static void updateCurrentWorld(){
//        System.out.println("StartServer.updateCurrentWorld");
        Dunjeon w = Dunjeon.getInstance();
        try {
            Thread.sleep(DESIRED_FRAMETIME_NS/1000000L);
            w.update(1.0D);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ServerManager.sendDataToClients();
    }

    private static void generateWorld(){
        System.out.println("StartServer.generateWorld");
        long seed = rand.nextInt();
        seed = -709714631;
        rand.setSeed(seed);

        System.out.println("SEED USED : " + seed);

        Dunjeon d = Dunjeon.getInstance();
        int floorCount = 1;

        Floor startFloor = null;

        for (int i = 0; i < floorCount; i++) {
            FloorGenerator generator = new FloorGenerator(40, 40);
            int hallWidth = 2; //Width of hallways
            int roomPadding = 0; //Extra walls between the room walls and the hallways

            generator.generateLeafs(7,-1, (hallWidth/2)+roomPadding);
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

//        TestSubject testSubject = new TestSubject("Player", true);
//        testSubject.addFixture(new Rectangle(0.5,0.5));
//        testSubject.setMass(new Mass(new Vector2(),1,1));
//        startFloor.addEntityRandomLoc(testSubject);
//
//        startFloor.addCreatureController(new ClientBasedTestSubjectController(testSubject));

        for (int i = 0; i < entityCount; i++) {
            TestSubject npcTestSubject = new TestSubject("NPC");
            npcTestSubject.addFixture(new Rectangle(0.5,0.5));
            npcTestSubject.setMass(new Mass(new Vector2(),1,1));
            startFloor.addEntityRandomLoc(npcTestSubject);
//            startFloor.addCreatureController(new TestSubjectAIController(npcTestSubject));
        }
    }
}
