package com.ewan.dunjeon.world.entities.memory.celldata;

import static com.ewan.dunjeon.world.WorldUtils.Side;

import java.util.HashMap;
/**
 * Represents what Creature A knows about Cell B.
 * Most info should be absolute (not relative to creature A) and
 * transferrable/shareable from Creature A to another Creature
 */
public class CellKnowledge {

    final int x;
    final int y;
    boolean hasVisited; //Has the host entity actually visited this cell?

    public CellKnowledge(int x, int y){
        this.x = x;
        this.y = y;
    }

    HashMap<Side, Boolean> sideVisibility = new HashMap<>();

    CellAccessibilityStateData lastKnownAccessibility;
    CellEntitiesStateData lastKnownEntitiesWithin;
    CellVisualStateData lastVisualData;
}
