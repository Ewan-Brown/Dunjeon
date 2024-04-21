package com.ewan.meworking.codec;

import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.server.EventPacket;
import com.ewan.meworking.data.server.event.ObservedEvent;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;

public class PacketTypes {
    public enum PacketType{
        FRAME_PACKET(FrameInfoPacket.class), DATA_PACKET(DataPacket.class), EVENT_PACKET(EventPacket.class);

        Class<?> relatedClass;

        PacketType(Class<?> relatedClass) {
            this.relatedClass = relatedClass;
        }
    }
}
