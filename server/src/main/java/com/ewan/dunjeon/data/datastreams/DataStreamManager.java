package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.server.world.Dunjeon;
import lombok.Getter;

@Getter
public class DataStreamManager {

    private final HearingDataStream hearingDataStream = new HearingDataStream();
    private final SightDataStream sightDataStream = new SightDataStream();

    public void update(float t, Dunjeon d){
        sightDataStream.update(d);
        hearingDataStream.update(d);
    }
}
