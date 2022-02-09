package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.entities.actions.IdleAction;
import com.ewan.dunjeon.world.entities.actions.InteractAction;
import com.ewan.dunjeon.world.entities.actions.MoveAction;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Level;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.generation.Main.rand;

public class World implements KeyListener {

    private static World w = new World();

    List<Integer> keyList = new ArrayList<>();

    private Entity player;
    private int tick_tracker = 0;
    public static World getInstance(){return w;}

    List<Level> levels = new ArrayList<>(); //TODO Should this be here, or should everything be stored in a node tree...?
    public void addLevel(Level l){
        levels.add(l);
    }

    public Entity getPlayer(){
        return player;
    }

    public void setPlayer(Entity p){ player = p;}

    /**
     * Adds a entity at a random location On a level.
     * TODO Add exception handling when there is no valid spots
     * @param e
     * @param l
     * @return
     */
    public boolean addEntityRandomLoc(Entity e, Level l){
        List<BasicCell> validCells =  l.getCellsAsList().stream().filter(basicCell -> basicCell.canBeEntered(e)).collect(Collectors.toList());
        if(validCells.size() == 0) return false;
        else {
            BasicCell randomValidCell = validCells.get(rand.nextInt(validCells.size()));
            randomValidCell.onEntry(e);
            e.enterCell(randomValidCell);
            return true;
        }

    }

    public void update(){
        System.out.println("\nUpdate [" + tick_tracker+"]");
        tick_tracker++;
        while(player.getCurrentAction() == null){
            doControls();
        }
        for (Level level : levels) {
            level.update();
        }
//        movementProcessor.processMovements();
        getPlayer().updateViewRange();
    }

    HashMap<Integer, Point> keyDirMapping = new HashMap<>();
    {
        keyDirMapping.put(KeyEvent.VK_UP, new Point(0, -1));
        keyDirMapping.put(KeyEvent.VK_DOWN, new Point(0, 1));
        keyDirMapping.put(KeyEvent.VK_LEFT, new Point(-1, 0));
        keyDirMapping.put(KeyEvent.VK_RIGHT, new Point(1, 0));
    }

    private int getNextKey(){
        while(keyList.size() == 0){ //FIXME Find a nicer way to wait for new keystroke. Maybe check out locks/synch?
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int retVal = keyList.get(keyList.size()-1);
        keyList.remove(keyList.size()-1);
        return retVal;
    }

    private int getNextKeyWithFilter(List<Integer> acceptable){
        int key;
        do{
            key = getNextKey();
        }while(!acceptable.contains(key));
        return key;
    }

    private static final List<Integer> ACCEPTABLE_INPUTS = new ArrayList<>();
    private static final List<Integer> DIRECTION_KEYS = new ArrayList<>();
    static {
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_UP);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_DOWN);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_LEFT);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_RIGHT);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_I);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_COMMA);
        ACCEPTABLE_INPUTS.add(KeyEvent.VK_PERIOD);

        DIRECTION_KEYS.add(KeyEvent.VK_UP);
        DIRECTION_KEYS.add(KeyEvent.VK_DOWN);
        DIRECTION_KEYS.add(KeyEvent.VK_LEFT);
        DIRECTION_KEYS.add(KeyEvent.VK_RIGHT);

    }

    //TODO Move this stuff somewhere else
    public void doControls(){
        int key = getNextKeyWithFilter(ACCEPTABLE_INPUTS);
        if(key == KeyEvent.VK_I){
            while(true) {
                key = getNextKeyWithFilter(DIRECTION_KEYS);
                int[] dir = getDir(key);
                int x = dir[0];
                int y = dir[1];
                if(x == 0 && y == 0) {
                    continue;
                }
                else {
                    player.setNewAction(new InteractAction(10, x, y));
                    return;
                }
            }
        }else if(DIRECTION_KEYS.contains(key)){
            int[] dir = getDir(key);
            int x = dir[0];
            int y = dir[1];
            if (x != 0 || y != 0) {
                player.setNewAction(new MoveAction(10, x, y));
            }
        }
        // Note that actions last ticks+1 updates.
        else if(key == KeyEvent.VK_PERIOD){
            player.setNewAction(new IdleAction(9));
        }else if(key == KeyEvent.VK_COMMA){
            player.setNewAction(new IdleAction(0));
        }

    }

    public int[] getDir(int key){
        int x = 0;
        int y = 0;
        x = (int)keyDirMapping.get(key).getX();
        y = (int)keyDirMapping.get(key).getY();
        return new int[]{x, y};
    }

    public void moveEntity(Entity e, BasicCell entry){
        e.containingCell.onExit(e);
        entry.onEntry(e);
        e.enterCell(entry);
    }

    public void killEntity(Entity e){
        e.containingCell.onEntityDeath(e);
        e.onDeath();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keyList.add(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
