package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.UpdateableData;
import lombok.Getter;

public abstract class Knowledge<I, D extends Data> {
    @Getter
    private final I identifier;
    public Knowledge(I identifier) {
        this.identifier = identifier;
    }

    public abstract <T extends D> void register(T object);
}
