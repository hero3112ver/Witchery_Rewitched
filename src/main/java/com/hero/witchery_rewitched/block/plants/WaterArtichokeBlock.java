package com.hero.witchery_rewitched.block.plants;

import com.hero.witchery_rewitched.block.plants.ModCropBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.RegistryObject;

public class WaterArtichokeBlock extends ModCropBase {
    public WaterArtichokeBlock(RegistryObject<Item> seed, Block farmland) {
        super(seed, farmland);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        boolean fluidstate = worldIn.getFluidState(pos).getType() == Fluids.WATER;
        boolean fluidstate1 = worldIn.getFluidState(pos.above()).getType() == Fluids.EMPTY;
        boolean ret = (fluidstate ) && fluidstate1 ;
        return ret;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        return mayPlaceOn(state, world, pos);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return null;
    }
}
