package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.UserInput;
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

    public void passInputsToController(List<UserInput> actions){
        synchronized (creatureController.getUserInputBuffer()) {
            creatureController.getUserInputBuffer().addAll(actions);
        }
    }

    public void sendDataToClient(){
        if(creatureController.getBasicMemoryBank() != null) {
            getClientChannel().writeAndFlush(new ServerData(creatureController.getBasicMemoryBank()));
        }else{
            System.err.println("Attempted to send data to client but the attached memory bank is null!");
        }

    }

}
