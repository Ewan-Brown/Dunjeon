package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.graphics.RenderableObject;
import com.ewan.dunjeon.world.entities.EntityStateData;

import java.util.List;

public class EntityMemory extends Memory {

    public EntityMemory(EntityStateData stateData, List<RenderableObject> renderData) {
        this.stateData = stateData;
        this.renderData = renderData;
    }

    public void update(EntityMemory newMemory){
        this.stateData = newMemory.stateData;
        this.renderData = newMemory.renderData;
        this.isOldData = newMemory.isOldData;
    }

    private List<RenderableObject> renderData;
    private EntityStateData stateData;

    public List<RenderableObject> getRenderableObjects() {
        return renderData;
    }
    public EntityStateData getStateData() {
        return stateData;
    }
}
