package com.bagel.buzzier_bees.common.entities.goals.honey_slime;

import com.bagel.buzzier_bees.common.entities.HoneySlimeEntity;
import com.bagel.buzzier_bees.common.entities.controllers.HoneySlimeMoveHelperController;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FloatGoal extends Goal {
    private final HoneySlimeEntity slime;

    public FloatGoal(HoneySlimeEntity slimeIn) {
        this.slime = slimeIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        slimeIn.getNavigator().setCanSwim(true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveHelper() instanceof HoneySlimeMoveHelperController;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpController().setJumping();
        }

        ((HoneySlimeMoveHelperController) this.slime.getMoveHelper()).setSpeed(1.2D);
    }
}
