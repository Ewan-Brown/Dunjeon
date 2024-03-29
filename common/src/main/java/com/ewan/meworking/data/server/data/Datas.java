package com.ewan.meworking.data.server.data;

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

    public static abstract class WorldData extends Data {

    }

    public static abstract class CellData extends Data {

    }

    @Getter
    @AllArgsConstructor
    public static class CellEnterableData extends CellData {

        EnterableStatus enterableStatus;

        public enum EnterableStatus{
            BLOCKED, //This cell isn't enterable! Don't even try.
            ENTERABLE, //This entity is certainly enterable! Go ahead.
            INTERACTABLE, //This entity may be enterable if we interact with it...?
            UNKNOWN //No clue.
        }

    }

    public abstract static class CellVisualData extends CellData{

    }


    public static abstract class EntityData extends Data {

    }

    @Getter
    @AllArgsConstructor
    public static class EntityVisualData extends EntityData {

        private List<RenderableElement> renderableElementList;

    }

    @Getter
    @AllArgsConstructor
    public static class EntityPositionalData extends EntityData {

        private Vector2 position;
        private long floorUUID;

    }

    @AllArgsConstructor
    @Getter
    public static class EntityKineticData extends EntityData {

        private Vector2 speed;
        private double rotation;
        private double rotationalSpeed;

    }

}
