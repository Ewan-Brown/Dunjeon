package com.ewan.meworking.data.server;

import lombok.AllArgsConstructor;
import org.dyn4j.geometry.Vector2;

import java.awt.*;

/**
 * Contains final calculated drawing data to be passed to the graphics engine. Requires data from host entity to properly render</p>
 */
@AllArgsConstructor
public abstract class RenderableElement {

    final private Shape shape;
    final private Color color;
    private final Vector2 localPosition;
    private final double localRotation;

}
