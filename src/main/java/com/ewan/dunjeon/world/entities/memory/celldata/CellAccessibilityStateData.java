package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.world.entities.memory.StateData;

/**
 * Represents a snapshot of the Accessibility of a cell at a given point in time
 */
public class CellAccessibilityStateData extends StateData {

    final Accessibility accessibility;

    public CellAccessibilityStateData(double time, Accessibility access) {
        super(time);
        accessibility = access;
    }

    public static enum Accessibility{
        OPEN, WALL, FURNITURE_BLOCKING, ENTITY_BLOCKING
    }

}
