package com.ewan.dunjeon.world.entities.actions;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.AttackData;
import com.ewan.dunjeon.world.entities.Entity;

public class MeleeAttackAction extends TimedAction {

    private AttackData attackData;

    public MeleeAttackAction(AttackData data){
        super(data.getTimeToHit());
        this.attackData = data;
    }

    @Override
    public void onTimerComplete() {
        System.out.println("\tSwing! from " + actor.getClass());
        int x = actor.getX() + attackData.getX();
        int y = actor.getY() + attackData.getY();
        BasicCell attackedCell = actor.getContainingCell().getFloor().getCellAt(x, y);
        Entity attackedEntity = attackedCell.getEntity();
        if(attackedEntity == null){
            System.out.println("\t\tand a miss...");
        }else{
            System.out.printf("\t\tWhack! for %d damage\n", attackData.getDamage());
            attackedEntity.applyDamage(attackData.getDamage());
        }
    }

    @Override
    public void update() {
        super.update();
    }

}
