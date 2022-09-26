package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.entities.memory.CellData;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.util.function.Predicate;

public class Player extends Creature{
    public Player(Color c, String name) {
        super(c, name);
    }

    @Override
    public void processSound(RelativeSoundEvent event) {
        super.processSound(event);
        //TODO This needs to be refined. What if the sound eminates from an invisible entity that is in viewing range?? Message should change.
//        boolean isVisible = getVisibleCellsData().stream().anyMatch(new Predicate<CellData>() {
//            @Override
//            public boolean test(CellData cellData) {
//                return cellData;
//            }
//        })
        int sourceX = (int)event.abs().sourceLocation().getX();
        int sourceY = (int)event.abs().sourceLocation().getY();
        boolean isVisible = !getFloorMemory(event.abs().sourceFloor()).getDataAt(sourceX, sourceY).isOldData();
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if(!message.isEmpty()) {
            System.out.println("["+message+"]");
        }
    }
}
