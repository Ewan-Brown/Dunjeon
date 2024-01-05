package com.ewan.dunjeonclient;

import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.client.MoveEntity;
import com.ewan.meworking.data.client.TurnEntity;
import com.ewan.meworking.data.client.UserInput;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.List;

public class SimpleControls implements KeyListener {

    private final ClientChannelHandler clientChannelHandler;

    public SimpleControls(ClientChannelHandler clientChannelHandler) {
        this.clientChannelHandler = clientChannelHandler;
    }

    private BitSet keySet = new BitSet(256);
    private long[] keyLastChangedMillis = new long[256];

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

        //Note that the controls here are ONLY responsible for sending stuff each key event - it's left up to the server-side controller to decide what to do with these

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

        UserInput mEntity = new MoveEntity(new Vector2(x, y));
        //Process turning keys

        float turn = 0;

        if(keySet.get(KeyEvent.VK_E)){
            turn -= 1;
        }
        if(keySet.get(KeyEvent.VK_Q)){
            turn += 1;
        }

        UserInput tEntity = new TurnEntity(turn);
        clientChannelHandler.sendMessageToClient(new ClientInputData(List.of(tEntity, mEntity)));
    }
}
