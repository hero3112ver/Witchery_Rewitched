package com.hero.witchery_rewitched.block.plants;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EmberMossBlock extends SelfSpreadingPlantBase {

    public EmberMossBlock() {
        super(false, false);
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        entityIn.setSecondsOnFire(3);
        super.entityInside(state, worldIn, pos, entityIn);
    }
}
