package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;
import lombok.Getter;

@Getter
public class KnowledgeFragment<D extends Data> {
    final D info;
    final Source source;
    private final Double timestamp;

    public interface Source { }

    public KnowledgeFragment(D info, Source source, Double timestamp) {
        this.info = info;
        this.source = source;
        this.timestamp = timestamp;
    }
}
