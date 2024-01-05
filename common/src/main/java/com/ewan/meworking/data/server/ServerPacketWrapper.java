package com.ewan.meworking.data.server;

import com.ewan.meworking.codec.PacketTypes;

import java.net.InetSocketAddress;


/**
 * Only used on server side, straps some data to be sent out with an address as required with UDP encoding final step
 * @param data
 * @param address
 */
public record ServerPacketWrapper(Object data, PacketTypes.PacketType pType, InetSocketAddress address) { }
