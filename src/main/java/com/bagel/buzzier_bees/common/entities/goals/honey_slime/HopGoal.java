package com.bagel.buzzier_bees.common.entities.goals.honey_slime;

import com.bagel.buzzier_bees.common.entities.HoneySlimeEntity;
import com.bagel.buzzier_bees.common.entities.controllers.HoneySlimeMoveHelperController;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class HopGoal extends Goal {
    private final HoneySlimeEntity slime;

    public HopGoal(HoneySlimeEntity slimeIn) {
        this.slime = slimeIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return !this.slime.isPassenger() && this.slime.getMoveHelper() instanceof HoneySlimeMoveHelperController;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        ((HoneySlimeMoveHelperController) this.slime.getMoveHelper()).setSpeed(1.0D);
    }
}