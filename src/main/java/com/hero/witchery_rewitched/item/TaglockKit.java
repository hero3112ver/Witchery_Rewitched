package com.hero.witchery_rewitched.item;

import com.hero.witchery_rewitched.util.util.PlayerUtils;
import com.hero.witchery_rewitched.util.capabilities.player.IPlayer;
import com.hero.witchery_rewitched.util.capabilities.player.PlayerCapability;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.UUID;

public class TaglockKit extends Item {
    public TaglockKit(Properties properties) {
        super(properties);
    }



    public static ItemStack giveStackWithPlayerInfo(ItemStack stack, UUID id, String name){
        CompoundNBT stackNBT = stack.getOrCreateTag();
        stackNBT.putString("PlayerName", name);
        stackNBT.putUUID("PlayerID", id);
        return stack;
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if(world.getBlockState(pos).is(BlockTags.BEDS))
        {
            if(world.getBlockState(pos).getValue(BedBlock.PART) == BedPart.FOOT)
            {
                if(world.getBlockState(pos.offset(1,0,0)).is(BlockTags.BEDS) && world.getBlockState(pos.offset(1,0,0)).getValue(BedBlock.PART) != BedPart.FOOT){
                    pos = pos.offset(1,0,0);
                }
                else if(world.getBlockState(pos.offset(0,0,1)).is(BlockTags.BEDS) && world.getBlockState(pos.offset(0,0,1)).getValue(BedBlock.PART) != BedPart.FOOT){
                    pos = pos.offset(0,0,1);
                }
                else if(world.getBlockState(pos.offset(-1,0,0)).is(BlockTags.BEDS) && world.getBlockState(pos.offset(-1,0,0)).getValue(BedBlock.PART) != BedPart.FOOT){
                    pos = pos.offset(-1,0,0);
                } else if(world.getBlockState(pos.offset(0,0,-1)).is(BlockTags.BEDS) && world.getBlockState(pos.offset(0,0,-1)).getValue(BedBlock.PART) != BedPart.FOOT){
                    pos = pos.offset(0,0,-1);
                }
                else{return ActionResultType.SUCCESS;}
            }
            TileEntity te = world.getBlockEntity(pos);
            if(te == null)
                return ActionResultType.SUCCESS;
            IPlayer playerData = te.getCapability(PlayerCapability.INSTANCE).orElse(null);
            if(playerData != null && playerData.getPlayerID() != null) {
                if(tryGiveTaglock(player, playerData, world))
                    player.setItemInHand(context.getHand(), new ItemStack(player.getItemInHand(context.getHand()).getItem(), player.getItemInHand(context.getHand()).getCount()-1));
            }
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack pStack, PlayerEntity pPlayerIn, LivingEntity pTarget, Hand pHand) {
        if(pTarget instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) pTarget;
            if(!playerEntity.is(pPlayerIn))
                possibleNotify( pPlayerIn, playerEntity);
            PlayerUtils.giveItem(pPlayerIn, giveStackWithPlayerInfo(new ItemStack(ModItems.FILLED_TAGLOCK.get()), playerEntity.getUUID(), playerEntity.getName().getString()));
        }
        return super.interactLivingEntity(pStack, pPlayerIn, pTarget, pHand);
    }

    private boolean tryGiveTaglock(PlayerEntity player, IPlayer source, World world){
        if(player != null && source.getPlayerID() != null && world.getPlayerByUUID(source.getPlayerID())  != null) {
            ItemStack stack = new ItemStack(ModItems.FILLED_TAGLOCK.get(), 1);
            giveStackWithPlayerInfo( stack, source.getPlayerID(), world.getPlayerByUUID(source.getPlayerID()).getName().getString());
            PlayerUtils.giveItem(player,stack);
            return true;
        }
        return false;
    }

    private void possibleNotify(PlayerEntity source, PlayerEntity target){
        int notifyChance = 1;
        if(!facingAway(source, target)) notifyChance += 7;
        if(!source.isCrouching()) notifyChance += 4;

        Random rand = new Random();
        if(rand.nextInt(10) < notifyChance){
            notifyPlayer(target, source);
        }
    }

    private void notifyPlayer(PlayerEntity player, PlayerEntity source){
        player.displayClientMessage(new StringTextComponent(source.getName().getString() + " pricked you with a taglock kit!"), false);
        DamageSource damageSource = new EntityDamageSource("taglock_kit", source);
        player.setLastHurtByPlayer(source);
        player.hurt(damageSource, .5f);
    }

    private boolean facingAway(PlayerEntity source, PlayerEntity target){
        Vector3d vv1 = source.getLookAngle().normalize();
        Vector3d vv2 = target.getLookAngle().normalize();

        Vector2f v1 = new Vector2f((float)vv1.x, (float)vv1.z);
        Vector2f v2 = new Vector2f((float)vv2.x, (float)vv2.z);

        return !(Math.acos((v1.x * v2.x + v1.y *v2.y) / (Math.sqrt(v1.x * v1.x + v1.y * v1.y) * Math.sqrt(v2.x * v2.x + v2.y * v2.y))) > Math.PI/2);
    }

}
