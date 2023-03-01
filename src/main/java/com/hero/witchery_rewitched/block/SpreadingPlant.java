package com.hero.witchery_rewitched.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.ArrayList;
import java.util.List;

public class SpreadingPlant extends BushBlock implements BonemealableBlock {
    private final boolean verticalValid;
    private final boolean stoneValid;
    public SpreadingPlant(boolean verticalValid, boolean stoneValid) {
        super(BlockBehaviour
                .Properties
                .of(Material.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP));
        this.verticalValid = verticalValid;
        this.stoneValid = stoneValid;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        if(getValidSpawns(pLevel, pPos).size() >  0)
            return true;
        return false;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if(pRandom.nextInt(10) < 3)
            trySpread(pLevel, pPos, pRandom);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return  (verticalValid && mayPlaceOn(defaultBlockState(), pLevel, pPos.above())
                || mayPlaceOn(defaultBlockState(), pLevel, pPos.below()));
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if ((stoneValid && pLevel.getBlockState(pPos).is(BlockTags.BASE_STONE_OVERWORLD)) ||
                        pLevel.getBlockState(pPos).is(BlockTags.DIRT)) {
            return true;
        }
        return  false;
    }

    private void trySpread(Level pLevel, BlockPos pPos, RandomSource  pRandom){
        List<BlockPos> validSpawns = getValidSpawns(pLevel, pPos);
        System.out.println("Trying to spread");
        if(validSpawns.size() > 0) {
            BlockPos spawn = validSpawns.get(pRandom.nextInt(validSpawns.size()));
            pLevel.setBlock(spawn, defaultBlockState(), 3);
        }
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        trySpread(pLevel, pPos, pRandom);
    }


    private List<BlockPos> getValidSpawns(Level pLevel, BlockPos pos){
        List<BlockPos> validSpawns = new ArrayList<>();
        for(int x  = -1; x <= 1;  x++){
            for(int y  = -1; y <= 1;  y++){
                for(int z  = -1; z <= 1;  z++){
                    BlockPos testPoint = pos.offset(x,y,z);
                    BlockState testBlock = pLevel.getBlockState(testPoint);
                    if(!(testBlock.is(Blocks.AIR) || testBlock.is(Blocks.CAVE_AIR))) continue;

                    if(canSurvive(defaultBlockState(), pLevel, testPoint)){
                        validSpawns.add(testPoint);
                    }
                }
            }
        }
        return validSpawns;
    }


}
