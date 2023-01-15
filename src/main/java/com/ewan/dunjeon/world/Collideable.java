package com.ewan.dunjeon.world;

import java.awt.*;

public abstract class Collideable {
    public abstract Shape getShape();
    public abstract CollideableRule getRule();

    public enum CollideableRule{
        ALL,
        WALLS_ONLY,
        NONE
    }
}
