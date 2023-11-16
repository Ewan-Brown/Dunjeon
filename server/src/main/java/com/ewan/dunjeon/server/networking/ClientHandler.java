package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.UserInput;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ClientHandler {

    @Getter
    private Channel clientChannel;
    private ClientBasedController<?, ?> creatureController;

    @Getter
    @Setter
    private boolean isConnectionActive = true;


    public ClientHandler(ClientBasedController<?, ?> creatureController, Channel clientChannel) {
        this.creatureController = creatureController;
        this.clientChannel = clientChannel;
    }

    public void passInputsToController(List<UserInput> actions){
        synchronized (creatureController.getUserInputBuffer()) {
            creatureController.getUserInputBuffer().addAll(actions);
        }
    }

    public void sendDataToClient(){
        if(isConnectionActive) {
            System.out.println("Sending data to client, ctx:" + getClientChannel().toString());
            if (creatureController.getBasicMemoryBank() != null) {
                getClientChannel().writeAndFlush(new ServerData(creatureController.getBasicMemoryBank(), Dunjeon.getInstance().getTimeElapsed()));
            } else {
                System.err.println("Attempted to send data to client but the attached memory bank is null!");
            }
        }

    }

}
