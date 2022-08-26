package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;

public class Door extends Furniture{

    boolean open;
    static Color BROWN = new Color(165, 42, 42);

    public Door(boolean isOpen) {
        open = isOpen;
    }

    @Override
    public Color getColor() {
        return (open) ? null : BROWN;
    }

    public boolean isBlocking(){
        return !open;
    }

    public void onInteract(Entity e) {
        open = !open;
    }
}
