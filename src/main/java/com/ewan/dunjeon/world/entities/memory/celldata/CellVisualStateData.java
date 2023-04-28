package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.StateData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CellVisualStateData extends StateData {

    final Color color;
    List<WorldUtils.Side> visibleSides = new ArrayList<>();


    public CellVisualStateData(double time, Color c) {
        super(time);
        color = c;
    }
}
