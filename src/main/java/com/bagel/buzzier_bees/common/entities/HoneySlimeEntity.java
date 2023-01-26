package com.bagel.buzzier_bees.common.entities;

import com.bagel.buzzier_bees.common.entities.controllers.HoneySlimeMoveHelperController;
import com.bagel.buzzier_bees.common.entities.goals.honey_slime.*;
import com.bagel.buzzier_bees.core.registry.BBEntities;
import com.bagel.buzzier_bees.core.registry.BBItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;

import javax.annotation.Nullable;
import java.util.Random;

public class HoneySlimeEntity extends AnimalEntity implements IMob {
   private static final DataParameter<Boolean> IN_HONEY = EntityDataManager.createKey(HoneySlimeEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> IN_HONEY_GROWTH_TIME = EntityDataManager.createKey(HoneySlimeEntity.class, DataSerializers.VARINT);
   private static final Ingredient BREEDING_ITEM = Ingredient.fromItems(Items.SUGAR);

   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;

   private boolean wasOnGround;

   public HoneySlimeEntity(EntityType<? extends HoneySlimeEntity> type, World worldIn) {
      super(type, worldIn);
      this.moveController = new HoneySlimeMoveHelperController(this);
   }

   protected void registerGoals() {
	  this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, BREEDING_ITEM));
      this.goalSelector.addGoal(4, new HopGoal(this));
      this.goalSelector.addGoal(5, new FaceRandomGoal(this));
      this.goalSelector.addGoal(6, new HurtByTargetGoal(this, new Class[0]));
      this.goalSelector.addGoal(7, new RevengeGoal(this));
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.setSlimeSize(2, true);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   @Override
   public ItemStack getPickedResult(RayTraceResult target) {
      return new ItemStack(BBItems.HONEY_SLIME_SPAWN_EGG.get());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void setSlimeSize(int size, boolean resetHealth) {
      this.recenterBoundingBox();
      this.recalculateSize();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(size * size));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)size);
      if (resetHealth) {
         this.setHealth(this.getMaxHealth());
      }

      this.experienceValue = size;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(IN_HONEY, false);
      this.dataManager.register(IN_HONEY_GROWTH_TIME, 0);
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      this.recalculateSize();
      this.rotationYaw = this.rotationYawHead;
      this.renderYawOffset = this.rotationYawHead;
      if (this.isInWater() && this.rand.nextInt(20) == 0) {
         this.doWaterSplashEffect();
      }
      super.notifyDataManagerChange(key);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putBoolean("inHoney", this.isInHoney());
      compound.putInt("inHoneyGrowthTimer", this.getInHoneyGrowthTime());
      compound.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setInHoney(compound.getBoolean("inHoney"));
      this.setInHoneyGrowthTime(compound.getInt("inHoneyGrowthTimer"));
      this.wasOnGround = compound.getBoolean("wasOnGround");
   }

   public boolean isInHoney() {
      return this.dataManager.get(IN_HONEY);
   }

   public void setInHoney(boolean value) {
      this.dataManager.set(IN_HONEY, value);
   }

   public int getInHoneyGrowthTime() {
      return this.dataManager.get(IN_HONEY_GROWTH_TIME);
   }

   public void setInHoneyGrowthTime(int value) {
      this.dataManager.set(IN_HONEY_GROWTH_TIME, value);
   }

   @Override
   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      World world = player.getEntityWorld();
      if (!this.isChild() && this.isInHoney()) {
         //Bottling
         if (itemstack.getItem() == Items.GLASS_BOTTLE) {
            world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            if (!player.abilities.isCreativeMode) {
               itemstack.shrink(1);
               if (itemstack.isEmpty()) {
                  player.setHeldItem(hand, new ItemStack(Items.HONEY_BOTTLE));
               } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE))) {
                  player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
               }
            }

            this.setRevengeTarget(player);
            getHoneyFromSlime(this);
            return true;
         }
         //Wanding
         else if (itemstack.getItem() == BBItems.HONEY_WAND.get()) {
            world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            if (!player.abilities.isCreativeMode) {
               player.setHeldItem(hand, new ItemStack(BBItems.STICKY_HONEY_WAND.get()));
            }
            getHoneyFromSlime(this);
            return true;
         }
      }
      return super.processInteract(player, hand);
   }

   private void getHoneyFromSlime(LivingEntity entity) {
      if (entity instanceof HoneySlimeEntity) {
         this.setInHoney(false);
         this.setInHoneyGrowthTime(-14400);
      }
   }

   @Override
   public void tick() {
      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int i = 2;

         if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
         for (int j = 0; j < i * 8; ++j) {
            float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Blocks.HONEY_BLOCK)), this.getPosX() + (double) f2, this.getPosY(), this.getPosZ() + (double) f3, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   @Override
   public void livingTick() {
      super.livingTick();
      if (this.isAlive()) {
         if (!isInHoney()) {
            setInHoneyGrowthTime(getInHoneyGrowthTime() + 1);
         }
         setInHoney(getInHoneyGrowthTime() >= 0);
      }
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return BREEDING_ITEM.test(stack);
   }

   @Nullable
   @Override
   public AgeableEntity createChild(AgeableEntity ageable) {
      HoneySlimeEntity childHoneySlimeEntity = BBEntities.HONEY_SLIME.get().create(this.world);
      childHoneySlimeEntity.setSlimeSize(1, true);
      return childHoneySlimeEntity;
   }

   protected void onGrowingAdult() {
      super.onGrowingAdult();
      if (!this.isChild()) {
         this.setSlimeSize(2, true);
      }
   }

   public static boolean honeySlimeCondition(EntityType<HoneySlimeEntity> honeySlime, IWorld world, SpawnReason reason, BlockPos position, Random randomIn) {
      if (world.getWorldInfo().getGenerator().handleSlimeSpawnReduction(randomIn, world) && randomIn.nextInt(6) != 1) {
         return false;
      }
      else {
         if (world.getDifficulty() != Difficulty.PEACEFUL) {
            if (position.getY() > 50 && position.getY() < 90 && randomIn.nextFloat() < 0.5F && world.getLight(position) >= randomIn.nextInt(8)) {
               return canSpawnOn(honeySlime, world, reason, position, randomIn);
            }
         }

         return false;
      }
   }

   protected void dealDamage(LivingEntity entityIn) {
      if (this.isAlive()) {
         int i = 2;
         if (this.getDistanceSq(entityIn) < 0.6D * (double) i * 0.6D * (double) i && this.canEntityBeSeen(entityIn) && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
            this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.applyEnchantments(this, entityIn);
         }
      }
   }

   public boolean canDamagePlayer() {
      return !this.isChild() && this.isServerWorld();
   }

   public void onCollideWithPlayer(PlayerEntity entityIn) {
      if (this.canDamagePlayer() && this.getRevengeTarget() == entityIn) {
         this.dealDamage(entityIn);
      }
   }

   @SuppressWarnings("unchecked")
   public EntityType<? extends HoneySlimeEntity> getType() {
      return (EntityType<? extends HoneySlimeEntity>) super.getType();
   }

   @Override
   public void remove(boolean keepData) {
      super.remove(keepData);
   }

   public void applyEntityCollision(Entity entityIn) {
      super.applyEntityCollision(entityIn);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.625F * sizeIn.height;
   }

   public void recalculateSize() {
      double d0 = this.getPosX();
      double d1 = this.getPosY();
      double d2 = this.getPosZ();
      super.recalculateSize();
      this.setPosition(d0, d1, d2);
   }

   @Override
   public boolean canBeLeashedTo(PlayerEntity player) {
      return !this.getLeashed() && this.attackingPlayer != player;
   }

   protected boolean func_225511_J_() {
      return !this.isChild();
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   public int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   protected float func_225512_er_() {
      return (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isChild() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isChild() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isChild() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
   }

   protected ResourceLocation getLootTable() {
      return this.isChild() ? this.getType().getLootTable() : LootTables.EMPTY;
   }

   public float getSoundVolume() {
      return 0.4F * (float) (isChild() ? 1 : 2);
   }

   public int getVerticalFaceSpeed() {
      return 0;
   }

   public boolean makesSoundOnJump() {
      return !this.isChild();
   }

   protected void jump() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x, (double) this.getJumpUpwardsMotion(), vec3d.z);
      this.isAirBorne = true;
   }

   public SoundEvent getJumpSound() {
      return this.isChild() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
   }

   protected boolean spawnCustomParticles() {
      return false;
   }


}