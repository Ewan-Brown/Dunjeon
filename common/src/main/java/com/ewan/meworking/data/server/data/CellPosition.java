package com.ewan.meworking.data.server.data;

import lombok.Getter;
import org.dyn4j.geometry.Vector2;

import java.util.Objects;

@Getter
public class CellPosition {
    private final Vector2 position;
    private final long floorID;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CellPosition) && ((CellPosition) obj).floorID == this.floorID && position.equals(((CellPosition) obj).getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, floorID);
    }

    public CellPosition(Vector2 position, long floorID) {
        this.position = position;
        this.floorID = floorID;
    }
}
