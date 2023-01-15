package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.graphics.Drawable;
import com.ewan.dunjeon.world.Collideable;

import java.awt.Shape;
import java.util.List;

public abstract class Entity {
    public abstract void update();

    public abstract List<Collideable> getCollidable();
    public abstract List<Drawable> getDrawable();
}
