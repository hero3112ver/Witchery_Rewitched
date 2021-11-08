package com.hero.witchery_rewitched.util.capabilities.player;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerCapability implements IPlayer, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IPlayer.class)
    public static Capability<IPlayer> INSTANCE = null;
    public static ResourceLocation NAME = new ResourceLocation(WitcheryRewitched.MODID, "player_id");

    private static final String NBT_PLAYER_ID = "PLAYER_ID";

    private final LazyOptional<IPlayer> holder = LazyOptional.of(() -> this);

    public UUID playerId;

    @Override
    public void setPlayerID(UUID id) {
        playerId = id;
    }

    @Override
    public UUID getPlayerID() {
        return playerId;
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
        if(playerId == null)
            return nbt;

        nbt.putUUID(NBT_PLAYER_ID, playerId);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains(NBT_PLAYER_ID))
            playerId = nbt.getUUID(NBT_PLAYER_ID);
    }

    public static void register(){
        CapabilityManager.INSTANCE.register(IPlayer.class, new PlayerCapability.Storage(), PlayerCapability::new);
    }

    public static boolean canAttachTo(ICapabilityProvider obj){
        return obj instanceof BedTileEntity && !obj.getCapability(INSTANCE).isPresent();
    }


    private static class Storage implements Capability.IStorage<IPlayer> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IPlayer> capability, IPlayer instance, Direction side) {
            if (instance instanceof PlayerCapability) {
                return ((PlayerCapability) instance).serializeNBT();
            }
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<IPlayer> capability, IPlayer instance, Direction side, INBT nbt) {
            if (instance instanceof PlayerCapability) {
                ((PlayerCapability) instance).deserializeNBT((CompoundNBT) nbt);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachTileEntityCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity te = event.getObject();
        if(canAttachTo(te)) {
            event.addCapability(NAME, new PlayerCapability());
        }
    }

    public static void playerSleepInBed(PlayerSleepInBedEvent event){
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();
        if(pos != null){
            event.getPlayer().level.getBlockEntity(pos).getCapability(INSTANCE).ifPresent(source -> source.setPlayerID(player.getUUID()));
        }
    }
}
