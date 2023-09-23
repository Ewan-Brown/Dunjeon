package com.ewan.dunjeon.server.networking;


import com.ewan.dunjeon.server.world.entities.ai.CreatureController;
import com.ewan.meworking.data.client.ClientAction;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClientHandler {

    public ClientHandler(CreatureController<?> clientCreature, Channel clientChannel) {
        this.clientCreature = clientCreature;
        this.clientChannel = clientChannel;
        recentClientActions = new ArrayList<>();
    }

    private CreatureController<?> clientCreature;
    private Channel clientChannel;

    //TODO hold player session data here?
}
