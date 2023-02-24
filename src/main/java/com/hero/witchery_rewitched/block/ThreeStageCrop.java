package com.hero.witchery_rewitched.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

public class ThreeStageCrop  extends CropBlock {
    public static final int MAX_AGE = 2;
    private final RegistryObject<Item> seed;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};
    private Block placeableBlock;
    public ThreeStageCrop(RegistryObject<Item> seeds) {
        super(BlockBehaviour
                .Properties
                .of(Material.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
        );
        seed = seeds;
        placeableBlock = Blocks.AIR;
    }
    public ThreeStageCrop(RegistryObject<Item> seeds, Block growthBlock) {
        super(BlockBehaviour
                .Properties
                .of(Material.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
        );
        seed = seeds;
        placeableBlock = growthBlock;
    }
    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }
    @Override
    protected ItemLike getBaseSeedId() {
        return seed.get();
    }
    @Override
    public void randomTick(BlockState p_220778_, ServerLevel p_220779_, BlockPos p_220780_, RandomSource p_220781_) {
        if (p_220781_.nextInt(3) != 0) {
            super.randomTick(p_220778_, p_220779_, p_220780_, p_220781_);
        }

    }


    @Override
    protected int getBonemealAgeIncrease(Level p_49663_) {
        return super.getBonemealAgeIncrease(p_49663_) / 3;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49665_) {
        p_49665_.add(AGE);
    }
    @Override
    public VoxelShape getShape(BlockState p_49672_, BlockGetter p_49673_, BlockPos p_49674_, CollisionContext p_49675_) {
        return SHAPE_BY_AGE[p_49672_.getValue(this.getAgeProperty())];
    }

    @Override
    public boolean canSurvive(BlockState p_52282_, LevelReader p_52283_, BlockPos p_52284_) {
        return super.canSurvive(p_52282_, p_52283_, p_52284_);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        if(placeableBlock.defaultBlockState().is(Blocks.WATER)) {
            Fluid fluidstate = state.getFluidState().getType();
            Fluid fluidstate1 = getter.getBlockState(pos.above()).getFluidState().getType();
            boolean ret = (fluidstate == Fluids.WATER) && (fluidstate1 == Fluids.EMPTY);
            return ret;
        }
        return super.mayPlaceOn(state, getter, pos);
    }
}