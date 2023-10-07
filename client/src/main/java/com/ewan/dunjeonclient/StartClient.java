package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;

public class StartClient
{
    public static void main(String[] args) {
        System.out.println("Starting Client");
        Log.TRACE();
        ClientChannelHandler clientChannelHandler = new ClientChannelHandler();
        new Thread(() -> new GameClient(clientChannelHandler)).start();
        new Thread(() -> {
            new UsingJogl(clientChannelHandler).start();
        }).start();
    }
}
