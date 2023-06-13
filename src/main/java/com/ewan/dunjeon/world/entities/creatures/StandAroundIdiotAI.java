package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.entities.creatures.AI.AIState;

public class StandAroundIdiotAI extends AIState {

    public StandAroundIdiotAI(Creature e) {
        super(e);
    }

    @Override
    public void process() {
        // Do nothing!
    }

    public static class StandAroundIdiotAIStateData extends AIStateData{
        @Override
        public boolean canContinue() {
            return true;
        }
    }

    @Override
    public StandAroundIdiotAIStateData getStateData() {
        return new StandAroundIdiotAIStateData();
    }
}
