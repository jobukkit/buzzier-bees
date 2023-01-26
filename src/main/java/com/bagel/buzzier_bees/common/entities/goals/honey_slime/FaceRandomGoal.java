package com.bagel.buzzier_bees.common.entities.goals.honey_slime;

import com.bagel.buzzier_bees.common.entities.HoneySlimeEntity;
import com.bagel.buzzier_bees.common.entities.controllers.HoneySlimeMoveHelperController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Effects;

import java.util.EnumSet;

public class FaceRandomGoal extends Goal {
    private final HoneySlimeEntity slime;
    private float chosenDegrees;
    private int nextRandomizeTime;

    public FaceRandomGoal(HoneySlimeEntity slimeIn) {
        this.slime = slimeIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(Effects.LEVITATION)) && this.slime.getMoveHelper() instanceof HoneySlimeMoveHelperController;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
            this.chosenDegrees = (float) this.slime.getRNG().nextInt(360);
        }

        ((HoneySlimeMoveHelperController) this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
    }
}
