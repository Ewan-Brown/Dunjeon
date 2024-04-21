package com.ewan.meworking.data.server.metadata;


import com.ewan.meworking.data.server.Timestamp;

public record FrameInfoPacket(long clientUUID, Timestamp timestamp, int expectedDataCount, int expectedEventCount){}
