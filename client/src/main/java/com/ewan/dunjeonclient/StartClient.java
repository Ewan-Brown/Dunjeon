package com.ewan.dunjeonclient;

import com.ewan.meworking.data.server.Timestamp;
import com.ewan.meworking.data.server.event.HeardSoundEvent;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.util.Arrays;

public class StartClient
{
    static Logger logger = LogManager.getLogger();
    @SneakyThrows
    public static void main(String[] args) {
        logger.info("Starting client");
        logger.debug("Arrays.toString(args) = " + Arrays.toString(args));
        EventManager eventManager = new EventManager();
        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(new BasicMemoryBank(), eventManager);
//        eventManager.processEvent(new HeardSoundEvent(new Timestamp(0,0), new Vector2(), HeardSoundEvent.SoundSourceCategory.ENTITY, 1.0f, false));
        new Thread(() -> new GameClient(clientChannelHandler, args[0])).start();
        new Thread(() -> {
            new ClientInterface(clientChannelHandler, eventManager).start();
        }).start();
    }
}
