package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import com.ewan.meworking.data.server.memory.MemoryBankListener;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ManagedClient {

    @Getter
    private InetSocketAddress clientAddress;
    private ClientBasedController<?, ?> creatureController;
    private List<DataWrapper<?,?>> unProcessedDataWrappers = new ArrayList<>(); //This doesn't currently need to worry about thread safety, since data is always processed at a different step from sending it

    @Getter
    @Setter
    private boolean isConnectionActive = true;


    public ManagedClient(ClientBasedController<?, ?> creatureController, InetSocketAddress clientAddress) {
        this.creatureController = creatureController;
        this.clientAddress = clientAddress;
        creatureController.getBasicMemoryBank().addListener(new MemoryBankListener() {
            @Override
            public <T extends Data, I, P extends KnowledgePackage<I, T>> void processWrappedData(DataWrapper<T, I> dataWrapper) {
                System.out.println("MemoryBankListener.processWrappedData()");
                unProcessedDataWrappers.add(dataWrapper);
            }
        });
    }

    public void passInputsToController(List<UserInput> actions){
        synchronized (creatureController.getUserInputBuffer()) {
            creatureController.getUserInputBuffer().addAll(actions);
        }
    }

    public void sendDataToClient(Channel channel){
        System.out.println("ManagedClient.sendDataToClient");
        if(isConnectionActive) {
            System.out.println("ManagedClient.sendDataToClient2");
            channel.writeAndFlush(new ServerDataWrapper(new ServerData(unProcessedDataWrappers, Dunjeon.getInstance().getTimeElapsed()), clientAddress));
            unProcessedDataWrappers = new ArrayList<>();
        }
    }
}
