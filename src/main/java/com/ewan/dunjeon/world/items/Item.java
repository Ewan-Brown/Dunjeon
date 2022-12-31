package com.ewan.dunjeon.world.items;

import java.awt.*;
import java.util.function.Supplier;

public abstract class Item {

    private String name;
    Supplier<ItemRenderData> renderDataSupplier;

    public String getName(){return name;}

    public ItemRenderData getRenderData(){
        return renderDataSupplier.get();
    }

    public class ItemRenderData{
        public Color c = Color.BLUE;
    }

}
