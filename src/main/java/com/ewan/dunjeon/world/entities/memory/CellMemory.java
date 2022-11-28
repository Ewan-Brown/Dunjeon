package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;

//Contains cell memory data on cell and furniture
public class CellMemory extends Memory {
    public CellMemory(CellRenderData cellRenderData, FurnitureData fData, EnterableStatus e, int x, int y, boolean isHostWithinCell) {
        super();
        this.cellRenderData = cellRenderData;
        this.enterable = e;
        this.x = x;
        this.y = y;
        furnitureData = fData;
        entityWithinCell = isHostWithinCell;
        exploredStatus = (isHostWithinCell) ? ExploredStatus.EXPLORED_UNCHANGED : ExploredStatus.NEVER_EXPLORED;
    }

    public void update(CellMemory newMemory) {
        if (this.x != newMemory.x || this.y != newMemory.y) {
            throw new IllegalArgumentException();
        }

        //If this cell is newly enterable
        if(newMemory.enterable == EnterableStatus.OPEN && this.enterable == EnterableStatus.CLOSED) {
            exploredStatus = switch (this.getExploredStatus()) {
                case EXPLORED_DIFFERENT, EXPLORED_UNCHANGED -> ExploredStatus.EXPLORED_DIFFERENT;
                case NEVER_EXPLORED -> ExploredStatus.NEVER_EXPLORED;
            };
        }
        //If the cell has not changed
        else{
            if(newMemory.exploredStatus == ExploredStatus.EXPLORED_UNCHANGED){
                this.exploredStatus = ExploredStatus.EXPLORED_UNCHANGED;
            }else{
                newMemory.exploredStatus = this.exploredStatus;
            }
        }

        this.cellRenderData = newMemory.cellRenderData;
        this.enterable = newMemory.enterable;
        this.entityWithinCell = newMemory.entityWithinCell;
        this.furnitureData = newMemory.furnitureData;
        this.isOldData = newMemory.isOldData();
    }

    public Point getPoint(){return new Point(x,y);}

    private final int x;
    private final int y;

    public CellRenderData cellRenderData; // For player
    public EnterableStatus enterable; // For AI

    /**<p>
     * Represents status of the exploration of the cell.
     * </p>
     * <ul>
         * <li>Has the cell never been explored</li>
         * <li>Has the cell been explored, but changed</li>
         * <li>Has the cell been explored, and hasnt appeared to have changed</li>
     * </ul>
     */
    public enum ExploredStatus{

        NEVER_EXPLORED,
        EXPLORED_DIFFERENT,
        EXPLORED_UNCHANGED;
    }

    boolean entityWithinCell;

    ExploredStatus exploredStatus;

    public ExploredStatus getExploredStatus(){
        return exploredStatus;
    }

    public FurnitureData furnitureData;

    public enum EnterableStatus {
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
