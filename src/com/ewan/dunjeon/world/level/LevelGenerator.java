package com.ewan.dunjeon.world.level;

import com.ewan.dunjeon.generation.Leaf;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Door;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.util.List;

import static com.ewan.dunjeon.generation.FloorGenerator.*;

public class LevelGenerator {

    static Color DARK_GRAY = new Color(60, 60, 60);

    public static Level createLevel(int[][] map){
        BasicCell[][] cells = new BasicCell[map.length][map[0].length];
        Level lev = new Level();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                int val = map[y][x];
                boolean filled = false;
                Color c;
                Furniture f = null;
                switch(val){
                    case BLOCK:
                        filled = true;
                        c = Color.DARK_GRAY;
                        break;
                    case WALL:
                        filled = true;
                        c = Color.LIGHT_GRAY;
                        break;
                    case OPEN:
                    case HALL:
                        c = Color.GRAY;
                        filled = false;
                        break;
                    case DOOR:
                        c = Color.WHITE;
                        f = new Door(false);
                        filled = false;
                        break;
                    default:
                        c = Color.RED;
                }
                BasicCell cell = new BasicCell(x, y, lev, filled, c);
                if(f != null) {
                    cell.setFurniture(f);
                }
                cells[y][x] = cell;

            }
        }
        lev.cells = cells;
        return lev;
    }
}
