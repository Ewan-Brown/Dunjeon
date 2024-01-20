package com.ewan.meworking.data.server.memory;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;

public abstract class DataSink {
    public abstract <T extends Data, I, K extends KnowledgePackage<I,T>> void processWrappedData(DataWrapper<T, I> wrappedData);
}
