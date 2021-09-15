package com.hero.witchery_rewitched.block.plants;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelfSpreadingPlantBase extends BushBlock implements IGrowable {
    final boolean CAN_GROW_ON_CEILING;

    public SelfSpreadingPlantBase(boolean plantOnCeiling, boolean emitsLight){
        super(AbstractBlock.Properties.of(Material.PLANT)
                .noOcclusion()
                .noCollission()
                .sound(SoundType.CROP)
                .noCollission()
                .strength(0)
                .randomTicks()
                .lightLevel( (state) -> emitsLight ? 14 : 0)
        );
        CAN_GROW_ON_CEILING = plantOnCeiling;
    }
    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        if(isClient)
            return true;

        for(int x = -1; x < 2; x++){
            for(int y = -1; y < 2; y++){
                for(int z = -1; z < 2; z++){
                    if(worldIn.getBlockState(pos.offset(x, y, z)).getBlock() == Blocks.AIR)
                        return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (isValidBonemealTarget(worldIn, pos, state, worldIn.isClientSide) && worldIn.getRandom().nextInt(10) == 0) {
            if (!worldIn.isAreaLoaded(pos, 1)) return;
            this.performBonemeal(worldIn, random, pos, state);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        if(CAN_GROW_ON_CEILING && state.getBlock() != Blocks.AIR)
            return true;
        return super.mayPlaceOn(state, worldIn, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        if(CAN_GROW_ON_CEILING){
            BlockPos blockpos = pos.above();
            if(this.mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos))
                return true;
        }
        return super.canSurvive(state, worldIn, pos);
    }

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return isValidBonemealTarget(worldIn, pos, state, false);
    }



    @Override
    public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        List<BlockPos> blockPos = getPossiblePlantSpaces(worldIn, pos);
        if(blockPos.size() > 0)
            worldIn.setBlockAndUpdate(blockPos.get(rand.nextInt(blockPos.size())), this.defaultBlockState());
    }

    private List<BlockPos> getPossiblePlantSpaces(World world, BlockPos pos){
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for(int x = -1; x < 2; x++){
            for(int y = -1; y < 2; y++){
                for(int z = -1; z < 2; z++){
                    if(world.getBlockState(pos.offset(x, y, z)).getBlock() == Blocks.AIR) {
                        Block above = world.getBlockState(pos.offset(x, y+1, z)).getBlock();
                        Block below = world.getBlockState(pos.offset(x, y-1, z)).getBlock();
                        if(CAN_GROW_ON_CEILING && (above.is(BlockTags.BASE_STONE_OVERWORLD) || above == Blocks.DIRT || above == Blocks.GRASS_BLOCK || below.is(BlockTags.BASE_STONE_OVERWORLD))){
                            blocks.add(pos.offset(x, y, z));
                        }
                        else if(below == Blocks.DIRT || below == Blocks.GRASS_BLOCK){
                            blocks.add(pos.offset(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }


}
