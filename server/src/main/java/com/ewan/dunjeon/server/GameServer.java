package com.ewan.dunjeon.server;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.dunjeon.server.networking.ServerManager;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.ServerDataEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class GameServer {



    public static void main(String[] args) throws Exception {

    }

}