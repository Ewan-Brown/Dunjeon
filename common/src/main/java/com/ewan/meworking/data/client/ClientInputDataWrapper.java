package com.ewan.meworking.data.client;

import java.net.InetSocketAddress;

public record ClientInputDataWrapper(ClientInputData clientInputData, InetSocketAddress sender) { }
