package com.ewan.dunjeonclient;

import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to contain data pertaining to a Game update "frame". Collects all DataWrappers associated with a single game update tick, based on the ordinal
 * Once all of the relevant datawrappers are received this frame is ready to be drawn and can be processed
 */
public class GameFrame {
    @Setter
    @Getter
    private FrameInfoPacket framePacket;

    @Getter
    private final List<DataWrapper<?,?>> collectedData = new ArrayList<>();

    public GameFrame(FrameInfoPacket framePacket) {
        this.framePacket = framePacket;
    }

    public boolean isComplete(){
        return framePacket != null && collectedData.size() == framePacket.expectedDataCount();
    }
}
