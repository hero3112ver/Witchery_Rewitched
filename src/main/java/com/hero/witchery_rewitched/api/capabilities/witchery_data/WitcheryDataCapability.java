package com.hero.witchery_rewitched.api.capabilities.witchery_data;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.api.capabilities.poppet_worlds.PoppetWorldCapability;
import com.hero.witchery_rewitched.data.ModBlockTagsProvider;
import com.hero.witchery_rewitched.entity.ent.EntEntity;
import com.hero.witchery_rewitched.init.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class WitcheryDataCapability implements IWitcheryData, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IWitcheryData.class)
    public static Capability<IWitcheryData> INSTANCE = null;
    public static ResourceLocation NAME = new ResourceLocation(WitcheryRewitched.MODID, "witchery_data");

    private static final String NBT_COVEN = "COVEN_SIZE";
    private static final String NBT_ENT_CHANCE = "ENT_CHANCE";

    private final LazyOptional<IWitcheryData> holder = LazyOptional.of(() -> this);

    private int covenSize;
    private int entChance;

    @Override
    public int getCovenSize() {
        return covenSize;
    }

    @Override
    public void setCovenSize(int size) {
        covenSize = size;
    }

    @Override
    public int getEntChance() {return entChance;}

    @Override
    public void setEntChance(int chance) {entChance = chance;}

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(NBT_COVEN, covenSize);
        nbt.putInt(NBT_ENT_CHANCE, entChance);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        covenSize = nbt.getInt(NBT_COVEN);
        entChance = nbt.getInt(NBT_ENT_CHANCE);
    }

    public void copyData(IWitcheryData source){
        this.entChance = source.getEntChance();
        this.covenSize = source.getCovenSize();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(INSTANCE == null)
            throw new IllegalStateException("Capabilities failed to initialize");
        return INSTANCE.orEmpty(cap, holder);
    }



    public static void register(){
        CapabilityManager.INSTANCE.register(IWitcheryData.class, new Storage(), WitcheryDataCapability::new);
    }

    public static boolean canAttachTo(ICapabilityProvider obj){
        return obj instanceof PlayerEntity && !obj.getCapability(INSTANCE).isPresent();
    }

    private static class Storage implements Capability.IStorage<IWitcheryData> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IWitcheryData> capability, IWitcheryData instance, Direction side) {
            if (instance instanceof WitcheryDataCapability) {
                return ((WitcheryDataCapability) instance).serializeNBT();
            }
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<IWitcheryData> capability, IWitcheryData instance, Direction side, INBT nbt) {
            if (instance instanceof WitcheryDataCapability) {
                ((WitcheryDataCapability) instance).deserializeNBT((CompoundNBT) nbt);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(canAttachTo(entity)) {
            event.addCapability(NAME, new WitcheryDataCapability());
            event.addCapability(PoppetWorldCapability.NAME, new PoppetWorldCapability());
        }
    }

    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event){
        event.getOriginal().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source ->
                event.getPlayer().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(dest ->
                        dest.copyData(source)));

        event.getOriginal().getCapability(PoppetWorldCapability.INSTANCE).ifPresent(source ->
                event.getPlayer().getCapability(PoppetWorldCapability.INSTANCE).ifPresent(dest ->
                        dest.setShelves(source.getShelves())));
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        if(event.getState().is(ModTags.Blocks.WITCH_LOG)) {
            AtomicInteger chance = new AtomicInteger();
            if (event.getPlayer() != null)
                event.getPlayer().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source -> chance.set(source.getEntChance()));
            else
                chance.set(1);

            if (event.getWorld().getRandom().nextInt(100) < chance.get()) {
                // Unchecked cast, need to make sure this doesn't break things
                World world = (World)event.getWorld();
                BlockPos pos = event.getPos();
                
                int dist = world.getRandom().nextInt(16) + 16;
                int angle = world.getRandom().nextInt(360);
                int y = pos.getY();
                
                while(world.getBlockState(pos.offset(dist * Math.cos(angle), y,dist * Math.sin(angle))).getBlock() != Blocks.AIR){
                    y++;
                }
                BlockPos newPos = pos.offset(dist * Math.cos(angle), y,dist * Math.sin(angle));
                EntEntity trent = new EntEntity(world);
                trent.setPos(newPos.getX(), newPos.getY(), newPos.getZ());
                world.addFreshEntity(trent);
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
                lightningboltentity.moveTo(Vector3d.atBottomCenterOf(newPos));
                lightningboltentity.setVisualOnly(true);
                world.addFreshEntity(lightningboltentity);
                if(event.getPlayer() != null)
                    event.getPlayer().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source -> source.setEntChance(1));
            }
            else if (event.getPlayer() != null) {
                event.getPlayer().getCapability(WitcheryDataCapability.INSTANCE).ifPresent(source -> source.setEntChance(chance.get() + 1));
            }
        }
    }
}

