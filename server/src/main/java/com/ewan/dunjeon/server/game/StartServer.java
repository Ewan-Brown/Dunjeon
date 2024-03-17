package com.ewan.dunjeon.server.game;

import com.ewan.dunjeon.server.networking.ServerManager;
import com.ewan.dunjeon.server.generation.FloorGenerator;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.dunjeon.server.world.floor.Floor;
import com.ewan.dunjeon.server.world.Dunjeon;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.*;

import java.util.*;


public class StartServer {

    public static final Random rand = new Random();
    private static final int entityCount = 0;
    static Logger logger = LogManager.getLogger();

    @SneakyThrows
    public static void main(String[] args) {
        logger.info("Starting server code");
        generateWorld();
        new Thread(ServerManager::runServer).start();

        new Thread(() -> {
            while (true) {
                updateCurrentWorld();
            }
        }).start();

    }

    private static final long DESIRED_FRAMETIME_NS = 16000000;

    private static void updateCurrentWorld(){
        Dunjeon w = Dunjeon.getInstance();
        try {
            Thread.sleep(DESIRED_FRAMETIME_NS/1000000L);
            w.update(1.0D);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ServerManager.sendDataToClients();
    }

    private static void generateWorld(){
        long seed = rand.nextInt();
        seed = -709714631;
        rand.setSeed(seed);

        logger.info("SEED USED : " + seed);

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

        for (int i = 0; i < entityCount; i++) {
            TestSubject npcTestSubject = new TestSubject("NPC");
            npcTestSubject.addFixture(new Rectangle(0.5,0.5));
            npcTestSubject.setMass(new Mass(new Vector2(),1,100));
            startFloor.addEntityRandomLoc(npcTestSubject);
        }
    }
}
