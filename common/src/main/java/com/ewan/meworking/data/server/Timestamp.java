package com.ewan.meworking.data.server;

public record Timestamp(int serverTick, float worldTime) {
    @Override
    public String toString() {
        return new String("tick: " + serverTick + ", time: " + worldTime);
    }
}
