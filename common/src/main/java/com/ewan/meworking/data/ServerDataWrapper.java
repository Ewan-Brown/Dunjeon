package com.ewan.meworking.data;

import lombok.Data;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


public record ServerDataWrapper(ServerData data, InetSocketAddress address) { }
