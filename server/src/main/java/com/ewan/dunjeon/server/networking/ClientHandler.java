package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import com.ewan.meworking.data.client.UserInput;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;

public class ClientHandler {

    @Getter
    private InetSocketAddress clientAddress;
    private ClientBasedController<?, ?> creatureController;

    @Getter
    @Setter
    private boolean isConnectionActive = true;


    public ClientHandler(ClientBasedController<?, ?> creatureController, InetSocketAddress clientAddress) {
        this.creatureController = creatureController;
        this.clientAddress = clientAddress;
    }

    public void passInputsToController(List<UserInput> actions){
        synchronized (creatureController.getUserInputBuffer()) {
            creatureController.getUserInputBuffer().addAll(actions);
        }
    }

    public void sendDataToClient(Channel channel){
        if(isConnectionActive) {
            if (creatureController.getBasicMemoryBank() != null) {
                channel.writeAndFlush(new ServerDataWrapper(new ServerData(creatureController.getBasicMemoryBank(), Dunjeon.getInstance().getTimeElapsed()), clientAddress));
            } else {
                System.err.println("Attempted to send data to client but the attached memory bank is null!");
            }
        }

    }

}
