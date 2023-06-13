package com.ewan.dunjeon.world.data;

import com.ewan.dunjeon.graphics.RenderableElement;

import java.util.List;

public class Datum {
    public class EntitySightData extends Data {

        List<IndividualEntityData> entityData;

        public EntitySightData(double timestamp) {
            super(timestamp);
        }

        @lombok.Data
        public class IndividualEntityData{
            long entityUUID;
            List<RenderableElement> renderableElementList;
        }
    }

}
