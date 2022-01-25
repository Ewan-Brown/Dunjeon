package com.ewan.dunjeon.generation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Display {
    public static void printMap(int[][] map){
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[j][i]);
            }
            System.out.println();
        }
    }

    private static Point nextPoint = new Point(0,0);
    private static HashMap<Integer, Color> colorScheme = new HashMap<>();
    static {
        colorScheme.put(GeneratorsMisc.BLOCK, Color.BLACK);
        colorScheme.put(GeneratorsMisc.WALL, Color.GRAY);
        colorScheme.put(GeneratorsMisc.DOOR, new Color(150, 50, 0)); //Brown
        colorScheme.put(GeneratorsMisc.OPEN, Color.WHITE);
        colorScheme.put(GeneratorsMisc.HALL, Color.BLUE);

    }




    public static void drawMap(int[][] map){
        int windowSize = 500;
        int size = (int)(Math.ceil((float)windowSize / (float)map.length));
        JPanel panel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[i].length; j++) {
                        int val = map[j][i];
                        g.setColor(colorScheme.get(val));
                        g.fillRect(i * size, j * size, size, size);
                    }
                }
            }
        };
        JFrame frame = new JFrame("test");
        frame.setLocation(nextPoint);
        frame.add(panel);
        frame.setSize(size*map.length + 20,size*map[0].length + 40);
        frame.setVisible(true);
        nextPoint = new Point(nextPoint.x + frame.getWidth(), nextPoint.y);
//        nextPoint = new Point(nextPoint.x + 15, nextPoint.y+ 15);

    }
}
