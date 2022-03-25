package com.ewan.dunjeon.generation;


import java.util.Random;

public class Main {

    public static Random rand = new Random();
    public static void main(String[] args) {

        long seed = rand.nextInt();
        System.out.println("SEED USED : " + seed);
        rand.setSeed(seed);

        //Procedurally generate and print out results at each stage
//        int size = 200;
//        FloorGenerator generator = new FloorGenerator(size,size);
//        generator.generateLeafs(10,100);
//        Display.drawMap(generator.getGrid());
//        generator.generateDoors(2,3, 2);
//        Display.drawMap(generator.getGrid());
//        generator.generateWeightMap();
//        generator.generateHalls();
//        Display.drawMap(generator.getGrid());
    }


    public static void generateWithRule4() {
        int[][] map = GeneratorsMisc.generateRandomMap(150,150, 0.4f);
        for (int i = 0; i < 5; i++) {
            Display.drawMap(map);
            map = Cellular.updateWithRule(map, Cellular.sumGreaterThan4);
        }
    }

}
