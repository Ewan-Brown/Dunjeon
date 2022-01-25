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
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int val = map[j][i];
                boolean filled = false;
                Color c;
                Furniture f = null;
                switch(val){
                    case BLOCK:
                        filled = true;
                        c = Color.BLACK;
                        break;
                    case WALL:
                        filled = true;
                        c = DARK_GRAY;
                        break;
                    case OPEN:
                        c = Color.LIGHT_GRAY;
                        break;
                    case DOOR:
                        c = Color.LIGHT_GRAY;
                        f = new Door(false);
                        break;
                    case HALL:
                        c = Color.GRAY;
                        filled = false;
                        break;
                    default:
                        c = Color.RED;
                }
                BasicCell cell = new BasicCell(j, i, lev, filled, c);
                if(f != null) {
                    cell.setFurniture(f);
                }
                cells[j][i] = cell;

            }
        }
        lev.cells = cells;
        return lev;
    }
}
