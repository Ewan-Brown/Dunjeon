package com.ewan.meworking.data.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dyn4j.geometry.Vector2;

@AllArgsConstructor
@Getter
public class MoveEntity extends ClientAction{
    private final Vector2 moveDir;
}
