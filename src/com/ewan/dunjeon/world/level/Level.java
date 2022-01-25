package com.ewan.dunjeon.world.level;

import com.ewan.dunjeon.generation.Leaf;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.furniture.Door;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.ewan.dunjeon.generation.FloorGenerator.*;


public class Level {
    BasicCell[][] cells;

    protected Level(){}

    public List<Furniture> getFurniture(){
        List<Furniture> furniture = new ArrayList<>();
        Stream.of(cells).forEach(cells -> Stream.of(cells)
                .filter(basicCell -> basicCell.getFurniture() != null)
                .forEach(basicCell -> furniture.add(basicCell.getFurniture())));

        return furniture;
    }

    public List<Entity> getEntities(){
        List<Entity> entities = new ArrayList<>();
        Stream.of(cells).forEach(cells -> Stream.of(cells)
                .filter(basicCell -> basicCell.getEntity() != null)
                .forEach(basicCell -> entities.add(basicCell.getEntity())));

        return entities;
    }

    public int getWidth(){return cells[0].length;}
    public int getHeight(){return cells.length;}

    public BasicCell[][] getCells() {
        return cells;
    }

    public BasicCell getCellAt(int x, int y){
        if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight()){
            return null;
        }
        else {
            return cells[x][y];
        }
    }

    public List<BasicCell> getCellsAsList(){
        List<BasicCell> cellsTotal = new ArrayList<>();
        for (BasicCell[] cellArr : cells) {
            cellsTotal.addAll(Arrays.asList(cellArr));
        }
        return cellsTotal;
    }

    public void update(){
        for (Entity entity : getEntities()) {
            entity.update();
        }
    }
}
