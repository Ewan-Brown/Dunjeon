package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.items.Item;

import java.awt.geom.AffineTransform;

public abstract class ItemAttack {
    private double timeElapsed = 0;
    private double totalTime;

    private ItemAttack(double totalTime){this.totalTime = totalTime;}

    double getPercentComplete() {return timeElapsed/totalTime;}
    public abstract AffineTransform getTransform();

    public static class StabAttack extends ItemAttack{

        private final double stabDist;

        public StabAttack(double totalTime, double stabDist) {
            super(totalTime);
            this.stabDist = stabDist;
        }

        public AffineTransform getTransform() {
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(stabDist * getPercentComplete(), 0);
            return affineTransform;
        }
    };

}
