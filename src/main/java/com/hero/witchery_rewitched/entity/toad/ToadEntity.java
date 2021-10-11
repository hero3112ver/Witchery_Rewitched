package com.hero.witchery_rewitched.entity.toad;

import com.hero.witchery_rewitched.init.ModEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class ToadEntity extends TameableEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);
    private int jumpTicks;
    private int jumpDuration;
    private int jumpDelayTicks;
    private boolean wasOnGround;

    //TODO: Make tameable entity easier and implemented
    public ToadEntity(World p_i48574_2_){
        this(ModEntities.TOAD.get(), p_i48574_2_);
    }

    public ToadEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
        this.jumpControl = new ToadEntity.JumpHelperController(this);
        this.moveControl = new ToadEntity.MoveHelperController(this);
    }

    public ToadEntity(FMLPlayMessages.SpawnEntity message, World world) {
        this(ModEntities.TOAD.get(), world);
    }
    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.15F);
    }



    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.ROTTEN_FLESH), false));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        super.registerGoals();
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        ToadEntity toad = ModEntities.TOAD.get().create(p_241840_1_);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            toad.setOwnerUUID(uuid);
            toad.setTame(true);
        }
        return toad;
    }

    public void setOwnerUUID(@Nullable UUID p_184754_1_) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_184754_1_));
    }

    public void tame(PlayerEntity pPlayer) {
        this.setTame(true);
        this.setOwnerUUID(pPlayer.getUUID());
        if (pPlayer instanceof ServerPlayerEntity) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)pPlayer, this);
        }

    }

    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        switch (spawnReasonIn){
            case EVENT:
            case TRIGGERED:
            case MOB_SUMMONED:
            case DISPENSER:
            case COMMAND:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.getItem() == Items.ROTTEN_FLESH;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
    {
        if(event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toad.jump", true));
        }
        else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toad.idle", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController( this, "controller", 0, this::predicate));
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity pPlayer, Hand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        this.getAge();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(pPlayer) || this.isTame() || item == Items.ROTTEN_FLESH && !this.isTame();
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        }else {
            if (this.isTame()) {
                if (item == Items.ROTTEN_FLESH && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.abilities.instabuild) {
                        itemstack.shrink(1);
                    }

                    this.heal((float) item.getFoodProperties().getNutrition());
                    return ActionResultType.SUCCESS;
                }
                else {
                    ActionResultType actionresulttype = super.mobInteract(pPlayer, pHand);
                    if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(pPlayer)) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((LivingEntity)null);
                        return ActionResultType.SUCCESS;
                    }

                    return actionresulttype;
                }

            } else if (item == Items.ROTTEN_FLESH) {
                if (!pPlayer.abilities.instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                    this.tame(pPlayer);
                    this.navigation.stop();
                    this.setTarget((LivingEntity)null);
                    this.setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return ActionResultType.SUCCESS;
            }

            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public void setTame(boolean tame){
        super.setTame(tame);
        if (tame) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
            this.setHealth(8.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(3.0D);
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }

    }

    public class JumpHelperController extends JumpController {
        private final ToadEntity toad;
        private boolean canJump;

        public JumpHelperController(ToadEntity p_i45863_2_) {
            super(p_i45863_2_);
            this.toad = p_i45863_2_;
        }

        public boolean wantJump() {
            return this.jump;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean pCanJump) {
            this.canJump = pCanJump;
        }

        /**
         * Called to actually make the entity jump if isJumping is true.
         */
        public void tick() {
            if (this.jump) {
                this.toad.startJumping();
                this.jump = false;
            }

        }
    }

    static class MoveHelperController extends MovementController {
        private final ToadEntity toad;
        private double nextJumpSpeed;

        public MoveHelperController(ToadEntity p_i45862_1_) {
            super(p_i45862_1_);
            this.toad = p_i45862_1_;
        }


        public void tick() {
            if (this.toad.onGround && !this.toad.jumping && !((ToadEntity.JumpHelperController)this.toad.jumpControl).wantJump()) {
                this.toad.setSpeedModifier(0.0D);
            } else if (this.hasWanted()) {
                this.toad.setSpeedModifier(this.nextJumpSpeed);
            }

            super.tick();
        }

        /**
         * Sets the speed and location to move to
         */
        public void setWantedPosition(double pX, double pY, double pZ, double pSpeed) {
            if (this.toad.isInWater()) {
                pSpeed = 1.5D;
            }

            super.setWantedPosition(pX, pY, pZ, pSpeed);
            if (pSpeed > 0.0D) {
                this.nextJumpSpeed = pSpeed;
            }

        }
    }

    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0D) {
            double d1 = getHorizontalDistanceSqr(this.getDeltaMovement());
            if (d1 < 0.01D) {
                this.moveRelative(0.3F, new Vector3d(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)1);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public float getJumpCompletion(float p_175521_1_) {
        return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
    }

    public void setSpeedModifier(double pNewSpeed) {
        this.getNavigation().setSpeedModifier(pNewSpeed);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), pNewSpeed);
    }

    public void setJumping(boolean pJumping) {
        super.setJumping(pJumping);
        if (pJumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }
    protected SoundEvent getJumpSound() {
        return SoundEvents.RABBIT_JUMP;
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }


    public void customServerAiStep() {
        if (this.jumpDelayTicks > 0) {
            --this.jumpDelayTicks;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            if ( this.jumpDelayTicks == 0) {
                LivingEntity livingentity = this.getTarget();
                if (livingentity != null && this.distanceToSqr(livingentity) < 16.0D) {
                    this.facePoint(livingentity.getX(), livingentity.getZ());
                    this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
                    this.startJumping();
                    this.wasOnGround = true;
                }
            }

            ToadEntity.JumpHelperController toadentity$jumphelpercontroller = (ToadEntity.JumpHelperController)this.jumpControl;
            if (!toadentity$jumphelpercontroller.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    Path path = this.navigation.getPath();
                    Vector3d vector3d = new Vector3d(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) {
                        vector3d = path.getNextEntityPos(this);
                    }

                    this.facePoint(vector3d.x, vector3d.z);
                    this.startJumping();
                }
            } else if (!toadentity$jumphelpercontroller.canJump()) {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround;
    }

    public boolean canSpawnSprintParticle() {
        return false;
    }

    private void facePoint(double pX, double pZ) {
        this.yRot = (float)(MathHelper.atan2(pZ - this.getZ(), pX - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F;
    }

    private void enableJumpControl() {
        ((ToadEntity.JumpHelperController)this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((ToadEntity.JumpHelperController)this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        if (this.moveControl.getSpeedModifier() < 2.2D) {
            this.jumpDelayTicks = 20;
        } else {
            this.jumpDelayTicks = 1;
        }

    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
}
