package com.hero.witchery_rewitched.block.poppet_shelf;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.util.util.PlayerUtils;
import com.hero.witchery_rewitched.util.capabilities.poppet_shelf.PoppetShelfCapability;
import com.hero.witchery_rewitched.util.capabilities.poppet_worlds.PoppetWorldCapability;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import com.hero.witchery_rewitched.init.ModContainerBlock;
import com.hero.witchery_rewitched.item.PoppetBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;


public class PoppetShelfBlock extends ModContainerBlock<PoppetShelfTileEntity> {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public PoppetShelfBlock(BiFunction<BlockState, IBlockReader, ? extends PoppetShelfTileEntity> tileFactory, Properties properties) {
        super(tileFactory, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof INamedContainerExtraData){
             PoppetShelfTileEntity pste = (PoppetShelfTileEntity) te;
            if(player.isCrouching()) {
                ItemStack stack = pste.extract();
                if(stack != ItemStack.EMPTY) {
                    PlayerUtils.giveItem(player, stack);
                    if (!pste.contains(player)) {
                        player.getCapability(PoppetWorldCapability.INSTANCE).ifPresent((source) ->
                                source.removeShelfIfPresent(worldIn)
                        );
                    }
                }
            }
            else if(player.getItemInHand(handIn).getItem() instanceof PoppetBase){
                pste.insert(player.getItemInHand(handIn));
                player.setItemInHand(handIn, ItemStack.EMPTY);
                player.getCapability(PoppetWorldCapability.INSTANCE).ifPresent(source -> source.addShelfIfNotPresent(worldIn));
            }
        }
        return ActionResultType.CONSUME;

    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!worldIn.isClientSide && placer instanceof PlayerEntity){
            ForgeChunkManager.forceChunk((ServerWorld) worldIn, WitcheryRewitched.MODID, pos, worldIn.getChunk(pos).getPos().x, worldIn.getChunk(pos).getPos().z, true, false);
            PlayerEntity player = (PlayerEntity)placer;
            worldIn.getCapability(PoppetShelfCapability.INSTANCE).ifPresent((source) -> source.addShelfIfNotPresent(player, pos));
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PoppetShelfTileEntity();
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(worldIn instanceof ServerWorld && !state.getBlock().is(newState.getBlock())){
            ForgeChunkManager.forceChunk((ServerWorld) worldIn, WitcheryRewitched.MODID, pos, worldIn.getChunk(pos).getPos().x, worldIn.getChunk(pos).getPos().z, false, false);
            worldIn.getCapability(PoppetShelfCapability.INSTANCE).ifPresent((source) -> source.removeShelfIfPresent(pos));
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);

    }
}
