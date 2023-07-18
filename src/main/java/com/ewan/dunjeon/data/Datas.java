package com.ewan.dunjeon.data;

import com.ewan.dunjeon.graphics.RenderableElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.List;

/**
 * The heriarchy of data types defines what they are attached to. This allows datawrappers to restrict the type associated with them.
 * e.x CellData is an abstract class for any data that is per-cell
 * Cell color, cell physical state are all must all directly extend CellData
 */
public class Datas {

    public static abstract class CellData extends Data {

    }

    @Getter
    @AllArgsConstructor
    public static class CellEnterableData extends CellData {

        EnterableStatus enterableStatus;

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

    public abstract static class CellVisualData extends CellData{

    }


    public static abstract class EntityData extends Data {

    }

    @Getter
    @AllArgsConstructor
    public static class EntityVisualData extends EntityData {

        private final List<RenderableElement> renderableElementList;

    }

    @Getter
    @AllArgsConstructor
    public static class EntityPositionalData extends EntityData {

        private final Vector2 position;
        private final long floorUUID;

    }

    @AllArgsConstructor
    @Getter
    public static class EntityKineticData extends EntityData {

        private final Vector2 speed;
        private final double rotation;
        private final double rotationalSpeed;

    }

}
