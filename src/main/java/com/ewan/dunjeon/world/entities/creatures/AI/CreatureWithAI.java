package com.ewan.dunjeon.world.entities.creatures.AI;

import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CreatureWithAI extends Creature {

    List<AIStateGenerator> AIStateGeneratorsByPriority = new ArrayList<>();
    int currentStatePriority = -1;
    AIState currentState;

    public CreatureWithAI(String name, List<AIStateGenerator> stateGenerators) {
        super(name);
        this.AIStateGeneratorsByPriority = stateGenerators;
    }

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
        processAI();

    }

    private void processAI(){

        boolean currentStateCanContinue = currentState != null && currentState.getStateData().canContinue();

        int highestPossibleStatePriority = -1;
        AIStateGenerator highestPriorityGenerator = null;
        for (int i = 0; i < AIStateGeneratorsByPriority.size(); i++) {
            AIStateGenerator gen = AIStateGeneratorsByPriority.get(i);
            if(gen.validityCheck.test(this)) {
                //Skip the current state if it can no longer continue.
                if(i != currentStatePriority || currentStateCanContinue) {
                    highestPossibleStatePriority = i;
                    highestPriorityGenerator = gen;
                    break;
                }
            }
        }

        if(highestPossibleStatePriority == -1){
            throw new IllegalStateException("No valid states found by AI at this time! This is catastrophic!");
        } else{
            if(highestPossibleStatePriority < currentStatePriority || currentStatePriority == -1 || !currentStateCanContinue){
                currentState = highestPriorityGenerator.AIStateSupplier.apply(this);
                currentStatePriority = highestPossibleStatePriority;
            }
            currentState.process();
        }

    }

    /**
     * Holds logic connecting a condition to a potential new State.
     */
    protected static class AIStateGenerator{

        public AIStateGenerator(Predicate<Creature> validityCheck, Function<Creature, AIState> AIStateSupplier) {
            this.validityCheck = validityCheck;
            this.AIStateSupplier = AIStateSupplier;
        }

        Predicate<Creature> validityCheck;
        Function<Creature,AIState> AIStateSupplier;
    }
}
