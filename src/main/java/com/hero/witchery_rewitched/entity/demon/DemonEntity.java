package com.hero.witchery_rewitched.entity.demon;

import com.hero.witchery_rewitched.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;

public class DemonEntity extends MonsterEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);

    public DemonEntity(World world){this(ModEntities.DEMON.get(), world);}
    public DemonEntity(EntityType<? extends MonsterEntity> type, World worldIn){super(type, worldIn);}
    public DemonEntity(FMLPlayMessages.SpawnEntity message, World world){this(ModEntities.DEMON.get(), world);}

    public static AttributeModifierMap.MutableAttribute registerAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 18);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
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
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new WaterAvoidingRandomWalkingGoal(this,.3f ));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new FireballAttackGoal(this));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, (pred) -> true));

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getMsgId().contains("fire") || pSource.getMsgId().contains("lava")){
            pAmount = Math.min(pAmount, 0);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController( this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
    {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.demon.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


    static class FireballAttackGoal extends Goal {
        private final DemonEntity demon;
        public int chargeTime;

        public FireballAttackGoal(DemonEntity p_i45837_1_) {
            this.demon = p_i45837_1_;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return this.demon.getTarget() != null;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.chargeTime = 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        //public void stop() {this.demon.setCharging(false);}

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = this.demon.getTarget();
            double d0 = 64.0D;
            if (livingentity.distanceToSqr(this.demon) < 4096.0D && this.demon.canSee(livingentity)) {
                World world = this.demon.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.demon.isSilent()) {
                    world.levelEvent((PlayerEntity)null, 1015, this.demon.blockPosition(), 0);
                }

                if (this.chargeTime == 20) {
                    double d1 = 4.0D;
                    Vector3d vector3d = this.demon.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.demon.getX() + vector3d.x * 4.0D);
                    double d3 = livingentity.getY(0.5D) - (0.5D + this.demon.getY(0.5D));
                    double d4 = livingentity.getZ() - (this.demon.getZ() + vector3d.z * 4.0D);
                    if (!this.demon.isSilent()) {
                        world.levelEvent((PlayerEntity)null, 1016, this.demon.blockPosition(), 0);
                    }

                    FireballEntity fireballentity = new FireballEntity(world, this.demon, d2, d3, d4);
                    fireballentity.explosionPower = 1;
                    fireballentity.setPos(this.demon.getX() + vector3d.x * 4.0D, this.demon.getY(0.5D) + 0.5D, fireballentity.getZ() + vector3d.z * 4.0D);
                    world.addFreshEntity(fireballentity);
                    this.chargeTime = -40;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            //this.demon.setCharging(this.chargeTime > 10);
        }
    }

    static class LookAroundGoal extends Goal {
        private final GhastEntity ghast;

        public LookAroundGoal(GhastEntity p_i45839_1_) {
            this.ghast = p_i45839_1_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vector3d vector3d = this.ghast.getDeltaMovement();
                this.ghast.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
                this.ghast.yBodyRot = this.ghast.yRot;
            } else {
                LivingEntity livingentity = this.ghast.getTarget();
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.ghast) < 4096.0D) {
                    double d1 = livingentity.getX() - this.ghast.getX();
                    double d2 = livingentity.getZ() - this.ghast.getZ();
                    this.ghast.yRot = -((float)MathHelper.atan2(d1, d2)) * (180F / (float)Math.PI);
                    this.ghast.yBodyRot = this.ghast.yRot;
                }
            }

        }
    }
}
