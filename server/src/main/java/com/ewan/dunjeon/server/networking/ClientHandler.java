package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.ClientAction;
import io.netty.channel.Channel;
import lombok.Getter;

import java.util.List;

public class ClientHandler {

    public ClientHandler(ClientBasedController<?, ?> creatureController, Channel clientChannel) {
        this.creatureController = creatureController;
        this.clientChannel = clientChannel;
    }

    private ClientBasedController<?, ?> creatureController;

    @Getter
    private Channel clientChannel;

    public void passActionsToController(List<ClientAction> actions){
        creatureController.getUnprocessedClientActions().addAll(actions);
    }

    public void sendDataToClient(){
        getClientChannel().writeAndFlush(new ServerData(creatureController.getMemoryBank()));
    }

    public void clearControllerActions(){
        creatureController.getUnprocessedClientActions().clear();
    }
}
