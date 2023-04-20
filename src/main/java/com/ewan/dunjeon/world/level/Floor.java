package com.ewan.dunjeon.world.level;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.furniture.Furniture;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


public class Floor {
    BasicCell[][] cells;
    List<Entity> entities = new ArrayList<>();
    public Floor(){}

    public void setCells(BasicCell[][] cells){
        this.cells = cells;
    }

    public List<Furniture> getFurniture(){
        List<Furniture> furniture = new ArrayList<>();
        Stream.of(cells).forEach(cells -> Stream.of(cells)
                .filter(basicCell -> basicCell.getFurniture() != null)
                .forEach(basicCell -> furniture.add(basicCell.getFurniture())));

        return furniture;
    }

    public List<Entity> getEntities(){
        return entities;
    }

    public void addEntity(Entity e){
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
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
            return cells[y][x];
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
        getCellsAsList().forEach(BasicCell::updateFurniture);

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
//            e.update();
//            doBoundsCheck(e);
        }

    }



//    public BasicCell getCellAt(double x, double y){
//        return getCellAt((int)Math.floor(x),(int)Math.floor(y));
//    }
//
//    public BasicCell getCellAt(Point2D point){
//        return getCellAt((double)Math.floor(point.getX()),(double)Math.floor(point.getY()));
//    }

//    public BasicCell getCellAt(Point point){
//        return getCellAt((int)point.getX(), (int)point.y);
//    }

    public BasicCell getCellAt(Vector2 v){
        return getCellAt((int)Math.floor(v.x), (int)Math.floor(v.y));
    }
}
