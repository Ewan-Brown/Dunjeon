package com.ewan.meworking.data.server.memory;

import com.esotericsoftware.kryo.kryo5.serializers.FieldSerializer;
import com.ewan.meworking.data.server.data.Data;
import lombok.Getter;


@Getter
public class KnowledgeFragment<D extends Data> {

    final Source source;
    private final double timestamp;
    final D info;

    public interface Source { }

    public KnowledgeFragment(D info, Source source, double timestamp) {
        this.info = info;
        this.source = source;
        this.timestamp = timestamp;
    }

}
