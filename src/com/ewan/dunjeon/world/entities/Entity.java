package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.ItemHolder;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.ai.GenericAction;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.item.Item;

import java.awt.*;
import java.util.List;

public class Entity implements ItemHolder, Updateable {
    public BasicCell containingCell;
    private GenericAction currentAction = null;
    private int speed;
    Color color;

    public Entity(Color c, int s){
        this.color = c;
        speed = s;
    }

    public Level getLevel(){
        return containingCell.getLevel();
    }

    public void enterCell(BasicCell c){
        containingCell = c;
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    public Color getColor(){
        return color;
    }

    public int getX(){
        return containingCell.getX();
    }
    public int getY(){
        return containingCell.getY();
    }
    public int getSpeed(){
        return speed;
    };

    public void setNewAction(GenericAction a){
        if(currentAction != null){
            currentAction.onCancel();
        }
        a.setActor(this);
        currentAction = a;
    }

    @Override
    public void update() {
        if(currentAction != null){
            currentAction.update();
            if(currentAction.isComplete()){
                currentAction.onComplete();
                currentAction = null;
            }
        }
    }

    public GenericAction getCurrentAction(){
        return currentAction;
    }

    public void onDeath(){

    }

}
