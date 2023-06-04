package com.ewan.dunjeon.world.entities.memory;

public class Relationship {

    public Relationship(long h, long t){
        hostUUID = h;
        targetUUID = t;
    }
    long hostUUID;
    long targetUUID;
}
