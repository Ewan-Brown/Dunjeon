package com.ewan.dunjeon.world;

import java.awt.*;

/**
 * Contains base collision data for an Entity. </p>
 * S
 */
public abstract class CollidingObject {

    public abstract Shape getShape();
    public abstract CollideableRule getRule();

    public enum CollideableRule{
        ALL,
        WALLS_ONLY,
        NONE
    }
}
