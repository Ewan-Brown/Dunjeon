package com.ewan.dunjeoncommon.memory;

import com.ewan.dunjeoncommon.data.Data;
import com.ewan.dunjeoncommon.data.DataWrapper;

public abstract class DataSink {

    public abstract <T extends Data, I, K extends KnowledgePackage<I,T>> void processWrappedData(DataWrapper<T, I> wrappedData);
}
