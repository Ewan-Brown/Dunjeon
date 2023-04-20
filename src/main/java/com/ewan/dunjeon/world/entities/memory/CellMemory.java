package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

//Contains cell memory data on cell and furniture
public class CellMemory extends Memory {
    public CellMemory(CellRenderData cellRenderData, FurnitureData fData, EnterableStatus e, int x, int y, boolean isHostWithinCell, java.util.List<WorldUtils.Side> sides) {
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

    public String toString(){
        return String.format("%d, %d", getPoint().x, getPoint().y);
    }

    public static class FurnitureData {
        private double xCenter;
        private double yCenter;
        private boolean enterable;
        private double size;
        private boolean visible;
        private boolean interactable; //For use with player
        public FurnitureRenderData furnitureRenderData;

        public FurnitureData(double xCenter, double yCenter, double size, boolean enterable, boolean visible, boolean interactable, FurnitureRenderData furnitureRenderData) {
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

        public double getPosX() {
            return xCenter;
        }

        public double getPosY() {
            return yCenter;
        }

        public boolean isEnterable() {
            return enterable;
        }

        public boolean isInteractable() {
            return interactable;
        }

        public double getSize() {
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

        public CellRenderData(BasicCell c, List<WorldUtils.Side> visibleSides, boolean shouldRenderWalls) {
            this.color = c.color;
            Arrays.stream(WorldUtils.Side.values()).forEach(cellSide -> sides.put(cellSide, CellSideVisibility.NEVER_SEEN));
            if(visibleSides != null) {
                for (WorldUtils.Side side : visibleSides) {
                    sides.put(side, CellSideVisibility.SEE_PRESENT);
                }
            }
            this.shouldRenderWalls = shouldRenderWalls;
        }

        public void update(CellRenderData newData){
            this.color = newData.color;

            sides.forEach((cellSide, oldVisibilityStatus) -> {
                CellSideVisibility newVisibilityStatus = newData.sides.get(cellSide);
                CellSideVisibility calculatedVisibility = switch (newVisibilityStatus) {
                    case SEEN_PREVIOUSLY -> throw new IllegalStateException("NEW render data shouldn't claim that it has data about the past");
                    case SEE_PRESENT -> CellSideVisibility.SEE_PRESENT;
                    case NEVER_SEEN -> (oldVisibilityStatus == CellSideVisibility.NEVER_SEEN)
                                    ? CellSideVisibility.NEVER_SEEN
                                    : CellSideVisibility.SEEN_PREVIOUSLY;
                };
                sides.put(cellSide, calculatedVisibility);
            });

            this.shouldRenderWalls = newData.shouldRenderWalls;
        }

        private Color color;
        private final HashMap<WorldUtils.Side, CellSideVisibility> sides = new HashMap<>();
        private boolean shouldRenderWalls;

        public Color getColor() {
            return color;
        }
        public CellSideVisibility isSideVisible(WorldUtils.Side s){
            return sides.get(s);
        }

        public HashMap<WorldUtils.Side, CellSideVisibility> getSides() {
            return sides;
        }

        public boolean shouldRenderWalls() {
            return shouldRenderWalls;
        }
    }


}
