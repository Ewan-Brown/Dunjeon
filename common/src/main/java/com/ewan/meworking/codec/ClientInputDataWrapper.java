package com.ewan.meworking.codec;

import com.ewan.meworking.data.ClientInputData;

import java.net.InetSocketAddress;

public record ClientInputDataWrapper(ClientInputData clientInputData, InetSocketAddress sender) { }
