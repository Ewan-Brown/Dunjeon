package com.ewan.meworking.data.server.memory;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;

public interface MemoryBankListener {
    <T extends Data, I, P extends KnowledgePackage<I,T>> void processWrappedData(DataWrapper<T, I> dataWrapper);
}
