package com.hero.witchery_rewitched.util.rituals;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class RiteOfBindingCircleTalisman extends AbstractRitual{
    public RiteOfBindingCircleTalisman(BlockPos pos, World world, UUID caster) {
        super(pos, world, caster, null,
                new ArrayList<Pair<Integer, GlyphBlock>>() {},
                new ArrayList<>(),
                0,
                0,
                true
        );
    }

    public RiteOfBindingCircleTalisman() {this(null, null, null);}

    @Override
    public AbstractRitual createRite(BlockPos pos, World world, UUID caster, boolean stone) {
        return new RiteOfBindingCircleTalisman(pos, world, caster);
    }

    @Override
    public boolean checkStartConditions(List<ItemStack> items) {
        for(String[] circle : circleShapes.values()){
            Block blockType = null;
            boolean failRing = false;
            for(int i = 0; i < circle[0].length(); i++){
                for(int x = 0; x < circle[0].length(); x++){
                    if(failRing)
                        break;
                    int posX = i - circle[0].length()/2;
                    int posZ = x - circle[0].length()/2;
                    Block blockAt = world.getBlockState(pos.offset(posX,0,posZ)).getBlock();
                    if(circle[i].charAt(x) == '0' && blockAt instanceof GlyphBlock && blockAt != ModBlocks.GOLD_GLYPH.get()){
                        if(blockType == null)
                            blockType = blockAt;
                    }
                    else if(circle[i].charAt(x) == '0' && blockAt != ModBlocks.GOLD_GLYPH.get())
                    {
                        blockType = null;
                        failRing = true;
                        break;
                    }
                }
            }
            if(!failRing) return true;
        }
        return false;
    }

    @Override
    public void start(ArrayList<ItemStack> items) {
        StringBuilder nbt = new StringBuilder();
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};

        for(String[] circle : circleShapes.values()){
            Block blockType = null;
            boolean failRing = false;
            ArrayList<BlockPos> pendingChanges = new ArrayList<>();
            for(int i = 0; i < circle[0].length(); i++){
                for(int x = 0; x < circle[0].length(); x++){
                    if(failRing)
                        break;
                    int posX = i - circle[0].length()/2;
                    int posZ = x - circle[0].length()/2;
                    Block blockAt = world.getBlockState(pos.offset(posX,0,posZ)).getBlock();
                    if(circle[i].charAt(x) == '0' && blockAt instanceof GlyphBlock && blockAt != ModBlocks.GOLD_GLYPH.get()){
                        if(blockType == null)
                            blockType = blockAt;
                        pendingChanges.add(pos.offset(posX, 0 , posZ));
                        world.setBlockAndUpdate(pos.offset(posX,0,posZ), Blocks.AIR.defaultBlockState());
                    }
                    else if(circle[i].charAt(x) == '0' && blockAt != ModBlocks.GOLD_GLYPH.get())
                    {
                        for(int z = 0; z < pendingChanges.size(); z++){
                            world.setBlockAndUpdate(pendingChanges.get(z), blockType.defaultBlockState().setValue(GlyphBlock.VARIANT,world.random.nextInt(9)).setValue(GlyphBlock.DIRECTION,dirs[world.random.nextInt(4)]));
                        }
                        blockType = null;
                        failRing = true;
                        break;
                    }
                }
            }
            if(blockType == null)
                nbt.append('x');
            else if(blockType == ModBlocks.RITUAL_GLYPH.get())
                nbt.append('r');
            else if(blockType == ModBlocks.OTHERWHERE_GLYPH.get())
                nbt.append('o');
            else if(blockType == ModBlocks.INFERNAL_GLYPH.get())
                nbt.append('i');
        }
        ItemStack stack = new ItemStack(ModItems.CIRCLE_TALISMAN.get());
        stack.getOrCreateTag().putString("circles",nbt.toString());
        InventoryHelper.dropItemStack(world,pos.getX(), pos.getY(), pos.getZ(), stack);
        active = false;
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        world.removeBlockEntity(pos);
        super.start(items);
    }

    @Override
    public String getName() {
        return "Rite of Binding(Circle)";
    }

    @Override
    public String getDescription() {
        return "Binds the currently placed circles to the talisman, consuming the circles and gold glyph.";
    }

    @Override
    public String getRequirements() {
        return "Available circle to place into the talisman.";
    }
}
