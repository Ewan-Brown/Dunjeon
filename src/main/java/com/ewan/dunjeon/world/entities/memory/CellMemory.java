package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;

//Contains cell memory data on cell and furniture
public class CellMemory extends Memory {
    public CellMemory(CellRenderData cellRenderData, FurnitureData fData, EnterableStatus e, int x, int y, boolean isExplored) {
        super();
        this.cellRenderData = cellRenderData;
        this.enterable = e;
        this.x = x;
        this.y = y;
        furnitureData = fData;
        hasBeenExplored = isExplored;
    }

    public void update(CellMemory newMemory) {
        if (this.x != newMemory.x || this.y != newMemory.y) {
            throw new IllegalArgumentException();
        }
        this.cellRenderData = newMemory.cellRenderData;
        this.enterable = newMemory.enterable;
        this.hasBeenExplored = this.hasBeenExplored || newMemory.hasBeenExplored;
        this.furnitureData = newMemory.furnitureData;
        this.isOldData = newMemory.isOldData();
    }

    public Point getPoint(){return new Point(x,y);}

    private int x;
    private int y;

    public CellRenderData cellRenderData; // For player
    public EnterableStatus enterable; // For AI

    private boolean hasBeenExplored = false;

    public FurnitureData furnitureData;

    public enum EnterableStatus {
        OPEN, //An open cell, an open door
        CLOSED //A wall, a locked door
    }

    public boolean hasBeenExplored() {
        return hasBeenExplored;
    }

    public static class FurnitureData {
        private float xCenter;
        private float yCenter;
        private boolean enterable;
        private float size;
        private boolean visible;
        private boolean interactable; //For use with player
        public FurnitureRenderData furnitureRenderData;

        public FurnitureData(float xCenter, float yCenter, float size, boolean enterable, boolean visible, boolean interactable, FurnitureRenderData furnitureRenderData) {
            this.xCenter = xCenter;
            this.yCenter = yCenter;
            this.enterable = enterable;
            this.visible = visible;
            this.size = size;
            this.furnitureRenderData = furnitureRenderData;
            this.interactable = interactable;
        }

        public static class FurnitureRenderData {
            public FurnitureRenderData(Furniture f) {
                color = f.getColor();
            }

            private Color color;

            public Color getColor() {
                return color;
            }
        }

        public float getPosX() {
            return xCenter;
        }

        public float getPosY() {
            return yCenter;
        }

        public boolean isEnterable() {
            return enterable;
        }

        public boolean isInteractable() {
            return interactable;
        }

        public float getSize() {
            return size;
        }

        public boolean isVisible() {
            return visible;
        }

    }

    public static class CellRenderData {

        public CellRenderData(BasicCell c) {
            this.color = c.color;
        }

        private Color color;

        public Color getColor() {
            return color;
        }

    }


}
