package com.ewan.dunjeon.world.level;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
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
        for (Entity entity : getEntities()) {
            entity.update();
            doBoundsCheck(entity);
        }
    }

    //Assumes all entities are squares, and are no bigger than a cell
    public void doBoundsCheck(Entity e){
        float xMin = - e.getSize()/2;
        float xMax = + e.getSize()/2;
        float yMin = - e.getSize()/2;
        float yMax = + e.getSize()/2;

        Point2D upperLeft = new Point2D.Double(xMin, yMax);
        Point2D upperRight = new Point2D.Double(xMax, yMax);
        Point2D lowerLeft = new Point2D.Double(xMin, yMin);
        Point2D lowerRight = new Point2D.Double(xMax, yMin);

        List<Point2D> corners = new ArrayList<>();
        corners.add(upperLeft);
        corners.add(upperRight);
        corners.add(lowerLeft);
        corners.add(lowerRight);

        float fX = 0;
        float fY = 0;
        for (Point2D corner : corners) {
            BasicCell cornerCell = (getCellAt((float)corner.getX() + e.getPosX(), (float)corner.getY() + e.getPosY()));
            if(!cornerCell.canBeEntered(e)){
                fX -= corner.getX();
                fY -= corner.getY();
            }
        }

        e.addVelocity(fX/100f, fY/100f);

    }



    public BasicCell getCellAt(float x, float y){
        return getCellAt((int)Math.floor(x),(int)Math.floor(y));
    }

    public BasicCell getCellAt(Point2D point){
        return getCellAt((float)Math.floor(point.getX()),(float)Math.floor(point.getY()));
    }
}
