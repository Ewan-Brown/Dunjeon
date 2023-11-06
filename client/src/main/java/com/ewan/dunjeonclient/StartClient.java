package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;

import java.util.Arrays;

public class StartClient
{
    public static void main(String[] args) {
        System.out.println("Arrays.toString(args) = " + Arrays.toString(args));
        ClientChannelHandler clientChannelHandler = new ClientChannelHandler();
        new Thread(() -> new GameClient(clientChannelHandler, args[0])).start();
        new Thread(() -> {
            new UsingJogl(clientChannelHandler).start();
        }).start();
    }
}
