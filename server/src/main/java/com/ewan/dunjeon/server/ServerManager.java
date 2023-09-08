package com.ewan.dunjeon.server;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.jogamp.opengl.GL;
import com.esotericsoftware.kryo.kryo5.Kryo;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {
    public void SerializeData(){
        Kryo kryo = new Kryo();
        Dunjeon dunjeon = Dunjeon.getInstance();


    }
}
