package com.ewan.dunjeon.server.world.entities.memory;

import com.esotericsoftware.kryo.kryo5.serializers.FieldSerializer;
import com.esotericsoftware.kryo.kryo5.serializers.TaggedFieldSerializer;
import com.esotericsoftware.kryo.kryo5.util.Null;
import com.ewan.dunjeon.data.Data;
import lombok.Getter;

import java.beans.Transient;


@Getter
public class KnowledgeFragment<D extends Data> {

    @FieldSerializer.Optional("")
    final Source source;
    private final double timestamp;
    final D info;

    public interface Source { }

    public KnowledgeFragment(D info, Source source, double timestamp) {
        this.info = info;
        this.source = source;
        this.timestamp = timestamp;
    }

    public KnowledgeFragment() {
        source = null;
        timestamp = 0;
        info = null;
    }
}
