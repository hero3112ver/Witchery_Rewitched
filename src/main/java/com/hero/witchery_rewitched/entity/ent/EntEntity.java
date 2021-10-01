package com.hero.witchery_rewitched.entity.ent;

import com.hero.witchery_rewitched.init.ModEntities;
import net.minecraft.advancements.criterion.EntityHurtPlayerTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntEntity extends MonsterEntity {
    private int variant = 0;


    public EntEntity(World world){this(ModEntities.ENT.get(), world);}
    public EntEntity(EntityType<? extends MonsterEntity> type, World worldIn){super(type, worldIn);}
    public EntEntity(FMLPlayMessages.SpawnEntity message, World world){this(ModEntities.ENT.get(), world);}

    public static AttributeModifierMap.MutableAttribute registerAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 11);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level.broadcastEntityEvent(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean flag = pEntity.hurt(DamageSource.mobAttack(this), f1);
        if (flag) {
            pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(0.0D, (double)0.4F, 0.0D));
            this.doEnchantDamageEffects(this, pEntity);
        }

        return flag;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 8.0F));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, (pred) -> true));
        super.registerGoals();
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
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!pSource.getMsgId().equals("player")){
            pAmount = Math.min(pAmount, 15);
        }
        return super.hurt(pSource, pAmount);
    }

    @SubscribeEvent
    public static void damageEnt(LivingDamageEvent event){
        if(event.getEntity() instanceof EntEntity){
            if(event.getSource().getMsgId().equals("player")){
                PlayerEntity player = event.getSource().getEntity() instanceof PlayerEntity ? (PlayerEntity)event.getSource().getEntity() : null;
                ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
                if(stack.getItem() instanceof AxeItem){
                    event.setAmount(event.getAmount() * 3);
                }
                else {
                    event.setAmount(Math.min(event.getAmount(), 15));
                }
            }
        }
    }
}
