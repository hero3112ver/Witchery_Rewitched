package com.hero.witchery_rewitched;

import com.hero.witchery_rewitched.api.capabilities.altar.AltarLocationCapability;
import com.hero.witchery_rewitched.api.capabilities.witchery_data.WitcheryDataCapability;
import com.hero.witchery_rewitched.api.capabilities.player.PlayerCapability;
import com.hero.witchery_rewitched.api.capabilities.poppet_shelf.PoppetShelfCapability;
import com.hero.witchery_rewitched.api.capabilities.poppet_worlds.PoppetWorldCapability;
import com.hero.witchery_rewitched.block.altar.AltarTileEntity;
import com.hero.witchery_rewitched.block.plants.MandrakeBlock;
import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.data.DataGenerators;
import com.hero.witchery_rewitched.data.loot.GrassDropModifier;
import com.hero.witchery_rewitched.entity.ent.EntEntity;
import com.hero.witchery_rewitched.init.*;
import com.hero.witchery_rewitched.item.PoppetBase;
import com.hero.witchery_rewitched.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SideProxy implements IProxy{
    @Nullable private static MinecraftServer server;

    SideProxy(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(DataGenerators::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        WitcheryRewitchedConfig.initialize();

        registerEvent(SideProxy::serverStarted);
        registerEvent(SideProxy::serverStopping);
        registerEvent(PlayerCapability::playerSleepInBed);
        registerEvent(WitcheryDataCapability::cloneEvent);
        registerEvent(WitcheryDataCapability::onBlockBreak);
        registerEvent(EntEntity::damageEnt);
        registerEvent(MandrakeBlock::onBroken);
        registerEvent(PoppetBase::onDeath);
        registerEvent(PoppetBase::onItemDestroy);
        registerEvent(PoppetBase::onArmorBreak);
        registerEvent(ModEntities::registerAttributes);
        registerEvent(AltarTileEntity::blockBroken);
        registerEvent(AltarTileEntity::blockPlaced);

        registerGenericEvent(Entity.class, WitcheryDataCapability::onAttachEntityCapabilities);
        registerGenericEvent(TileEntity.class, PlayerCapability::onAttachTileEntityCapabilities);
        registerGenericEvent(World.class, PoppetShelfCapability::onAttachWorldCapabilities);
        registerGenericEvent(Chunk.class, AltarLocationCapability::onAttachChunkCapabilities);
        registerGenericEvent(GlobalLootModifierSerializer.class, GrassDropModifier.SeedDropModifier::registerModifiers);

        RegistryHandler.init();
        Network.init();
    }

    private <T extends Event> void registerEvent(Consumer<T> consumer){
        MinecraftForge.EVENT_BUS.addListener(consumer);
    }
    private <T extends GenericEvent<? extends F>, F> void registerGenericEvent(Class<F> c, Consumer<T> consumer){
        MinecraftForge.EVENT_BUS.addGenericListener(c, consumer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        WitcheryDataCapability.register();
        PlayerCapability.register();
        PoppetShelfCapability.register();
        PoppetWorldCapability.register();
        AltarLocationCapability.register();
        //TODO: Register compostable

        // LibHooks.registerCompostable(0.3f, INSERTITEMHERE);
    }

    private static void serverStarted(FMLServerStartedEvent event){
        server = event.getServer();
    }

    private static void serverStopping(FMLServerStoppingEvent event) {
        server = null;
    }

    @Nullable
    @Override
    public PlayerEntity getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    static class Client extends SideProxy{
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::blockColorSetup);
        }

        private static void clientSetup(FMLClientSetupEvent event) {
            ModBlocks.registerRenderTypes(event);
            ModEntities.registerRenderers(event);
            ModTileEntities.registerRenderers(event);
            ModContainers.registerScreens(event);
            ModItemModelProperties.register();
        }

        private static void blockColorSetup(ColorHandlerEvent.Block event){
            ModBlocks.registerBlockColors(event);
        }

        @Nullable
        @Override
        public PlayerEntity getClientPlayer() {
            return Minecraft.getInstance().player;
        }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {}
    }
}
