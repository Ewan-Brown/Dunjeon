package com.ewan.dunjeon.world.entities;

public class SimpleProjectile{

//    private double thresholdSpeed = 0.001f;
//    private boolean hasCollided = false;

//    public SimpleProjectile(Color c, String name) {
//        super(c, name);
//        friction = 0f;
//    }

//    @Override
//    public void update() {
//        super.update();
//    }
//
//    //TODO Make collision managed by a singleton class like soundmanager
//    @Override
//    public void onCollideWithWall(BasicCell cell) {
//        super.onCollideWithWall(cell);
//        hasCollided = true;
//        World.getInstance().getSoundManager().exposeSound(new AbsoluteSoundEvent(2, new Point2D.Double(getPosX(), getPosY()), getFloor(), "The projectile hits the wall", "Clang!", AbsoluteSoundEvent.SoundType.PHYSICAL, this));
//        this.getFloor().removeEntity(this);
//    }
//
//    @Override
//    public void onCollideWithEntity(Entity e) {
//        super.onCollideWithEntity(e);
//        hasCollided = true;
//        World.getInstance().getSoundManager().exposeSound(new AbsoluteSoundEvent(2, new Point2D.Double(getPosX(), getPosY()), getFloor(), "The projectile hits the " + e.getName(), "Thunk!", AbsoluteSoundEvent.SoundType.PHYSICAL, this));
//        this.getFloor().removeEntity(this);
//    }
//
//    @Override
//    public boolean exists() {
//        return !hasCollided;
//    }
}
