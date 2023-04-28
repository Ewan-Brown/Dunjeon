package com.ewan.dunjeon.graphics;

import org.dyn4j.geometry.Vector2;

import java.awt.*;

/**
 * Contains final calculated drawing data to be passed to the graphics engine. Requires data from host entity to</p>
 */
public abstract class RenderableElement {

    public RenderableElement(Shape shape, Color c, Vector2 localPos, double localRot){
        this.shape = shape;
        this.color = c;
        this.localPosition = localPos;
        this.localRotation = localRot;
    }

    final private Shape shape;
    final private Color color;
    private final Vector2 localPosition;
    private final double localRotation;

}
