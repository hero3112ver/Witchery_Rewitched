package com.hero.witchery_rewitched.item;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.api.rituals.AbstractRitual;
import com.hero.witchery_rewitched.api.util.PlayerUtils;
import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CircleTalisman extends Item {
    public CircleTalisman(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public ActionResultType useOn(ItemUseContext pContext) {
        if(pContext.getClickedFace() != Direction.UP || pContext.getLevel().isClientSide)
            return ActionResultType.SUCCESS;
        BlockPos pos = pContext.getClickedPos().offset(0,1,0);
        ItemStack stack = pContext.getPlayer().getItemInHand(pContext.getHand());
        World world = pContext.getLevel();
        if(stack.hasTag()){
            String nbt = stack.getTag().getString("circles");
            if(tryPlace(nbt, world, pos, true) ){
                tryPlace(nbt, world, pos, false);
                ItemStack stack2 = pContext.getPlayer().getItemInHand(pContext.getHand()).copy();
                stack2.shrink(1);
                pContext.getPlayer().setItemInHand(pContext.getHand(), stack2);
                PlayerUtils.giveItem(pContext.getPlayer(), new ItemStack(ModItems.CIRCLE_TALISMAN.get()));
                return ActionResultType.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }

    private boolean tryPlace(String nbt, World world, BlockPos pos, boolean simulate){
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};
        for(int z = 0; z  < nbt.length(); z++){
            if(nbt.charAt(z) != 'x')
            {
                String[] circle = AbstractRitual.circleShapes.get(z+1);
                for(int i = 0; i < circle[0].length(); i++){
                    for(int x = 0; x < circle[0].length(); x++){
                        int posX = i - circle[0].length()/2;
                        int posZ = x - circle[0].length()/2;
                        Block blockAt = world.getBlockState(pos.offset(posX,0,posZ)).getBlock();
                        if(circle[i].charAt(x) == '0' ){
                            if(simulate){
                                if(blockAt != Blocks.AIR && !world.getBlockState(pos.offset(posX,-1,posZ)).isFaceSturdy(world,pos.offset(posX, -1, posZ), Direction.UP)) return false;
                            }else{
                                if(nbt.charAt(z) == 'r')
                                    world.setBlockAndUpdate(pos.offset(posX, 0, posZ), ModBlocks.RITUAL_GLYPH.get().defaultBlockState().setValue(GlyphBlock.VARIANT,world.random.nextInt(9)).setValue(GlyphBlock.DIRECTION,dirs[world.random.nextInt(4)]));
                                else if(nbt.charAt(z) == 'i')
                                    world.setBlockAndUpdate(pos.offset(posX, 0, posZ), ModBlocks.INFERNAL_GLYPH.get().defaultBlockState().setValue(GlyphBlock.VARIANT,world.random.nextInt(9)).setValue(GlyphBlock.DIRECTION,dirs[world.random.nextInt(4)]));
                                else if(nbt.charAt(z) == 'o')
                                    world.setBlockAndUpdate(pos.offset(posX, 0, posZ), ModBlocks.OTHERWHERE_GLYPH.get().defaultBlockState().setValue(GlyphBlock.VARIANT,world.random.nextInt(9)).setValue(GlyphBlock.DIRECTION,dirs[world.random.nextInt(4)]));
                                world.setBlockAndUpdate(pos, ModBlocks.GOLD_GLYPH.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
