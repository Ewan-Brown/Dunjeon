package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.items.Item;

import java.awt.geom.AffineTransform;

public abstract class ItemAttack {
    private float timeElapsed = 0;
    private float totalTime;

    private ItemAttack(float totalTime){this.totalTime = totalTime;}

    float getPercentComplete() {return timeElapsed/totalTime;}
    public abstract AffineTransform getTransform();

    public static class StabAttack extends ItemAttack{

        private final float stabDist;

        public StabAttack(float totalTime, float stabDist) {
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
