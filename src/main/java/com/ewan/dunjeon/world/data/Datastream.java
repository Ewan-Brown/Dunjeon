package com.ewan.dunjeon.world.data;

import com.ewan.dunjeon.world.Dunjeon;

public abstract class Datastream<T extends Data, S extends DataStreamParameters> {
    public abstract void update(Dunjeon d);
    public abstract T generateDataForParams(S params);


}
