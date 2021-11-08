package com.hero.witchery_rewitched.util.capabilities.altar;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AltarLocationCapability implements IAltarLocations, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IAltarLocations.class)
    public static Capability<IAltarLocations> INSTANCE = null;
    public static ResourceLocation NAME = new ResourceLocation(WitcheryRewitched.MODID, "altar_locations");

    private static final String NBT_ALTARS = "ALTAR_LOCATIONS";
    private final LazyOptional<IAltarLocations> holder = LazyOptional.of(() -> this);

    private List<BlockPos> altars = new ArrayList<>();

    @Override
    public List<BlockPos> getAltars() {
        return altars;
    }

    @Override
    public void addAltar(BlockPos pos) {
        if(!altars.contains(pos))
            altars.add(pos);
    }

    @Override
    public void deleteAltar(BlockPos pos) {
        altars.remove(pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(INSTANCE == null)
            throw new IllegalStateException("Capabilities failed to initialize");
        return INSTANCE.orEmpty(cap, holder);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        for(int i = 0; i < altars.size(); i++){
            nbt.put(NBT_ALTARS + i, NBTUtil.writeBlockPos(altars.get(i)));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int i = 0;
        while(nbt.contains(NBT_ALTARS + i)){
            altars.add(NBTUtil.readBlockPos(nbt.getCompound(NBT_ALTARS+i)));
            i++;
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IAltarLocations.class, new Storage(), AltarLocationCapability::new);
    }

    private static class Storage implements Capability.IStorage<IAltarLocations>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<IAltarLocations> capability, IAltarLocations instance, Direction side) {
            if(instance instanceof AltarLocationCapability){
                return ((AltarLocationCapability) instance).serializeNBT();
            }
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<IAltarLocations> capability, IAltarLocations instance, Direction side, INBT nbt) {
            if(instance instanceof AltarLocationCapability){
                ((AltarLocationCapability) instance).deserializeNBT((CompoundNBT) nbt);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event){
        event.addCapability(NAME, new AltarLocationCapability());
    }

}
