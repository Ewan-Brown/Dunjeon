package com.ewan.dunjeon.data;

import com.ewan.dunjeon.graphics.RenderableElement;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class Datas {

    public static abstract class CellData extends Data {

        protected CellData(double timestamp) {
            super(timestamp);
        }

    }

    @Getter
    public static class CellEnterableData extends CellData {

        EnterableStatus enterableStatus;

        protected CellEnterableData(double timestamp, EnterableStatus status) {
            super(timestamp);
            enterableStatus = status;
        }

        public enum EnterableStatus{
            /**
             * This cell isn't enterable! Don't even try.
             */
            BLOCKED,
            /**
             * This entity is certainly enterable! Go ahead.
             */
            ENTERABLE,
            /**
             * This entity may be enterable if we interact with it...?
             */
            INTERACTABLE,
            /**
             * No clue.
             */
            UNKNOWN
        }

    }

    public static class CellVisualData extends CellData implements UpdateableData<CellVisualData>{
        protected CellVisualData(double timestamp) {
            super(timestamp);
        }

        @Override
        public void updateWithData(CellVisualData data) {

        }

    }


    @Getter
    public static abstract class EntityData extends Data {

        protected EntityData(double timestamp) {
            super(timestamp);
        }
    }

    @Getter
    public static class EntityVisualData extends EntityData {

        private final List<RenderableElement> renderableElementList;

        public EntityVisualData(double timestamp, List<RenderableElement> renderableElements) {
            super(timestamp);
            renderableElementList = renderableElements;
        }
    }

    @Getter
    public static class EntityPositionalData extends EntityData {

        private final Vector2 position;
        private final long floorUUID;

        public EntityPositionalData(double timestamp, Vector2 pos, long floorID) {
            super(timestamp);
            System.out.println("[DATA] EntityPositionalData created with : " + pos.toString());
            position = pos;
            floorUUID = floorID;
        }
    }

    @Getter
    public static class EntityKineticData extends EntityData {

        private final Vector2 speed;
        private final double rotation;
        private final double rotationalSpeed;

        public EntityKineticData(double timestamp, Vector2 speed, double rotation, double rotationalSpeed) {
            super(timestamp);
            this.speed = speed;
            this.rotation = rotation;
            this.rotationalSpeed = rotationalSpeed;
        }
    }

}
