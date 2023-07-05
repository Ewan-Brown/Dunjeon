package com.ewan.dunjeon.world;

import com.ewan.dunjeon.graphics.Graphics2DDisplay;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.Player;
import com.ewan.dunjeon.world.level.Floor;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.game.Main.rand;

public class Dunjeon implements KeyListener {

    private static Dunjeon dunjeon = new Dunjeon();
    private double time = 0;

    public double getTime(){return time;}

    private Player player;
    public static Dunjeon getInstance(){return dunjeon;}
    public static void resetDunjeon(){
        dunjeon = new Dunjeon();
    }

    List<Floor> floors = new ArrayList<>(); // TODO Should this be here, or should everything be stored in a node tree...?
    public void addLevel(Floor l){
        floors.add(l);
    }


    public Dunjeon(){
        initialize();
    }

    private void initialize() {

    }

    /**
     * Adds ,man entity at a random location On a level.
     * <i>TODO Add exception handling when there is no valid spots</i>
     */
    public boolean addEntityRandomLoc(Entity e, Floor l){
        List<BasicCell> validCells =  l.getCellsAsList().stream().filter(basicCell -> basicCell.canBeEntered(e)).collect(Collectors.toList());
        if(validCells.size() == 0) throw new Error("No valid spots for entity found");
        else {
            BasicCell randomValidCell = validCells.get(rand.nextInt(validCells.size()));
            randomValidCell.onEntry(e);
            e.onEnterCell(randomValidCell);
            //TODO Prepping for Dyn4J
            e.translate(randomValidCell.getX() + 0.5d, randomValidCell.getY() + 0.5d);
            e.setFloor(l);
            l.addEntity(e);
            l.getWorld().addBody(e);
            return true;
        }

    }

    /*
    Updates the game, returns true if the game is over.
     */
    public void update(double t){
        System.out.println("Updating world, t = " + time);
        this.time += t;


        //Remove entities, apply physics step and collect collisions
        getPlayer().getFloor().updatePhysics(t);

        //Update Datastreams!
        sightDataStream.update(this);

        //Update Entities
        getPlayer().getFloor().updateEntities(t);

    }


    //**************************************************
    //PLAYER SPECIFIC TODO MOVE THIS STUFF
    //**************************************************

    double playerInteractionDist = 1.5;

    double playerSpeed = 0.03f;

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player p){ player = p;}

    //TODO Make a nice wrapper for this to make managing controls easier!
    public void doControls(){
        //TODO Prepping for Dyn4J
        if(keySet.get(KeyEvent.VK_UP)){
//            player.addVelocity(0.0f,-playerSpeed);
        }
        if(keySet.get(KeyEvent.VK_DOWN)){
//            player.addVelocity(0.0f,+playerSpeed);
        }
        if(keySet.get(KeyEvent.VK_LEFT)){
//            player.addVelocity(-playerSpeed,0.0f);
        }
        if(keySet.get(KeyEvent.VK_RIGHT)){
//            player.addVelocity(playerSpeed, 0.0f);
        }

        if(keySet.get(KeyEvent.VK_M)){
            keySet.set(KeyEvent.VK_M, false);
            Graphics2DDisplay.RENDER_GRID = !Graphics2DDisplay.RENDER_GRID;
        }

        if(keySet.get(KeyEvent.VK_N)){
            keySet.set(KeyEvent.VK_N, false);
//            player.true_sight_debug = !player.true_sight_debug;
        }

        if(keySet.get(KeyEvent.VK_Z)){
            keySet.set(KeyEvent.VK_Z, false);
            Graphics2DDisplay.RENDER_DEBUG_LINES = !Graphics2DDisplay.RENDER_DEBUG_LINES;
        }

        if(keySet.get(KeyEvent.VK_X)){
            keySet.set(KeyEvent.VK_X, false);
            Graphics2DDisplay.RENDER_DEBUG_CELLS = !Graphics2DDisplay.RENDER_DEBUG_CELLS;
        }

        //Fire projectile in random direction
        if(keySet.get(KeyEvent.VK_R)){
            keySet.set(KeyEvent.VK_R, false);
//            Entity e = new SimpleProjectile(Color.RED, "Projectile");
//            e.setVelocity((rand.nextFloat() - 0.5f) / 30f,(rand.nextFloat() - 0.5f) / 30f + 0.01f);
//            addEntityAtLoc(e, player.getFloor(), player.getWorldCenter().x, player.getWorldCenter().y);
        }

        //Test function
        if(keySet.get(KeyEvent.VK_Z)){
            keySet.set(KeyEvent.VK_Z, false);
            System.out.println(player.getPoint2DLoc());
        }
    }

    //****** Data streams ******//
    private Datastreams.SightDataStream sightDataStream = new Datastreams.SightDataStream();
    public Datastreams.SightDataStream getSightDataStream() {
        return sightDataStream;
    }

    private final BitSet keySet = new BitSet();

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keySet.set(keyEvent.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keySet.set(keyEvent.getKeyCode(), false);
    }
}
