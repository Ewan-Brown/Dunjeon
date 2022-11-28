package com.ewan.dunjeon.world.entities.creatures;

import lombok.NonNull;

public class StandAroundIdiotAI extends AIState{

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
    public @NonNull StandAroundIdiotAIStateData getStateData() {
        return new StandAroundIdiotAIStateData();
    }
}
