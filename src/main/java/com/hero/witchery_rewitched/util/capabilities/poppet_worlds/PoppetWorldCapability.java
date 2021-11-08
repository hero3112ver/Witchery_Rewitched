package com.hero.witchery_rewitched.util.capabilities.poppet_worlds;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PoppetWorldCapability implements IPoppetWorlds, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IPoppetWorlds.class)
    public static Capability<IPoppetWorlds> INSTANCE = null;
    public static ResourceLocation NAME = new ResourceLocation(WitcheryRewitched.MODID, "poppet_worlds");

    private static final String NBT_WORLD = "WORLD";

    private final LazyOptional<IPoppetWorlds> holder = LazyOptional.of(() -> this);
    private final List<String> shelves = new ArrayList<>();

    public static void register(){
        CapabilityManager.INSTANCE.register(IPoppetWorlds.class, new PoppetWorldCapability.Storage(), PoppetWorldCapability::new);
    }

    @Override
    public List<String> getShelves() {
        return shelves;
    }

    @Override
    public void setShelves(List<String> shelves) {
        this.shelves.addAll(shelves);
    }

    @Override
    public void addShelfIfNotPresent(World world) {
        if(!shelves.contains(world.dimension().location().toString()))
            shelves.add(world.dimension().location().toString());
    }

    @Override
    public void removeShelfIfPresent(World world) {
        shelves.remove(world.dimension().location().toString());
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
        for(int i = 0; i < shelves.size(); i++){
            nbt.putString(NBT_WORLD+i, shelves.get(i));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int count = 0;
        while(nbt.contains(NBT_WORLD+count)){
            shelves.add(nbt.getString(NBT_WORLD+count));
            count++;
        }
    }

    private static class Storage implements Capability.IStorage<IPoppetWorlds>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<IPoppetWorlds> capability, IPoppetWorlds instance, Direction side) {
            if(instance instanceof PoppetWorldCapability){
                return ((PoppetWorldCapability) instance).serializeNBT();
            }
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<IPoppetWorlds> capability, IPoppetWorlds instance, Direction side, INBT nbt) {
            if(instance instanceof PoppetWorldCapability){
                ((PoppetWorldCapability) instance).deserializeNBT((CompoundNBT) nbt);
            }
        }
    }

    // Attached in Coven
}
