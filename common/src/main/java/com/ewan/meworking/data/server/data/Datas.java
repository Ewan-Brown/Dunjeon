package com.ewan.meworking.data.server.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.List;

/**
 * The base class of data types defines what they are attached to. This allows datawrappers to restrict the type associated with them.
 * e.x CellData is an abstract class for any data that is per-cell
 * Cell color, cell physical state must all directly extend CellData
 * </p>
 * A few rules:
 * <ul>
 *     <li>Two Class layers here, 'Category' and 'Exact' </li>
 *     <li>Each Category layer must be abstract and directly extend Data - and may not have ANY fields or constructors</li>
 *     <li>Each Exact data type must directly extend a Category type</li>
 *     <li>Each Exact type must have all of the necessary fields declared private </li>
 *     <li>Each Exact type needs @Getter and @AllArgsConstructor</li>
 * </ul>
 * Note that no fields are final. This is to make deserialization easier :)
 */
public class Datas {

    public static abstract class WorldData extends Data {

    }

    public static abstract class CellData extends Data {

    }

    @Getter
    @AllArgsConstructor
    public static class CellEnterableData extends CellData {

        private EnterableStatus enterableStatus;

        public enum EnterableStatus{
            IMPASSABLE, //This cell isn't enterable! Don't even try.
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
