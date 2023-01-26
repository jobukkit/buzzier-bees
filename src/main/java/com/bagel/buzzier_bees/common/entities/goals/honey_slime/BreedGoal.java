package com.bagel.buzzier_bees.common.entities.goals.honey_slime;

import com.bagel.buzzier_bees.common.entities.HoneySlimeEntity;
import com.bagel.buzzier_bees.common.entities.controllers.HoneySlimeMoveHelperController;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BreedGoal extends Goal {
    private static final EntityPredicate field_220689_d = (new EntityPredicate()).setDistance(8.0D).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired();
    protected final HoneySlimeEntity slime;
    private final Class<? extends AnimalEntity> mateClass;
    protected final World world;
    protected AnimalEntity field_75391_e;
    private int spawnBabyDelay;
    @SuppressWarnings("unused")
	private final double moveSpeed;

    public BreedGoal(HoneySlimeEntity animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public BreedGoal(HoneySlimeEntity p_i47306_1_, double p_i47306_2_, Class<? extends AnimalEntity> p_i47306_4_) {
        this.slime = p_i47306_1_;
        this.world = p_i47306_1_.world;
        this.mateClass = p_i47306_4_;
        this.moveSpeed = p_i47306_2_;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.slime.isInLove()) {
            return false;
        } else {
            this.field_75391_e = this.getNearbyMate();
            return this.field_75391_e == null ? false : this.slime.getMoveHelper() instanceof HoneySlimeMoveHelperController;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return this.field_75391_e.isAlive() && this.field_75391_e.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.field_75391_e = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.slime.getLookController().setLookPositionWithEntity(this.field_75391_e, 10.0F, (float)this.slime.getVerticalFaceSpeed());

        this.slime.faceEntity(this.field_75391_e, 10.0F, 10.0F);
        ((HoneySlimeMoveHelperController) this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, true);
        ((HoneySlimeMoveHelperController) this.slime.getMoveHelper()).setSpeed(1.0D);

        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.slime.getDistanceSq(this.field_75391_e) < 9.0D) {
            this.spawnBaby();
        }

    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    @Nullable
    private AnimalEntity getNearbyMate() {
        List<AnimalEntity> list = this.world.getTargettableEntitiesWithinAABB(this.mateClass, field_220689_d, this.slime, this.slime.getBoundingBox().grow(8.0D));
        double d0 = Double.MAX_VALUE;
        AnimalEntity animalentity = null;

        for(AnimalEntity animalentity1 : list) {
            if (this.slime.canMateWith(animalentity1) && this.slime.getDistanceSq(animalentity1) < d0) {
                animalentity = animalentity1;
                d0 = this.slime.getDistanceSq(animalentity1);
            }
        }

        return animalentity;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    protected void spawnBaby() {
        AgeableEntity ageableentity = this.slime.createChild(this.field_75391_e);
        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(slime, field_75391_e, ageableentity);
        final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.slime.setGrowingAge(6000);
            this.field_75391_e.setGrowingAge(6000);
            this.slime.resetInLove();
            this.field_75391_e.resetInLove();
            return;
        }
        if (ageableentity != null) {
            ServerPlayerEntity serverplayerentity = this.slime.getLoveCause();
            if (serverplayerentity == null && this.field_75391_e.getLoveCause() != null) {
                serverplayerentity = this.field_75391_e.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.slime, this.field_75391_e, ageableentity);
            }

            this.slime.setGrowingAge(6000);
            this.field_75391_e.setGrowingAge(6000);
            this.slime.resetInLove();
            this.field_75391_e.resetInLove();
            ageableentity.setGrowingAge(-24000);
            ageableentity.setLocationAndAngles(this.slime.getPosX(), this.slime.getPosY(), this.slime.getPosZ(), 0.0F, 0.0F);
            this.world.addEntity(ageableentity);
            this.world.setEntityState(this.slime, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.slime.getPosX(), this.slime.getPosY(), this.slime.getPosZ(), this.slime.getRNG().nextInt(7) + 1));
            }

        }
    }
}
