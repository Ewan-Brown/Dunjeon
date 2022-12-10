package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//Contains cell memory data on cell and furniture
public class CellMemory extends Memory {
    public CellMemory(CellRenderData cellRenderData, FurnitureData fData, EnterableStatus e, int x, int y, boolean isHostWithinCell, java.util.List<BasicCell.CellSide> sides) {
        super();
        this.cellRenderData = cellRenderData;
        this.enterable = e;
        this.x = x;
        this.y = y;
        furnitureData = fData;
        entityWithinCell = isHostWithinCell;
        exploredStatus = (isHostWithinCell) ? ExploredStatus.EXPLORED_UNCHANGED : ExploredStatus.NEVER_EXPLORED;
    }

    public void update(CellMemory updatedMemoryData) {
        if (this.x != updatedMemoryData.x || this.y != updatedMemoryData.y) {
            throw new IllegalArgumentException();
        }

        //If this cell is newly enterable
        if(updatedMemoryData.enterable == EnterableStatus.OPEN && this.enterable == EnterableStatus.CLOSED) {
            exploredStatus = switch (this.getExploredStatus()) {
                case EXPLORED_DIFFERENT, EXPLORED_UNCHANGED -> ExploredStatus.EXPLORED_DIFFERENT;
                case NEVER_EXPLORED -> ExploredStatus.NEVER_EXPLORED;
            };
        }

        //If the cell has not changed
        else{
            if(updatedMemoryData.exploredStatus == ExploredStatus.EXPLORED_UNCHANGED){
                this.exploredStatus = ExploredStatus.EXPLORED_UNCHANGED;
            }else{
                updatedMemoryData.exploredStatus = this.exploredStatus;
            }
        }

        this.cellRenderData.update(updatedMemoryData.cellRenderData);
        this.enterable = updatedMemoryData.enterable;
        this.entityWithinCell = updatedMemoryData.entityWithinCell;
        this.furnitureData = updatedMemoryData.furnitureData;
        this.isOldData = updatedMemoryData.isOldData();
    }

    public Point getPoint(){return new Point(x,y);}

    private final int x;
    private final int y;

    public final CellRenderData cellRenderData; // For player
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

        public enum CellSideVisibility{
            NEVER_SEEN,
            SEEN_PREVIOUSLY,
            SEE_PRESENT;
        }

        public CellRenderData(BasicCell c, List<BasicCell.CellSide> visibleSides, boolean shouldRenderWalls) {
            this.color = c.color;
            Arrays.stream(BasicCell.CellSide.values()).forEach(cellSide -> sides.put(cellSide, CellSideVisibility.NEVER_SEEN));
            if(visibleSides != null) {
                for (BasicCell.CellSide side : visibleSides) {
                    sides.put(side, CellSideVisibility.SEE_PRESENT);
                }
            }
            this.shouldRenderWalls = shouldRenderWalls;
        }

        public void update(CellRenderData data){
            this.color = data.color;

            sides.replaceAll((cellSide, newVisibility) -> data.sides.put(cellSide,
                    switch (newVisibility) {
                        case SEEN_PREVIOUSLY -> throw new IllegalStateException("*NEW* render data shouldn't' claim it's seen something in the past");
                        case SEE_PRESENT -> CellSideVisibility.SEE_PRESENT;
                        default -> data.sides.get(cellSide);
                    }
            ));
            this.shouldRenderWalls = data.shouldRenderWalls;
        }

        private Color color;
        private final HashMap<BasicCell.CellSide, CellSideVisibility> sides = new HashMap<>();
        private boolean shouldRenderWalls;

        public Color getColor() {
            return color;
        }
        public CellSideVisibility isSideVisible(BasicCell.CellSide s){
            return sides.get(s);
        }

        public HashMap<BasicCell.CellSide, CellSideVisibility> getSides() {
            return sides;
        }

        public boolean shouldRenderWalls() {
            return shouldRenderWalls;
        }
    }


}
