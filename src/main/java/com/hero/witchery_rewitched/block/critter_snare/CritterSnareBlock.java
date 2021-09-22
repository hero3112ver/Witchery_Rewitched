package com.hero.witchery_rewitched.block.critter_snare;


import com.hero.witchery_rewitched.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
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
    public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        TileEntity tileentity = pLevel.getBlockEntity(pPos);
        if (!pLevel.isClientSide &&tileentity instanceof CritterSnareTileEntity && pStack.hasTag() && pStack.getTag().contains("BlockEntityTag")) {
            CompoundNBT nbt = pStack.getTagElement("BlockEntityTag");
            ((CritterSnareTileEntity)tileentity).loadData(nbt);
            String id = ((CompoundNBT)nbt.get("entityData")).getString("id");
            CritterEnum critter = CritterEnum.NONE;
            if(id.contains("bat"))
                critter = CritterEnum.BAT;
            else if(id.contains("silver"))
                critter = CritterEnum.SILVERFISH;
            else if(id.contains("slime"))
                critter = CritterEnum.SLIME;
            pLevel.setBlockAndUpdate(pPos, pState.setValue(CritterSnareBlock.HAS_ENTITY, critter));
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public void playerWillDestroy(World pLevel, BlockPos pPos, BlockState pState, PlayerEntity pPlayer) {
        TileEntity tileEntity = pLevel.getBlockEntity(pPos);
        if (tileEntity instanceof CritterSnareTileEntity && !pLevel.isClientSide) {
            ItemStack stack = new ItemStack(ModBlocks.CRITTER_SNARE.get());
            CritterSnareTileEntity te = (CritterSnareTileEntity) tileEntity;
            CompoundNBT nbt = te.saveData(new CompoundNBT());

            if(!nbt.isEmpty()){
                stack.addTagElement("BlockEntityTag", nbt);
            }
            ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, stack);
            itementity.setDefaultPickUpDelay();
            pLevel.addFreshEntity(itementity);
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_ENTITY);
    }


}
