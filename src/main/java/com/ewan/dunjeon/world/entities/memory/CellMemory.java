package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;

//Contains cell memory data on cell and furniture
public class CellMemory extends Memory {
    public CellMemory(CellRenderData cellRenderData, FurnitureData fData, EnterableStatus e, int x, int y) {
        super();
        this.cellRenderData = cellRenderData;
        this.enterable = e;
        this.x = x;
        this.y = y;
        furnitureData = fData;
    }

    private final int x;
    private final int y;

    public final CellRenderData cellRenderData; // For player
    public final EnterableStatus enterable; // For AI

    public final FurnitureData furnitureData;

    public enum EnterableStatus{
        OPEN, //An open cell, an open door
        CLOSED //A wall, a locked door
    }

    public static class FurnitureData {
        private float xCenter;
        private float yCenter;
        private boolean enterable;
        private float size;
        private boolean visible;
        private boolean interactable; //For use with player
        public final FurnitureRenderData furnitureRenderData;

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
            public FurnitureRenderData(Furniture f){
                color = f.getColor();
            }
            private final Color color;
            public Color getColor(){return color;}
        }

        public float getCenterX() {return xCenter;}
        public float getCenterY() {return yCenter;}
        public boolean isEnterable() {return enterable;}
        public boolean isInteractable() {return interactable;}
        public float getSize() {return size;}
        public boolean isVisible (){return visible;}

    }

    public static class CellRenderData {

        public CellRenderData(BasicCell c) {
            this.color = c.color;
        }

        private Color color;

        public Color getColor(){
            return color;
        }

    }
}
