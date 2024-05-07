package com.ewan.meworking.data.server.memory;

import com.esotericsoftware.kryo.kryo5.serializers.FieldSerializer;
import com.ewan.meworking.data.server.Timestamp;
import com.ewan.meworking.data.server.data.Data;
import lombok.Getter;

/**
 * Immutable, atomic data + requred time/source context
 * @param <D>
 */
@Getter
public class KnowledgeFragment<D extends Data> {

    private final Source source;
    private final Timestamp time;
    private final D info;

    public interface Source { }

    public KnowledgeFragment(D info, Source source, Timestamp time) {
        this.info = info;
        this.source = source;
        this.time = time;
    }

}
