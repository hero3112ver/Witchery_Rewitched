package com.hero.witchery_rewitched.block.altar;

import com.hero.witchery_rewitched.util.AltarPowerer;
import com.hero.witchery_rewitched.util.capabilities.altar.AltarLocationCapability;
import com.hero.witchery_rewitched.util.capabilities.altar.IAltarLocations;
import com.hero.witchery_rewitched.block.INamedContainerExtraData;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModTags;
import com.hero.witchery_rewitched.init.ModTileEntities;
import com.hero.witchery_rewitched.util.listeners.AltarPowererReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class AltarTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerExtraData {

    private int rechargeRate;
    private int maxEnergy;
    private int energy;
    private int config;
    public int recalculateTimer = 0;
    private AltarPowerer powerer = null;

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
        List<Block> blocks = getBlocksAboveAltar();
        maxEnergy = powerer.calculateMaxEnergy(blocks);
        rechargeRate = powerer.calculateRechargeRate(blocks);
        recalculateTimer = 0;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (!level.getBlockState(worldPosition).getValue(AltarBlock.MULTIBLOCK_FORMED))
            return;

        if(powerer == null)
            powerer = new AltarPowerer(this.worldPosition, this.level, getBlocksAboveAltar());
        if (energy < maxEnergy)
            energy += rechargeRate;
        else if (energy > maxEnergy)
            energy = maxEnergy;


        if(recalculateTimer <= 60) recalculateTimer++;
    }

    public boolean takePower(int amount){
        if(amount <= energy) {
            energy-= amount;
            return true;
        }
        return false;
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



    @SubscribeEvent
    public static void blockBroken(BlockEvent.BreakEvent event){
        ((Chunk)event.getWorld().getChunk(event.getPos())).getCapability(AltarLocationCapability.INSTANCE).ifPresent((source) -> {
            for(BlockPos altar : source.getAltars()){
                TileEntity te = event.getWorld().getBlockEntity(altar);
                if(te instanceof AltarTileEntity && ((AltarTileEntity) te).powerer != null){
                    ((AltarTileEntity)te).powerer.markDirty(event.getPos(), true, event.getState().getBlock(), ((AltarTileEntity) te).getBlocksAboveAltar());
                }
            }
        });
    }

    @SubscribeEvent
    public static void blockPlaced(BlockEvent.EntityPlaceEvent event){
        ((Chunk)event.getWorld().getChunk(event.getPos())).getCapability(AltarLocationCapability.INSTANCE).ifPresent((source) -> {
            for(BlockPos altar : source.getAltars()){
                TileEntity te = event.getWorld().getBlockEntity(altar);
                if(te instanceof AltarTileEntity && ((AltarTileEntity) te).powerer != null){
                    ((AltarTileEntity)te).powerer.markDirty(event.getPos(), false, event.getPlacedBlock().getBlock(), ((AltarTileEntity) te).getBlocksAboveAltar());
                }
            }
        });
    }


}
