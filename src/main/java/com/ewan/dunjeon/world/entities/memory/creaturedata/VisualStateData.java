package com.ewan.dunjeon.world.entities.memory.creaturedata;

import com.ewan.dunjeon.graphics.RenderableElement;
import com.ewan.dunjeon.world.entities.memory.StateData;

import java.util.List;

public class VisualStateData extends StateData {

    //Rendering Data
    final List<RenderableElement> renderableElements;

    public VisualStateData(double time, List<RenderableElement> renderableElements) {
        super(time);
        this.renderableElements = renderableElements;
    }
}
