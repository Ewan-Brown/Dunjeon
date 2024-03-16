package com.ewan.dunjeonclient;

import com.ewan.meworking.data.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class SimpleControls implements KeyListener {

    private final ClientChannelHandler clientChannelHandler;
    static Logger logger = LogManager.getLogger();

    public SimpleControls(ClientChannelHandler clientChannelHandler) {
        this.clientChannelHandler = clientChannelHandler;
    }

    private BitSet keySet = new BitSet(256);

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        //This ensures that we can ignore whatever builtin 'repeating' button functionality that comes when you hold down a key on some systems
        if(!keySet.get(e.getKeyCode())){
            keySet.set(e.getKeyCode(), true);
            processKeys(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //See keyPressed() comment
        if(keySet.get(e.getKeyCode())){
            keySet.set(e.getKeyCode(), false);
            processKeys(e);
        }

    }

    /*
     * Processes current key state, triggered WHENEVER A key is pressed or released, and send to server - semi-asynchronous from the receiving part of the client
     */
    public void processKeys(KeyEvent triggeringKeyEvent){


        List<UserInput> collectedInputs = new ArrayList<>();

        // Process movement keys
        float x = 0;
        float y = 0;

        if(keySet.get(KeyEvent.VK_W)){
            y++;
        }
        if(keySet.get(KeyEvent.VK_S)){
            y--;
        }
        if(keySet.get(KeyEvent.VK_D)){
            x++;
        }
        if(keySet.get(KeyEvent.VK_A)){
            x--;
        }

        collectedInputs.add(new MoveEntity(new Vector2(x, y).getNormalized()));
        //Process turning keys

        float turn = 0;

        if(keySet.get(KeyEvent.VK_E)){
            turn -= 1;
        }
        if(keySet.get(KeyEvent.VK_Q)){
            turn += 1;
        }

        collectedInputs.add(new TurnEntity(turn));

        if(triggeringKeyEvent.getKeyCode() == KeyEvent.VK_SPACE){
            collectedInputs.add(new DebugInput());
        }
        clientChannelHandler.sendMessageToClient(new ClientInputData(collectedInputs));
    }
}
