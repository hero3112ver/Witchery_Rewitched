package com.hero.witchery_rewitched.block.altar;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.api.capabilities.altar.AltarLocationCapability;
import com.hero.witchery_rewitched.api.capabilities.altar.IAltarLocations;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModTags;
import com.hero.witchery_rewitched.init.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class AltarTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerExtraData {

    private int rechargeRate;
    private int maxEnergy;
    private int energy;
    private int config;
    public int recalculateTimer = 0;

    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index){
                case 0:
                    return rechargeRate;
                case 1:
                    return maxEnergy;
                case 2:
                    return energy;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch(index){
                case 0:
                    rechargeRate = value;
                    break;
                case 1:
                    maxEnergy = value;
                    break;
                case 2:
                    energy = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };


    public AltarTileEntity(int config) {
        super(ModTileEntities.ALTAR.get());
        this.maxEnergy = 0;
        this.rechargeRate = 1;
        this.energy = 0;
        this.config = config;
    }

    public int getConfig(){return config;}

    public void queueRecalculation(){
        if(recalculateTimer == -1)
            recalculateTimer = 20;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (!level.getBlockState(worldPosition).getValue(AltarBlock.MULTIBLOCK_FORMED))
            return;

        if (energy < maxEnergy)
            energy += rechargeRate;
        else if (energy > maxEnergy)
            energy = maxEnergy;

        List<Block> blocks = getBlocksAboveAltar();
        if (recalculateTimer == 0) {
            calculateMaxEnergy(blocks);
            calculateRechargeRate(blocks);
        }
        if(recalculateTimer >= 0) recalculateTimer--;
    }

    public boolean takePower(int amount){
        if(amount <= energy) {
            energy-= amount;
            return true;
        }
        return false;
    }

    static {
        AltarPowerer.add(Blocks.DRAGON_EGG,250, 1);
        //AltarPowerer.add(Blocks.DEMON_HEART.get(), 40, 2);
        AltarPowerer.add(ModBlocks.SPANISH_MOSS.get(), 3, 20);
        AltarPowerer.add(BlockTags.FLOWERS ,4, 30);
        AltarPowerer.add(Blocks.WHEAT,4, 20);
        AltarPowerer.add(Blocks.PUMPKIN,4, 20);
        AltarPowerer.add(Blocks.SEA_PICKLE, 4,20);
        AltarPowerer.add(Blocks.MELON,4, 20);
        AltarPowerer.add(Blocks.CARROTS, 4, 20);
        AltarPowerer.add(Blocks.POTATOES, 4, 20);
        AltarPowerer.add(ModBlocks.BELLADONNA.get(), 4, 20);
        AltarPowerer.add(ModBlocks.MANDRAKE.get(), 4, 20);
        AltarPowerer.add(ModBlocks.WATER_ARTICHOKE.get(), 4, 20);
        AltarPowerer.add(ModBlocks.SNOWBELL.get(), 4, 20);
        AltarPowerer.add(ModBlocks.EMBER_MOSS.get(), 4, 20);
        AltarPowerer.add(ModTags.Blocks.WITCH_LEAVES ,4, 50);
        AltarPowerer.add(ModTags.Blocks.WITCH_LOG, 3, 100);
        AltarPowerer.add(BlockTags.SAPLINGS, 4, 20);
        AltarPowerer.add(BlockTags.LOGS,4, 20);
        AltarPowerer.add(BlockTags.LEAVES,3, 100);
        AltarPowerer.add(Blocks.TALL_GRASS,3, 50);
        AltarPowerer.add(Blocks.RED_MUSHROOM, 3, 20);
        AltarPowerer.add(Blocks.BROWN_MUSHROOM, 3, 20);
        AltarPowerer.add(Blocks.CRIMSON_FUNGUS, 3,20);
        AltarPowerer.add(Blocks.WARPED_FUNGUS, 3,20);
        AltarPowerer.add(Blocks.CACTUS, 3, 50);
        AltarPowerer.add(Blocks.BAMBOO, 3, 50);
        AltarPowerer.add(BlockTags.CORAL_BLOCKS, 3, 20);
        AltarPowerer.add(BlockTags.CORALS, 3, 20);
        AltarPowerer.add(Blocks.SUGAR_CANE,3, 50);
        AltarPowerer.add(Blocks.PUMPKIN_STEM, 3, 20);
        AltarPowerer.add(Blocks.RED_MUSHROOM_BLOCK, 3, 20);
        AltarPowerer.add(Blocks.BROWN_MUSHROOM_BLOCK, 3, 20);
        AltarPowerer.add(Blocks.MELON_STEM, 3, 20);
        AltarPowerer.add(Blocks.COCOA, 3, 20);
        //AltarPowerer.add(ModBLocks.WISPY_COTTON.get(),  3, 20);
        AltarPowerer.add(BlockTags.LOGS, 2, 50);
        AltarPowerer.add(Blocks.GRASS_BLOCK, 2, 80);
        AltarPowerer.add(Blocks.SEAGRASS, 2, 80);
        AltarPowerer.add(Blocks.CRIMSON_FUNGUS,2, 80);
        AltarPowerer.add(Blocks.WARPED_FUNGUS,2, 80);
        AltarPowerer.add(Blocks.VINE, 2, 50);
        AltarPowerer.add(Blocks.WEEPING_VINES, 2, 50);
        AltarPowerer.add(Blocks.TWISTING_VINES, 2, 50);
        AltarPowerer.add(ModBlocks.GLINTWEED.get(), 2, 20);
        //AltarPowerer.add(ModBlocks.CRITTER_SNARE.get(), 2, 10);
        //AltarPowerer.add(ModBlocks.GRASSPER.get(), 2, 10);
        //AltarPowerer.add(ModBlocks.BLOOD_POPPY.get(), 2,10);
        AltarPowerer.add(Blocks.DIRT, 1, 80);
        AltarPowerer.add(Blocks.FARMLAND, 1, 100);
        AltarPowerer.add(Blocks.WATER, 1, 50);
        AltarPowerer.add(Blocks.MYCELIUM, 1, 80);
    }

    private void calculateMaxEnergy(List<Block> blocks){
        if(level == null)
            return;

        int rate = 1;
        int range = WitcheryRewitchedConfig.Server.altarRadius.get();
        int power = 0;
        AltarPowerer powerer = new AltarPowerer();
        if(blocks.contains(Blocks.SKELETON_SKULL) && blocks.contains(Blocks.WITHER_SKELETON_SKULL))
            blocks.remove(Blocks.SKELETON_SKULL);
        /*if(blocks.contains(ModBlocks.FILLED_CHALICE.get()) & blocks.contains(ModBlocks.CHALICE.get()))
        blocks.remove(ModBlocks.CHALICE.get());*/
        for(Block block : blocks) {
            if (block == Blocks.SKELETON_SKULL)
                rate += 1;
            else if (block == Blocks.WITHER_SKELETON_SKULL)
                rate += 2;
            /* TODO: Uncomment this when the blocks are in place and maybe player heads https://ftb.fandom.com/wiki/Altar_(Witchery)
            else if(blocks == ModBlocks.CHALICE.get())
                rate += 2;
              else if(blocks == ModBlocks.FILLED_CHALICE.get())
                rate += 2;*/
            else if(blocks == ModBlocks.ARTHANA.get())
                range *= 2;

        }
        for(int x = -range; x < range; x++){
            for(int y = -range; y < range; y++){
                for(int z = -range; z < range; z++){
                    Block block = level.getBlockState(getBlockPos().offset(x, y, z)).getBlock();
                    if(block == Blocks.AIR || new Vector3d(worldPosition.offset(x, y, z).getX(), worldPosition.offset(x, y, z).getY(), worldPosition.offset(x, y, z).getZ()).length() <= range)
                        continue;
                    power += powerer.getPower(block);
                }
            }
        }

        this.maxEnergy = power * rate;
    }

    private void calculateRechargeRate(List<Block> blocks){
        int rate = 1;
        if(blocks.contains(Blocks.SKELETON_SKULL) && blocks.contains(Blocks.WITHER_SKELETON_SKULL))
            blocks.remove(Blocks.SKELETON_SKULL);
        for(Block block : blocks) {
            if (block == Blocks.SKELETON_SKULL)
                rate += 1;
            else if (block == Blocks.WITHER_SKELETON_SKULL)
                rate += 2;
            else if (block == Blocks.TORCH)
                rate += 1;
        /* TODO: Uncomment this when the blocks are in place
        else if(block == ModBlocks.CANDELABRA.get())
            rate += 2;
          else if(block == ModBlocks.PENTACLE.get()))
            rate *= 2;*/
        }
        this.rechargeRate = rate;
    }

    public static BlockPos findFirstAltar(BlockPos pos, World world) {
        LazyOptional<IAltarLocations> altarLocations = world.getChunkAt(pos).getCapability(AltarLocationCapability.INSTANCE);
        if(!altarLocations.isPresent())
            return null;

        for(BlockPos altar2 : altarLocations.orElse(new AltarLocationCapability()).getAltars() ) {
            TileEntity te = world.getBlockEntity(altar2);
            if (te instanceof AltarTileEntity)
                if (((AltarTileEntity) te).isInRange(pos)) {
                    return altar2;
                }
        }

        return null;
    }

    public List<Block> getBlocksAboveAltar(){
        ArrayList<Block> blocks = new ArrayList<>();
        if(level == null)
            return blocks;

        for(int x = -1; x < 2; x++){
            for(int z = -1; z < 2; z++){
                if(level.getBlockState(worldPosition.offset(x, 0, z)).getBlock() != ModBlocks.ALTAR.get())continue;
                blocks.add(level.getBlockState(worldPosition.offset(x, 1, z)).getBlock());
            }
        }
        return blocks;
    }

    public boolean isInRange(BlockPos pos){
        if(pos.closerThan(this.worldPosition, WitcheryRewitchedConfig.Server.altarRadius.get()))
            return true;
        else return pos.closerThan(this.worldPosition, WitcheryRewitchedConfig.Server.altarRadius.get() * 2) && getBlocksAboveAltar().contains(ModBlocks.ARTHANA.get());
    }

    @Override
    public void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(fields.getCount());
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.witchery_rewitched.altar");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return AltarContainer.createAltar(id, fields);
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        rechargeRate = nbt.getInt("rechargerate");
        maxEnergy = nbt.getInt("maxenergy");
        energy = nbt.getInt("energy");
        config = nbt.getInt("config");
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        compound.putInt("rechargerate", rechargeRate);
        compound.putInt("maxenergy", maxEnergy);
        compound.putInt("energy", energy);
        compound.putInt("config", config);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        CompoundNBT tags = packet.getTag();
        rechargeRate = tags.getInt("rechargerate");
        maxEnergy = tags.getInt("maxenergy");
        energy = tags.getInt("energy");
        config = tags.getInt("config");
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags= super.getUpdateTag();
        tags.putInt("rechargerate", rechargeRate);
        tags.putInt("maxenergy", maxEnergy);
        tags.putInt("energy", energy);
        tags.putInt("config", config);
        return tags;
    }

    public static class AltarPowerer{
        static HashMap<Block, Point> blocks = new HashMap<>();
        HashMap<Block, Integer> blockAmount;
        static HashMap<ITag.INamedTag<Block>, Point> tags = new HashMap<>();
        HashMap<ITag.INamedTag<Block>, Integer> tagsAmount;
        public AltarPowerer(){
            blockAmount = new HashMap<>();
            for(Block block : blocks.keySet())
                blockAmount.put(block, 0);
            tagsAmount = new HashMap<>();
            for(ITag.INamedTag<Block> tag : tags.keySet())
                tagsAmount.put(tag, 0);
        }

        public static void add(Block block, int boost, int max){
            blocks.put(block, new Point(boost, max));
        }

        public static void add(ITag.INamedTag<Block> tag , int boost, int max){
            tags.put(tag, new Point(boost, max));
        }

        public int getPower(Block block){
            Integer amount = blockAmount.get(block);
            if(amount == null){
                Point[] points = tags.values().toArray(new Point[0]);
                ArrayList<ITag.INamedTag<Block>> tag = new ArrayList<>(tags.keySet());
                int max = 0;
                ITag.INamedTag<Block> maxTag = null;
                for(int i = 0; i < tag.size(); i++){
                    if(block.is(tag.get(i))){
                        if(max < points[i].x && tagsAmount.get(tag.get(i)) != points[i].y){
                            maxTag = tag.get(i);
                            max = points[i].x;
                        }
                    }
                }
                if(maxTag != null){
                    tagsAmount.replace(maxTag, tagsAmount.get(maxTag)+1);
                    return max;
                }
            }
            if(amount == null)
                return 0;

            Point pair = blocks.get(block);
            if(amount == pair.y)
                return 0;
            else
            {
                blockAmount.replace(block, amount  + 1);
                return pair.x;
            }
        }
    }

    @SubscribeEvent
    public static void blockBroken(BlockEvent.BreakEvent event){
        ((Chunk)event.getWorld().getChunk(event.getPos())).getCapability(AltarLocationCapability.INSTANCE).ifPresent((source) -> {
            for(BlockPos altar : source.getAltars()){
                TileEntity te = event.getWorld().getBlockEntity(altar);
                if(te instanceof AltarTileEntity){
                    ((AltarTileEntity)te).queueRecalculation();
                }
            }
        });
    }

    @SubscribeEvent
    public static void blockPlaced(BlockEvent.EntityPlaceEvent event){
        ((Chunk)event.getWorld().getChunk(event.getPos())).getCapability(AltarLocationCapability.INSTANCE).ifPresent((source) -> {
            for(BlockPos altar : source.getAltars()){
                TileEntity te = event.getWorld().getBlockEntity(altar);
                if(te instanceof AltarTileEntity){
                    ((AltarTileEntity)te).queueRecalculation();
                }
            }
        });
    }


}
