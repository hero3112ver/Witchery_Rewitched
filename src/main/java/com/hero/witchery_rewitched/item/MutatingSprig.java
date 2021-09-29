package com.hero.witchery_rewitched.item;

import com.hero.witchery_rewitched.block.critter_snare.CritterEnum;
import com.hero.witchery_rewitched.block.critter_snare.CritterSnareBlock;
import com.hero.witchery_rewitched.block.critter_snare.CritterSnareTileEntity;
import com.hero.witchery_rewitched.block.plants.grassper.GrassperTileEntity;
import com.hero.witchery_rewitched.entity.toad.ToadEntity;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MutatingSprig extends Item {
    public MutatingSprig(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public ActionResultType useOn(ItemUseContext pContext) {

        int[][] offsets = {{-1,-1},{-1, 1},{1, -1},{1, 1}, {0,1}, {0,-1}, {1,0}, {-1,0}};

        BlockPos pos = pContext.getClickedPos();
        World world = pContext.getLevel();
        VoxelShape shape = VoxelShapes.box(pos.getX(), pos.getY()+1, pos.getZ(), pos.getX()+1, pos.getY()-1, pos.getZ()+1);
        List<Entity> entities = world.getEntities((Entity) null, shape.bounds(), (entity -> entity instanceof LivingEntity));
        ArrayList<BlockPos> snared = new ArrayList<>();
        ArrayList<BlockPos> items = new ArrayList<>();
        Block block = world.getBlockState(pos).getBlock();



        for(int i = 0; i < 8; i++){
            BlockPos newPos = pos.offset(offsets[i][0], 0, offsets[i][1]);
            BlockState state = world.getBlockState(newPos);
            if(state.getBlock() == ModBlocks.GRASSPER.get()){
                TileEntity te = world.getBlockEntity(pos.offset(offsets[i][0], 0, offsets[i][1]));
                if(te instanceof GrassperTileEntity){
                    items.add(newPos);
                }
            }
            if(state.getBlock() == ModBlocks.CRITTER_SNARE.get()){
                if(CritterSnareBlock.getEntity(state) != CritterEnum.NONE){
                    snared.add(newPos);
                }
            }
        }

        // This is hardcoded, and I really don't care right now
        if(snared.size() > 1){
            if(tryToad(block, entities, snared, items, world)){
                for(BlockPos grassperPos : items){
                    ((IInventory)world.getBlockEntity(grassperPos)).removeItem(0,1);
                }
                for(BlockPos critterPos : snared){
                    if(world.getBlockState(critterPos).getValue(CritterSnareBlock.HAS_ENTITY) == CritterEnum.SLIME){
                        ((CritterSnareTileEntity)world.getBlockEntity(critterPos)).releaseEntity(false);
                        world.setBlockAndUpdate(critterPos, ModBlocks.CRITTER_SNARE.get().defaultBlockState());
                        ToadEntity toad = new ToadEntity(world);
                        toad.setPos(critterPos.getX() + .5, critterPos.getY(), critterPos.getZ() + .5);
                        world.addFreshEntity(toad);
                    }
                }
                entities.stream().map(entity -> entity instanceof CatEntity || entity instanceof OcelotEntity ? entity : null).collect(Collectors.toList()).get(0).remove();
            }
        }
        return super.useOn(pContext);
    }

    private boolean tryToad(Block block, List<Entity> entities, List<BlockPos> snaredPos, List<BlockPos> itemPos, World world){
        if(block != Blocks.COBWEB)
            return false;

        List<CritterEnum> snared = snaredPos.stream().map( pos -> CritterSnareBlock.getEntity(world.getBlockState(pos))).collect(Collectors.toList());
        List<Item> items = itemPos.stream().map(pos -> ((GrassperTileEntity)world.getBlockEntity(pos)).getItem(0).getItem()).collect(Collectors.toList());

        int count = 0;
        for(CritterEnum critter : snared){
            if(critter == CritterEnum.SLIME) count++;
        }

        if(count < 2) return false;

        boolean flag = false;
        for(Entity entity : entities){
            if (entity instanceof OcelotEntity || entity instanceof CatEntity) {
                flag = true;
                break;
            }
        }

        if(!flag) return false;

        count = 0;

        if(items.contains(ModItems.CHARGED_ATTUNED_STONE.get())){
            for(Item item: items){
                if(item == ModItems.MUTANDIS_EXTREMIS.get()) count++;
            }
        }

        return count >= 3;
    }
}
