package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;

public class StartClient
{
    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("Starting client");
        System.setOut(new PrintStream(new FileOutputStream(Paths.get("C:\\Users\\Ewan\\Documents\\Dunjeon\\client.txt").toFile())));
        System.out.println("Arrays.toString(args) = " + Arrays.toString(args));
        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(new BasicMemoryBank(0));
        new Thread(() -> new GameClient(clientChannelHandler, args[0])).start();
        new Thread(() -> {
            new UsingJogl(clientChannelHandler).start();
        }).start();
    }
}
