package com.ewan.meworking.data.server.metadata;


public record FrameInfoPacket(long clientUUID, double worldTimeExact, int worldTimeTicks, int expectedDataCount){}
