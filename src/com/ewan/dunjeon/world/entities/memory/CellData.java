package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;

//Contains cell memory data on cell and furniture
public class CellData {
    public CellData(CellVisualData visual, EnterableStatus e, int x, int y) {
        this.visual = visual;
        isOldData = false;
        this.enterable = e;
        this.x = x;
        this.y = y;
    }

    private final int x;
    private final int y;

    public final CellVisualData visual; // For player

    public final EnterableStatus enterable; // For AI
    boolean isOldData;

    public boolean isOldData(){return isOldData;}

    public enum EnterableStatus{
        OPEN, //An open cell, an open door
        CLOSED //A wall, a locked door
    }

    public static class FurnitureData {
        private int xCenter;
        private int yCenter;
        private boolean enterable;

        public static class FurnitureVisualData{

            public FurnitureVisualData(Furniture f){
                color = f.getColor();
            }
            private Color color;
            private int offsetX;
            private int offsetY;
        }

    }

    public static class CellVisualData {

        public CellVisualData(BasicCell c) {
            this.color = c.color;
        }

        private Color color;

        public Color getColor(){
            return color;
        }

    }
}
