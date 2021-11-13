package com.hero.witchery_rewitched.util;

import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.util.listeners.AltarPowererReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AltarPowerer{
    HashMap<Block, Integer> blockAmount;
    HashMap<ITag.INamedTag<Block>, Integer> tagsAmount;
    int altarRange;
    int power = 0;

    final BlockPos pos;

    public AltarPowerer(BlockPos pos, World level, List <Block> blocks){
        blockAmount = new HashMap<>();
        for(Block block : AltarPowererReloadListener.INSTANCE.BLOCKS.keySet())
            blockAmount.put(block, 0);
        tagsAmount = new HashMap<>();
        for(ITag.INamedTag<Block> tag : AltarPowererReloadListener.INSTANCE.TAGS.keySet())
            tagsAmount.put(tag, 0);

        this.pos = pos;

        altarRange = WitcheryRewitchedConfig.Server.altarRadius.get() * (blocks.contains(ModBlocks.ARTHANA.get()) ? 2 : 1);

        for(int x = -altarRange/2; x < altarRange/2; x++){
            for(int y = -altarRange/2; y < altarRange/2; y++){
                for(int z = -altarRange/2; z < altarRange/2; z++){
                    BlockState state = level.getBlockState(pos.offset(x,y,z));
                    if(!(state.getBlock() == Blocks.AIR))
                        power += getPower(state.getBlock(), false);
                }
            }
        }
    }

    private AltarPowerer(BlockPos pos, int range, int power, HashMap<Block, Integer> blockAmount, HashMap<ITag.INamedTag<Block>, Integer> tagsAmount){
        altarRange = range;
        this.power = power;
        this.blockAmount = blockAmount;
        this.tagsAmount = tagsAmount;
        this.pos = pos;
    }

    public int calculateMaxEnergy(List<Block> blocks){
        int rate = 1;
        if(blocks.contains(Blocks.SKELETON_SKULL) && blocks.contains(Blocks.WITHER_SKELETON_SKULL))
            blocks.remove(Blocks.SKELETON_SKULL);
        /*if(blocks.contains(ModBlocks.FILLED_CHALICE.get()) & blocks.contains(ModBlocks.CHALICE.get()))
        blocks.remove(ModBlocks.CHALICE.get());*/
        for(Block block : blocks) {
            if (block == Blocks.SKELETON_SKULL)
                rate += 1;
            else if (block == Blocks.WITHER_SKELETON_SKULL)
                rate += 2;
            /* TODO: Uncomment this when the blocks are in place and maybe player heads https://ftb.fandom.com/wiki/Altar_(Witchery)
            else if(blocks == ModBlocks.CHALICE.get())
                rate += 2;
              else if(blocks == ModBlocks.FILLED_CHALICE.get())
                rate += 2;*/

        }
        return power * rate;
    }


    public int calculateRechargeRate(List<Block> blocks){
        int rate = 1;
        if(blocks.contains(Blocks.SKELETON_SKULL) && blocks.contains(Blocks.WITHER_SKELETON_SKULL))
            blocks.remove(Blocks.SKELETON_SKULL);
        for(Block block : blocks) {
            if (block == Blocks.SKELETON_SKULL)
                rate += 1;
            else if (block == Blocks.WITHER_SKELETON_SKULL)
                rate += 2;
            else if (block == Blocks.TORCH)
                rate += 1;
        /* TODO: Uncomment this when the blocks are in place
        else if(block == ModBlocks.CANDELABRA.get())
            rate += 2;
          else if(block == ModBlocks.PENTACLE.get()))
            rate *= 2;*/
        }
        return rate;
    }

    public int getPower(Block block, boolean remove){
        Integer amount = blockAmount.get(block);
        int rm = (remove ? -1 : 1);
        if(amount == null){
            Point[] points = AltarPowererReloadListener.INSTANCE.TAGS.values().toArray(new Point[0]);
            ArrayList<ITag.INamedTag<Block>> tag = new ArrayList<>(AltarPowererReloadListener.INSTANCE.TAGS.keySet());
            int max = 0;
            ITag.INamedTag<Block> maxTag = null;
            boolean flag = false;
            for(int i = 0; i < tag.size(); i++){
                if(block.is(tag.get(i))){
                    if(max < points[i].x ){
                        maxTag = tag.get(i);
                        max = points[i].x;
                        flag = tagsAmount.get(tag.get(i)) >= points[i].y;
                    }
                }
            }
            if(maxTag != null){
                tagsAmount.replace(maxTag, tagsAmount.get(maxTag)+ rm);
                return max * (rm) * (flag ? 0 : 1);
            }
        }
        if(amount == null)
            return 0;

        Point pair = AltarPowererReloadListener.INSTANCE.BLOCKS.get(block);
        blockAmount.replace(block, amount  + (rm));
        return pair.x * rm * (amount >= pair.y  ? 0 : 1);
    }

    public void markDirty(BlockPos blockPos, boolean remove, Block block, List<Block> blocks){
        double dist = Math.sqrt(Math.pow(blockPos.getX() - pos.getX(), 2) + Math.pow(blockPos.getY() - pos.getY(), 2) + Math.pow(blockPos.getZ() - pos.getZ(), 2));
        if( dist <= altarRange){
            altarRange = WitcheryRewitchedConfig.Server.altarRadius.get() * (blocks.contains(ModBlocks.ARTHANA.get()) ? 2 : 1);
            float num = (float)(altarRange / (blocks.contains(ModBlocks.ARTHANA.get()) ? 1 : 2));
            int pow = getPower(block, remove);
            power += pow;
        }
    }

    public CompoundNBT getNBT(){
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("pos", NBTUtil.writeBlockPos(pos));
        nbt.putInt("power", power);
        nbt.putInt("range", altarRange);

        CompoundNBT blockNBT = new CompoundNBT();
        for(Map.Entry<Block, Integer> entry : blockAmount.entrySet()){
            blockNBT.putInt(entry.getKey().getRegistryName().toString(), entry.getValue());
        }
        CompoundNBT tagNBT = new CompoundNBT();
        for(Map.Entry<ITag.INamedTag<Block>, Integer> entry : tagsAmount.entrySet()){
            tagNBT.putInt(entry.getKey().getName().toString(), entry.getValue());
        }
        nbt.put("blockAmount", blockNBT);
        nbt.put("tagsAmount", tagNBT);
        return nbt;
    }

    public static AltarPowerer fromNBT(CompoundNBT nbt, World level){
        if(!nbt.contains("pos"))
            return null;
        BlockPos pos = NBTUtil.readBlockPos((CompoundNBT) nbt.get("pos"));
        int power = nbt.getInt("power");
        int range = nbt.getInt("range");

        CompoundNBT blockNBT = nbt.getCompound("blockAmount");
        CompoundNBT tagNBT = nbt.getCompound("tagsAmount");
        HashMap<Block, Integer> blockAmount = new HashMap<>();
        HashMap<ITag.INamedTag<Block>, Integer> tagsAmount = new HashMap<>();

        for(String key : blockNBT.getAllKeys()){
            blockAmount.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key)), blockNBT.getInt(key));
        }

        for(String key : tagNBT.getAllKeys()){
            tagsAmount.put(BlockTags.createOptional(new ResourceLocation(key)), tagNBT.getInt(key));
        }

        return new AltarPowerer(pos, range, power, blockAmount, tagsAmount);
    }
}
