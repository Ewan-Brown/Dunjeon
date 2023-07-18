package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;
import lombok.Getter;

@Getter
public class KnowledgeFragment<D extends Data> {
    final D info;
    final Source source;
    private final double timestamp;

    public interface Source { }

    public KnowledgeFragment(D info, Source source, double timestamp) {
        this.info = info;
        this.source = source;
        this.timestamp = timestamp;
    }
}
