package com.ewan.dunjeon.world;

import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Creature;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.sounds.SoundManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.game.Main.rand;

public class World implements KeyListener {

    private static World w = new World();
    private SoundManager soundManager = new SoundManager();

    private Creature player;
    public static World getInstance(){return w;}

    List<Floor> floors = new ArrayList<>(); //TODO Should this be here, or should everything be stored in a node tree...?
    public void addLevel(Floor l){
        floors.add(l);
    }


    /**
     * Adds a entity at a random location On a level.
     * TODO Add exception handling when there is no valid spots
     * @param e
     * @param l
     * @return
     */
    public boolean addEntityRandomLoc(Entity e, Floor l){
        List<BasicCell> validCells =  l.getCellsAsList().stream().filter(basicCell -> basicCell.canBeEntered(e)).collect(Collectors.toList());
        if(validCells.size() == 0) return false;
        else {
            BasicCell randomValidCell = validCells.get(rand.nextInt(validCells.size()));
            randomValidCell.onEntry(e);
            e.enterCell(randomValidCell);
            e.setPosition(randomValidCell.getX() + 0.5f, randomValidCell.getY() + 0.5f);
            e.setFloor(l);
            l.addEntity(e);
            return true;
        }

    }



    /*
    Updates the game, returns true if the game is over.
     */
    public boolean update(){
        if(player.isDead()){
            System.out.println("Game over! Player dead.");
            return true;
        }
        soundManager.propogateSounds();
        doControls();
        getPlayer().getFloor().update();
        getPlayer().updateViewRange();
        return false;
    }

    public SoundManager getSoundManager(){
        return soundManager;
    }

    //**************************************************
    //PLAYER SPECIFIC TODO MOVE THIS STUFF
    //**************************************************

    double playerInteractionDist = 1.5;


    public Creature getPlayer(){
        return player;
    }

    public void setPlayer(Creature p){ player = p;}

    //TODO Make a nice wrapper for this to make managing controls easier!
    public void doControls(){
        if(keySet[KeyEvent.VK_UP]){
            player.addVelocity(0.0f,-0.003f);
        }
        if(keySet[KeyEvent.VK_DOWN]){
            player.addVelocity(0.0f,+0.003f);
        }
        if(keySet[KeyEvent.VK_LEFT]){
            player.addVelocity(-0.003f,0.0f);
        }
        if(keySet[KeyEvent.VK_RIGHT]){
            player.addVelocity(0.003f, 0.0f);
        }
        if(keySet[KeyEvent.VK_SPACE]){
            //Prevents instant repeat on hold
            keySet[KeyEvent.VK_SPACE] = false;
            Interactable i = getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.TOUCH);
            if(i != null){
                i.onInteract(player, Interactable.InteractionType.TOUCH);
            }
        }
        if(keySet[KeyEvent.VK_T]){
            //Prevents instant repeat on hold
            keySet[KeyEvent.VK_T] = false;
            Interactable i = getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.CHAT);
            if(i != null){
                i.onInteract(player, Interactable.InteractionType.CHAT);
            }
        }
        if(keySet[KeyEvent.VK_M]){
            //Prevents instant repeat on hold
            keySet[KeyEvent.VK_M] = false;
            LiveDisplay.SHOW_ALL_TILES = !LiveDisplay.SHOW_ALL_TILES;
        }
    }

    //Returns the closest world object that supports 'type' InteractionType for the player
    public Interactable getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType type){
        Interactable closestInteractable = null;
        Floor f = player.getFloor();

        List<Interactable> interactables = new ArrayList<>();

        for (int x = 0; x < f.getWidth(); x++) {
            for (int y = 0; y < f.getHeight(); y++) {
                BasicCell currentCell = f.getCellAt(x, y);
                if (currentCell.getFurniture() instanceof Interactable) {
                    interactables.add((Interactable) currentCell.getFurniture());
                }
            }
        }

        for (Entity e : getPlayer().getFloor().getEntities()) {
            if(e instanceof Interactable){
                interactables.add((Interactable)e);
            }
        }

        closestInteractable = interactables.stream()
                .filter(interactable -> interactable.getAvailableInteractions(getPlayer()).contains(type))
                .filter(interactable -> WorldUtils.getRawDistance(interactable, getPlayer()) < playerInteractionDist)
                .min((o1, o2) -> (int)Math.signum(WorldUtils.getRawDistance(o1, getPlayer()) - WorldUtils.getRawDistance(o2, getPlayer()))).orElse(null);

//        for (Interactable interactable : interactables) {
//            if (interactable.isInteractable(getPlayer())) {
//                float dist = WorldUtils.getRawDistance(interactable, getPlayer());
//                if(dist < minDist || minDist == -1){
//                    minDist = dist;
//                    closestInteractable = interactable;
//                }
//            }
//        }

        return closestInteractable;

    }


    private boolean[] keySet = new boolean[256];

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keySet[keyEvent.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keySet[keyEvent.getKeyCode()] = false;
    }
}