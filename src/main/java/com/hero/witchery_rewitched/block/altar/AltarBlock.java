package com.hero.witchery_rewitched.block.altar;

import com.hero.witchery_rewitched.util.capabilities.altar.AltarLocationCapability;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import com.hero.witchery_rewitched.init.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class AltarBlock extends Block {
    private static final String[][] configs = {
            // Horizontal Positions
            {"xxx", "ozx"},
            {"ozx", "xxx"},
            {"xxx","xzo"},
            {"xzo", "xxx"},

            // Vertical Positions
            {"ox", "zx", "xx"},
            {"xo", "xz", "xx"},
            {"xx", "zx", "ox"},
            {"xx","xz", "xo"},

            // Core Positions
            {"xxx", "xox"},
            {"xox", "xxx"},
            {"xx", "ox", "xx"},
            {"xx", "xo", "xx"}
    };

    public static BooleanProperty MULTIBLOCK_FORMED =BooleanProperty.create("formed");
    public static BooleanProperty CORE = BooleanProperty.create("core");
    private BlockPos coreLoc = null;


    public AltarBlock() {
        super(AbstractBlock.Properties.of(Material.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(MULTIBLOCK_FORMED, false).setValue(CORE, false));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(MULTIBLOCK_FORMED);
        builder.add(CORE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        if(state.getValue(CORE))
            return true;
        return super.hasTileEntity(state);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(state.getValue(MULTIBLOCK_FORMED))
        {
            if (!worldIn.isClientSide && worldIn.getBlockState(findCore(pos, worldIn)).getValue(CORE)) {
                TileEntity tile = worldIn.getBlockEntity(findCore(pos, worldIn));
                if (tile instanceof INamedContainerExtraData && player instanceof ServerPlayerEntity) {
                    INamedContainerExtraData te = (INamedContainerExtraData) tile;
                    NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
                    ((AltarTileEntity)te).queueRecalculation();
                    return ActionResultType.CONSUME;
                }
            }
        }else {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        ifValidActivate(pos, worldIn);
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(newState.getBlock() !=state.getBlock() && state.getValue(MULTIBLOCK_FORMED)){
            deactivateAltar(pos, worldIn);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    private void deactivateAltar(BlockPos pos, World world){
        BlockPos coreLoc = findCore(pos, world);
        if(world.getBlockEntity(coreLoc) != null && world.getBlockEntity(coreLoc) instanceof AltarTileEntity){
            int config = ((AltarTileEntity)world.getBlockEntity(coreLoc)).getConfig();
            int[] loc = findCoreInString(configs[config]);
            for(int i = 0; i < configs[config].length; i++){
                for(int x = 0; x < configs[config][i].length(); x++){
                    BlockPos newPos = coreLoc.offset(i-loc[0], 0, x-loc[1]);
                    if(world.getBlockState(newPos).getBlock() == ModBlocks.ALTAR.get()){
                        world.setBlockAndUpdate(newPos, world.getBlockState(newPos).setValue(MULTIBLOCK_FORMED,false).setValue(CORE, false));
                    }
                }
            }
        }
        for(int i = -2; i < 3; i++){
            for(int x = -2; x < 3; x++){
                BlockPos newPos = coreLoc.offset(i * 16, 0, x * 16);
                world.getChunkAt(newPos).getCapability(AltarLocationCapability.INSTANCE).ifPresent(
                        (source) -> source.deleteAltar(coreLoc)
                );
            }
        }
        world.removeBlockEntity(coreLoc);
    }

    private void ifValidActivate(BlockPos pos, World world){
        for(int z = 0; z < configs.length; z++){
            int[] startPoint = findStartPoint(configs[z]);
            int count = 0;
            for(int i = 0; i < configs[z].length; i++){
                for(int x = 0; x < configs[z][i].length(); x++){
                    if(world.getBlockState(pos.offset(i-startPoint[0], 0,x-startPoint[1])).getBlock() == ModBlocks.ALTAR.get() && !world.getBlockState(pos.offset(i-startPoint[0], 0,x-startPoint[1])).getValue(MULTIBLOCK_FORMED)){
                        count++;
                    }
                    if(count == 6){
                        int confignum;
                        if(z < 4){
                            confignum = z % 2 + 8;
                        }
                        else if(z < 8) confignum = z % 2 + 10;
                        else confignum = z;
                        activateAltar(configs[z], pos, world, confignum);
                        return;
                    }
                }
            }
        }
    }

    private void activateAltar(String[] config, BlockPos pos, World world, int configNum){
        int[] corePos = findCoreInString(config);
        int[] startPoint = findStartPoint(config);
        for(int i = 0; i < config.length; i++){
            for(int x = 0; x < config[i].length(); x++){
                BlockPos pos2 = pos.offset(i-startPoint[0], 0, x-startPoint[1]);
                if(world.getBlockState(pos2).getBlock() == ModBlocks.ALTAR.get()){
                    world.setBlockAndUpdate(pos2, world.getBlockState(pos).setValue(MULTIBLOCK_FORMED, true));
                }
            }
        }
        BlockPos worldCorePos = pos.offset(corePos[0]-startPoint[0], 0, corePos[1]-startPoint[1]);

        if(world.getBlockState(worldCorePos).getBlock() == ModBlocks.ALTAR.get()) {
            world.setBlockAndUpdate(worldCorePos, world.getBlockState(worldCorePos).setValue(CORE, true));
            world.setBlockEntity(worldCorePos, new AltarTileEntity(configNum));
        }
        for(int i = -2; i < 3; i++){
            for(int x = -2; x < 3; x++){
                BlockPos newPos = worldCorePos.offset(i * 16, 0, x * 16);
                world.getChunkAt(newPos).getCapability(AltarLocationCapability.INSTANCE).ifPresent((source) -> source.addAltar(worldCorePos));
            }
        }

    }

    private int[] findCoreInString(String[] config){
        int[] pos = {-1,-1};
        for(int i = 0; i < config.length; i++){
            if (pos[1] == -1)
                pos = new int[]{i, config[i].indexOf('z')};
        }
        if(pos[1] == -1){
            for(int i = 0; i < config.length; i++){
                {
                    if (pos[1] == -1)
                        pos = new int[]{i, config[i].indexOf('o')};
                }
            }
        }
        return pos;
    }

    private int[] findStartPoint(String[] arr){
        for(int i = 0; i < arr.length; i++){
            for(int x = 0; x < arr[i].length(); x++){
                if(arr[i].charAt(x) == 'o'){
                    return new int[]{i, x};
                }
            }
        }
        return new int[]{0,0};
    }


    public static BlockPos findCore(BlockPos pos, World worldIn){
        BlockPos altarCore = null;
        if(worldIn.getBlockEntity(pos) instanceof AltarTileEntity) {altarCore = pos;}
        else if(worldIn.getBlockEntity(pos.offset(1, 0 ,0)) instanceof AltarTileEntity){altarCore = pos.offset(1,0,0);}
        else if(worldIn.getBlockEntity(pos.offset(0, 0 ,1)) instanceof AltarTileEntity){altarCore = pos.offset(0,0,1);}
        else if(worldIn.getBlockEntity(pos.offset(0, 0 ,-1)) instanceof AltarTileEntity){altarCore = pos.offset(0,0,-1);}
        else if(worldIn.getBlockEntity(pos.offset(-1, 0 ,0)) instanceof AltarTileEntity){altarCore = pos.offset(-1,0,0);}
        else if(worldIn.getBlockEntity(pos.offset(1, 0 ,1)) instanceof AltarTileEntity){altarCore = pos.offset(1,0,1);}
        else if(worldIn.getBlockEntity(pos.offset(-1, 0 ,1)) instanceof AltarTileEntity){altarCore = pos.offset(-1,0,1);}
        else if(worldIn.getBlockEntity(pos.offset(1, 0 ,-1)) instanceof AltarTileEntity){altarCore = pos.offset(1,0,-1);}
        else if(worldIn.getBlockEntity(pos.offset(-1, 0 ,-1)) instanceof AltarTileEntity){altarCore = pos.offset(-1,0,-1);}
        return altarCore;
    }
}
