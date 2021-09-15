package com.hero.witchery_rewitched.block.plants;

import com.hero.witchery_rewitched.entity.mandrake.MandrakeEntity;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class MandrakeBlock extends ModCropBase{
    public MandrakeBlock() {
        super(ModItems.MANDRAKE_SEEDS, Blocks.FARMLAND);
    }

    @SubscribeEvent
    public static void onBroken(BlockEvent.BreakEvent event){
        IWorld worldIn = event.getWorld();
        BlockPos pos = event.getPos();

        if(worldIn.getBlockState(pos) == ModBlocks.MANDRAKE.get().defaultBlockState().setValue(AGE, EnumPlantAge.AGE2) && worldIn.isAreaLoaded(pos, 1)){
            int chance = 10;
            if(worldIn.getLevelData().getDayTime() > 13000 && worldIn.getLevelData().getDayTime() < 23000)
                chance = 1;
            if(worldIn.getRandom().nextInt(20) < chance){
                worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
                Entity mandrake = new MandrakeEntity((FMLPlayMessages.SpawnEntity)null , (World)worldIn);
                mandrake.setPos(pos.getX(), pos.getY(), pos.getZ());
                worldIn.addFreshEntity(mandrake);
                return;
            }

        }
    }

    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {


        super.onRemove(state, worldIn, pos, newState, isMoving);
    }
}
