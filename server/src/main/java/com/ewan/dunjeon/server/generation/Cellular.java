package com.ewan.dunjeon.server.generation;

public class Cellular {
    public static int[][] updateWithRule(int[][] map, Rule rule){
        int[][] new_map = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                int livingNeighbors = 0;
                for (int k = -1; k < 2; k++) {
                    for (int l = -1; l < 2; l++) {
                        int x = i + k;
                        int y = j + l;
                        if(k == 0 && l == 0 || x < 0 || x >= map.length || y < 0 || y >= map[i].length) continue;
                        livingNeighbors += map[y][x];
                    }
                }
               new_map[i][j] = (rule.calculate(map[i][j],livingNeighbors)) ? 1 : 0;
            }
        }
        return new_map;
    }

    public static final Rule sumGreaterThan4 = (center, neighbors) -> ((neighbors + center) > 4);
    public static final Rule ruleBlank = (center, neighbors) -> false;

    public interface Rule {
        boolean calculate(int center, int neighbors);
    }
}
