package com.ewan.dunjeon.world.entities.actions;

import com.ewan.dunjeon.world.entities.AttackData;
import com.ewan.dunjeon.world.entities.Entity;

public class MeleeAttackAction extends TimedAction {

    private Entity attacker, attackee;
    private AttackData attackData;

    public MeleeAttackAction(Entity attacker, Entity attackee, AttackData data){
        super(data.getTimeToHit());
        this.attacker = attacker;
        this.attackee = attackee;
        this.attackData = data;
    }

    @Override
    public void onTimerComplete() {
        System.out.println("\tI've attacked!");
    }

    @Override
    public void update() {
        super.update();
    }

}
