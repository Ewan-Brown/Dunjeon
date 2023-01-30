package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.graphics.RenderableObject;

import java.util.List;

/**
 * Bundle of data describing a hosts' memory of an observed entity
 *
 * </p> Contains data describing the state of the entity,
 */
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

    public List<RenderableObject> getRenderData() {
        return renderData;
    }
    public EntityStateData getStateData() {
        return stateData;
    }
}
