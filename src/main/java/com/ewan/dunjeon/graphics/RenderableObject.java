package com.ewan.dunjeon.graphics;

import java.awt.*;

/**
 * Contains base drawing data for a renderable object. </p>
 */
public abstract class RenderableObject {
    public abstract Shape getShape();
    public abstract Color getColor();
}
