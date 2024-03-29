package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.meworking.codec.PacketTypes;
import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.server.ServerPacketWrapper;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import com.ewan.meworking.data.server.memory.MemoryBankListener;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ManagedClient {

    @Getter
    private InetSocketAddress clientAddress;
    private ClientBasedController<?, ?> creatureController;
    private List<DataWrapper<?,?>> unProcessedDataWrappers = new ArrayList<>(); //This doesn't currently need to worry about thread safety, since data is always processed at a different step from sending it
    static Logger logger = LogManager.getLogger();

    @Getter
    @Setter
    private boolean isConnectionActive = true;

    public ManagedClient(ClientBasedController<?, ?> creatureController, InetSocketAddress clientAddress) {
        this.creatureController = creatureController;
        this.clientAddress = clientAddress;
        creatureController.getBasicMemoryBank().addListener(new MemoryBankListener() {
            @Override
            public <T extends Data, I, P extends KnowledgePackage<I, T>> void processWrappedData(DataWrapper<T, I> dataWrapper) {
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
        if(isConnectionActive) {
            logger.debug("sending some data to client: " + channel.toString());
            //We need to ensure that the client knows how many datawrappers to expect before it can draw its next frame, as well as what creature it is attached to
            logger.debug("sending frameInfoPacket for " + unProcessedDataWrappers.size() +" # of datas on frame" + Dunjeon.getInstance().getTicksElapsed());
            channel.writeAndFlush(new ServerPacketWrapper(new FrameInfoPacket(creatureController.getBasicMemoryBank().getOwnerUUID(), Dunjeon.getInstance().getTimeElapsed(), Dunjeon.getInstance().getTicksElapsed(), unProcessedDataWrappers.size()), PacketTypes.PacketType.FRAME_PACKET, clientAddress));
            int dataCounter = 0;
            for (DataWrapper<?, ?> unProcessedDataWrapper : unProcessedDataWrappers) {
                logger.trace("sending dataPacket "+dataCounter+"/"+unProcessedDataWrappers.size());
                channel.writeAndFlush(new ServerPacketWrapper(new DataPacket(unProcessedDataWrapper), PacketTypes.PacketType.DATA_PACKET, clientAddress));
                dataCounter++;
            }
            unProcessedDataWrappers = new ArrayList<>();
//            isConnectionActive = false; //TODO DEBUG LINE
        }
    }
}
