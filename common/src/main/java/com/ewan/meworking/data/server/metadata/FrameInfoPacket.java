package com.ewan.meworking.data.server.metadata;


public record FrameInfoPacket(double worldTimeExact, int worldTimeTicks, int expectedDataCount){}
