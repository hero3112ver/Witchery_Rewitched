package com.hero.witchery_rewitched.util.capabilities.poppet_shelf;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfTileEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
import java.util.Objects;

public class PoppetShelfCapability implements  IPoppetShelf, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IPoppetShelf.class)
    public static Capability<IPoppetShelf> INSTANCE = null;
    public static ResourceLocation NAME = new ResourceLocation(WitcheryRewitched.MODID, "poppet_shelf");

    private static final String NBT_POPPET_SHELF = "SHELF";

    private final LazyOptional<IPoppetShelf> holder = LazyOptional.of(() -> this);
    private final List<Pair<GameProfile, BlockPos>> shelves = new ArrayList<>();

    @Override
    public List<Pair<GameProfile, BlockPos>> getShelves() {
        return shelves;
    }

    @Override
    public void addShelfIfNotPresent(PlayerEntity player, BlockPos pos) {
        if(!shelves.contains(new Pair(player.getGameProfile(), pos))){
            shelves.add(new Pair(player.getGameProfile(), pos));
        }
    }

    @Override
    public void removeShelfIfPresent(BlockPos pos) {
        for(int i = 0; i < shelves.size(); i++){
            if(shelves.get(i).getSecond().equals(pos))
                shelves.remove(i);
        }
    }

    public static void register(){
        CapabilityManager.INSTANCE.register(IPoppetShelf.class, new PoppetShelfCapability.Storage(), PoppetShelfCapability::new);
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
            nbt.put(NBT_POPPET_SHELF+"_PLAYER_"+i, NBTUtil.writeGameProfile( new CompoundNBT() ,shelves.get(i).getFirst()));
            nbt.put(NBT_POPPET_SHELF+"_POS_"+i, NBTUtil.writeBlockPos(shelves.get(i).getSecond()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for(int i = 0; i < PoppetShelfTileEntity.INVENTORY_SIZE; i++){
            if(nbt.contains(NBT_POPPET_SHELF+"_POS_"+i)) {
                shelves.add(new Pair(NBTUtil.readGameProfile((CompoundNBT) Objects.requireNonNull(nbt.get(NBT_POPPET_SHELF + "_PLAYER_" + i))) ,NBTUtil.readBlockPos((CompoundNBT) Objects.requireNonNull(nbt.get(NBT_POPPET_SHELF + "_POS_" + i)))));
            }
        }
    }

    private static class Storage implements Capability.IStorage<IPoppetShelf>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<IPoppetShelf> capability, IPoppetShelf instance, Direction side) {
            if(instance instanceof  PoppetShelfCapability){
                return ((PoppetShelfCapability) instance).serializeNBT();
            }
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<IPoppetShelf> capability, IPoppetShelf instance, Direction side, INBT nbt) {
            if(instance instanceof PoppetShelfCapability){
                ((PoppetShelfCapability) instance).deserializeNBT((CompoundNBT) nbt);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event){
        event.addCapability(NAME, new PoppetShelfCapability());
    }
}
