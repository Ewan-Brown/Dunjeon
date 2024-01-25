package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;

public class StartClient
{
    static Logger logger = LogManager.getLogger();
    @SneakyThrows
    public static void main(String[] args) {
//        logger.info("Starting client");
        logger.info("Logger.info called, and we're starting client");
//        System.setOut(new PrintStream(new FileOutputStream(Paths.get("C:\\Users\\Ewan\\Documents\\Dunjeon\\client.txt").toFile())));
//        System.setErr(new PrintStream(new FileOutputStream(Paths.get("C:\\Users\\Ewan\\Documents\\Dunjeon\\client.txt").toFile())));
        logger.debug("Arrays.toString(args) = " + Arrays.toString(args));
        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(new BasicMemoryBank());
        new Thread(() -> new GameClient(clientChannelHandler, args[0])).start();
        new Thread(() -> {
            new UsingJogl(clientChannelHandler).start();
        }).start();
    }
}
