package com.hero.witchery_rewitched.block.critter_snare;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class CritterSnareBlock extends Block{
    public static EnumProperty<CritterEnum> HAS_ENTITY = EnumProperty.create("has_entity", CritterEnum.class);

    public CritterSnareBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_ENTITY, CritterEnum.NONE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CritterSnareTileEntity();
    }

    private boolean hasEntity(BlockState state){
        return state.getValue(HAS_ENTITY) != CritterEnum.NONE;
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        if (pLevel.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if(pPlayer.isCrouching() && hasEntity(pState)){
            if(pLevel.getBlockEntity(pPos) instanceof CritterSnareTileEntity){
                CritterSnareTileEntity te = (CritterSnareTileEntity) pLevel.getBlockEntity(pPos);
                te.releaseEntity();
                pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_ENTITY, CritterEnum.NONE));
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void entityInside(BlockState pState, World pLevel, BlockPos pPos, Entity pEntity) {
        CritterSnareTileEntity te = null;
        if(pLevel.getBlockEntity(pPos) instanceof CritterSnareTileEntity){
            te = (CritterSnareTileEntity) pLevel.getBlockEntity(pPos);
        }
        if(te != null && (te.lastReleaseTime == -1 || pLevel.getDayTime() - te.lastReleaseTime > te.CAPTURE_CD)) {
            if (!hasEntity(pState) && pEntity instanceof BatEntity ) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_ENTITY, CritterEnum.BAT));
                te.putEntity(pEntity);
                pEntity.remove();
            } else if (!hasEntity(pState) && pEntity instanceof SlimeEntity && ((SlimeEntity) pEntity).isTiny()) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_ENTITY, CritterEnum.SLIME));
                te.putEntity(pEntity);
                pEntity.remove();
            } else if (!hasEntity(pState) && pEntity instanceof SilverfishEntity) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_ENTITY, CritterEnum.SILVERFISH));
                te.putEntity(pEntity);
                pEntity.remove();
            }
        }

        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_ENTITY);
    }


}
