package com.ewan.dunjeon.world.data;

import com.ewan.dunjeon.graphics.RenderableElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class Datum {
    @Getter
    public static class EntityVisualData extends Data {

        List<IndividualEntityData> entityDatum;

        public EntityVisualData(double timestamp) {
            super(timestamp);
        }

        @Getter
        @AllArgsConstructor
        public static class IndividualEntityData{
            long entityUUID;
            List<RenderableElement> renderableElementList;
        }
    }

    @Getter
    public static class CellVisualData extends Data {

        List<IndividualCellData> cellDatum;

        protected CellVisualData(double timestamp) {
            super(timestamp);
        }

        @Getter
        @AllArgsConstructor
        public static class IndividualCellData{
            long entityUUID;
            int x;
            int y;

        }
    }


}
