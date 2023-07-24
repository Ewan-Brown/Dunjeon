package com.ewan.dunjeon.input;

import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@Getter
public class KeyBank implements KeyListener {

    /**
     * List of keys that have been recorded as being pressed down but not released
     */
    private BitSet keySet = new BitSet(256);

    /**
     * Ephemeral list of keys that have been recorded as pressed down in the most recent check but have not been processed yet
     */
    private Set<Integer> keysDown = new HashSet<>();
    /**
     * Ephemeral list of keys that have been recorded as being released (now in UP state) in the most recent check but have not been processed yet
     */
    private Set<Integer> keysUp = new HashSet<>();


    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("Zoop1");
    }

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public void keyPressed(KeyEvent e) {
        System.out.println("Zoop2");
        int c = e.getKeyCode();
        if(!keySet.get(c)){
            keysDown.add(c);
            keySet.set(c, true);
        }else{
            //An automatic repeat has occurred, not an actual key press, we are safe to ignore this.
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("Zoop3");
        int c = e.getKeyCode();
        keysUp.add(c);
        keySet.set(c, false);
    }

    public void clearSets(){
        keysDown.clear();
        keysUp.clear();;
    }
}
